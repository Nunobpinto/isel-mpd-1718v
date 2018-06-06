/*
 * Copyright (c) 2017, Miguel Gamboa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package movasync;

import movasync.dto.*;
import movasync.model.SearchItem;
import movlazy.dto.*;
import movasync.model.Credit;
import movasync.model.Person;
import movasync.model.Movie;
import util.Cache;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static util.QueriesSpliterator.joinSeq;
import static util.QueriesSpliterator.takeWhile;

/**
 * @author Miguel Gamboa
 * created on 02-03-2017
 */
public class MovieService {

    private final MovieWebApi movieWebApi;
    private final Map<Integer, Movie> movieCache = new HashMap<>();
    private final Map<Integer, Supplier<Stream<Credit>>> creditCache = new HashMap<>();
    private final Map<Integer, Person> personCache = new HashMap<>();

    public MovieService(MovieWebApi movieWebApi) {
        this.movieWebApi = movieWebApi;
    }

    public Supplier<Stream<SearchItem>> search(String name) {
        return () ->
                takeWhile(
                        Stream.iterate(1, prev -> ++prev).map(page -> movieWebApi.search(name, page)),
                        movs -> movs.length != 0
                )
                        .flatMap(Stream::of)
                        .map(this::parseSearchItemDto);
    }

    public Supplier<Stream<SearchItem>> getPersonCreditsCast(int actorId) {
        return () ->
                Stream.of(movieWebApi.getPersonCreditsCast(actorId)).map(this::parseSearchItemDto);
    }

    public Movie getMovie(int movId) {
        return movieCache.computeIfAbsent(movId, id -> {
            MovieDto mov = movieWebApi.getMovie(id);
            return parseMovieDto(mov);
        });
    }

    public Supplier<Stream<Credit>> getMovieCredits(int movId) {
        return creditCache.computeIfAbsent(movId, id ->
                Cache.of(
                        () -> {
                            MovieCreditsDto dto = movieWebApi.getMovieCredits(id);
                            Stream<Credit> castStream = Stream.of(dto.getCast()).map(item -> parseCasttItemDto(item, movId));
                            Stream<Credit> crewStream = Stream.of(dto.getCrew()).map(item -> parseCrewItemDto(item, movId));
                            return joinSeq(
                                    castStream,
                                    crewStream.collect(Collectors.toList()),
                                    (item, list) -> {
                                        Optional<Credit> optionalCredit = list.stream().filter(i -> i.getId() == item.getId()).findFirst();
                                        if (optionalCredit.isPresent()) {
                                            Credit credit = optionalCredit.get();
                                            item.setDepartment(credit.getDepartment());
                                            item.setJob(credit.getJob());
                                            list.remove(credit);
                                        }
                                    }
                            );
                        }
                )
        );
    }

    public Person getPerson(int actorId) {
        return personCache.computeIfAbsent(actorId, id -> {
            PersonDto personDto = movieWebApi.getPerson(actorId);
            return parsePersonDto(personDto);
        });
    }

    private Person parsePersonDto(PersonDto dto) {
        return new Person(
                dto.getId(),
                dto.getName(),
                dto.getPlace_of_birth(),
                dto.getBiography(),
                getPersonCreditsCast(dto.getId())
        );
    }

    private SearchItem parseSearchItemDto(SearchItemDto dto) {
        return new SearchItem(
                dto.getId(),
                dto.getTitle(),
                dto.getReleaseDate(),
                dto.getVoteAverage(),
                () -> getMovie(dto.getId())
        );
    }

    private Movie parseMovieDto(MovieDto dto) {
        return new Movie(
                dto.getId(),
                dto.getOriginalTitle(),
                dto.getTagline(),
                dto.getOverview(),
                dto.getVoteAverage(),
                dto.getReleaseDate(),
                this.getMovieCredits(dto.getId())

        );
    }

    private Credit parseCasttItemDto(CastItemDto dto, int movId) {
        return new Credit(
                dto.getId(),
                movId,
                null,
                null,
                dto.getCharacter(),
                dto.getName(),
                () -> getPerson(dto.getId())
        );
    }

    private Credit parseCrewItemDto(CrewItemDto dto, int movId) {
        return new Credit(
                dto.getId(),
                movId,
                dto.getDepartment(),
                dto.getJob(),
                null,
                dto.getName(),
                () -> getPerson(dto.getId())
        );
    }

}