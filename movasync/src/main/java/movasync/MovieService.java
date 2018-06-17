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
import movasync.model.Credit;
import movasync.model.Person;
import movasync.model.Movie;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static util.QueriesSpliterator.joinSeq;

/**
 * @author Miguel Gamboa
 * created on 02-03-2017
 */
public class MovieService {

    private final MovieWebApi movieWebApi;
    private final Map<Integer, CompletableFuture<Movie>> movieCache = new ConcurrentHashMap<>();
    private final Map<Integer, CompletableFuture<List<Credit>>> creditCache = new ConcurrentHashMap<>();
    private final Map<Integer, CompletableFuture<Person>> personCache = new ConcurrentHashMap<>();

    public MovieService(MovieWebApi movieWebApi) {
        this.movieWebApi = movieWebApi;
    }

    public CompletableFuture<Stream<SearchItem>> search(String name) {
        return movieWebApi.search(name, 1)
                .thenApply(searchDto -> {
                    int totalPages = searchDto.getTotalPages();
                    List<CompletableFuture<SearchDto>> searchCp = new ArrayList<>();
                    searchCp.add(CompletableFuture.completedFuture(searchDto));
                    int i = 2;
                    while (i <= totalPages)
                        searchCp.add(movieWebApi.search(name, i++));
                    return searchCp.stream();
                })
                .thenApply(stream -> stream.collect(Collectors.toList()).stream())
                .thenApply(stream -> stream.map(CompletableFuture::join))
                .thenApply(stream -> stream.flatMap(searchDto -> Arrays.stream(searchDto.getResults()).map(this::parseSearchItemDto)));
    }

    public CompletableFuture<Stream<SearchItem>> getPersonCreditsCast(int actorId) {
        return movieWebApi.getPersonCreditsCast(actorId)
                .thenApply(Stream::of)
                .thenApply(strm -> strm.map(this::parseSearchItemDto));
    }

    public CompletableFuture<Movie> getMovie(int movId) {
        return movieCache.computeIfAbsent(movId, id ->
                movieWebApi.getMovie(id)
                        .thenApply(this::parseMovieDto)
        );
    }

    public CompletableFuture<Stream<Credit>> getMovieCredits(int movId) {
        return creditCache.computeIfAbsent(movId, id -> {
                    CompletableFuture<MovieCreditsDto> movieCreditsDto = movieWebApi.getMovieCredits(id);
                    CompletableFuture<Stream<Credit>> crewStream = movieCreditsDto
                            .thenApply(MovieCreditsDto::getCrew)
                            .thenApply(Stream::of)
                            .thenApply(strm -> strm.map(item -> parseCrewItemDto(item, movId)));
                    return movieCreditsDto
                            .thenApply(MovieCreditsDto::getCast)
                            .thenApply(Stream::of)
                            .thenApply(stream -> stream.map(item -> parseCastItemDto(item, movId)))
                            .thenApply(stream -> joinSeq(
                                    stream,
                                    crewStream.join().collect(Collectors.toList()),
                                    (item, list) -> {
                                        Optional<Credit> optionalCredit = list.stream().filter(i -> i.getId() == item.getId()).findFirst();
                                        if (optionalCredit.isPresent()) {
                                            Credit credit = optionalCredit.get();
                                            item.setDepartment(credit.getDepartment());
                                            item.setJob(credit.getJob());
                                            list.remove(credit);
                                        }
                                    }
                            ))
                            .thenApply(stream -> stream.collect(Collectors.toList()));
                }
        ).thenApply(Collection::stream);
    }

    public CompletableFuture<Person> getPerson(int actorId) {
        return personCache.computeIfAbsent(actorId, id ->
                movieWebApi.getPerson(actorId)
                        .thenApply(this::parsePersonDto)
        );
    }

    private Person parsePersonDto(PersonDto dto) {
        return new Person(
                dto.getId(),
                dto.getName(),
                dto.getPlace_of_birth(),
                dto.getBiography(),
                getPersonCreditsCast(dto.getId()),
                dto.getProfilePath()
        );
    }

    private SearchItem parseSearchItemDto(SearchItemDto dto) {
        return new SearchItem(
                dto.getId(),
                dto.getTitle(),
                dto.getReleaseDate(),
                dto.getVoteAverage(),
                getMovie(dto.getId())
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
                dto.getPoster_path(),
                getMovieCredits(dto.getId())
        );
    }

    private Credit parseCastItemDto(CastItemDto dto, int movId) {
        return new Credit(
                dto.getId(),
                movId,
                null,
                null,
                dto.getCharacter(),
                dto.getName(),
                getPerson(dto.getId())
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
                getPerson(dto.getId())
        );
    }

}