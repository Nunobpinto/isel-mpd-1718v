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

package movlazy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import movlazy.model.Credit;
import util.Queries;

import movlazy.dto.*;
import movlazy.model.Person;
import movlazy.model.Movie;
import movlazy.model.SearchItem;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Miguel Gamboa
 * created on 02-03-2017
 */
public class MovieService {

    private final MovieWebApi movieWebApi;
    private final Map<Integer, Movie> movies = new HashMap<>();
    private final Map<Integer, Supplier<Stream<Credit>>> credit = new HashMap<>();
    private final Map<Integer, Person> person = new HashMap<>();

    public MovieService(MovieWebApi movieWebApi) {
        this.movieWebApi = movieWebApi;
    }

    public Supplier<Stream<SearchItem>> search(String name) {
        return () ->
                Queries.takeWhile(
                        Stream.iterate(1, prev -> ++prev).map(page -> movieWebApi.search(name, page)),
                        movs -> movs.length != 0
                )
                        .flatMap(Stream::of)
                        .map(this::parseSearchItemDto);
    }

    //TODO
    public Supplier<Stream<Credit>> getPersonCredits(int actorId) {
       /* return credit.computeIfAbsent(actorId, id ->
                Cache.of(
                        () -> Stream.of(movieWebApi.getPersonCredits(actorId)).methodToConcatSeq()
                )
        );*/
        return null;
    }

    public Movie getMovie(int movId) {
        return movies.computeIfAbsent(movId, id -> {
            MovieDto mov = movieWebApi.getMovie(id);
            return parseMovieDto(mov);
        });
    }

    //TODO
    public Supplier<Stream<Credit>> getMovieCredits(int movId) {
        return credit.computeIfAbsent(movId, id -> {
            MovieCreditsDto dto = movieWebApi.getMovieCredits(id);

            List<Credit> l = new ArrayList<>();
            //boolean [] b = new boolean[dto.getCrew().length];

            for (CastItemDto castItemDto : dto.getCast()) {
                l.add(parseCreditItemDto(castItemDto, movId));
            }

            for (CrewItemDto crewItemDto : dto.getCrew()) {
                Credit credit = parseCrewItemDto(crewItemDto, movId);
                for (Credit c : l) {
                    if (c.getId() == credit.getId()){
                        c.setDepartment(crewItemDto.getDepartment());
                        c.setJob(crewItemDto.getJob());
                    }
                    l.add(credit);
                }
            }
            return l::stream;
//            return () -> Stream
//                    .of(dto.getCast())
//                    .map(castItemDto -> parseCreditItemDto(castItemDto, movId))
//                    .;
        });
    }

    public Person getPerson(int actorId, String name) {
        return person.computeIfAbsent(actorId, id -> {
            PersonDto personDto = movieWebApi.getPerson(actorId);
            return parsePersonDto(personDto);
        });
    }

    //TODO
    private Person parsePersonDto(PersonDto dto) {
        return new Person(
                dto.getId(),
                dto.getName(),
                dto.getPlace_of_birth(),
                dto.getBiography(),
                null
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

    //TODO
    private Credit parseCreditItemDto(CastItemDto dto, int movId) {
        return new Credit(
                dto.getId(),
                movId,
                null,
                null,
                dto.getCharacter(),
                dto.getName(),
                () -> getPerson(dto.getId(), "")
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
                null
        );
    }
}
