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

import java.util.HashMap;
import java.util.Map;

import movlazy.dto.*;
import movlazy.model.Actor;
import movlazy.model.CastItem;
import movlazy.model.Movie;
import movlazy.model.SearchItem;
import util.Cache;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Miguel Gamboa
 * created on 02-03-2017
 */
public class MovieService {

    private final MovieWebApi movieWebApi;
    private final Map<Integer, Movie> movies = new HashMap<>();
    private final Map<Integer, Supplier<Stream<CastItem>>> cast = new HashMap<>();
    private final Map<Integer, Actor> actors = new HashMap<>();

    public MovieService(MovieWebApi movieWebApi) {
        this.movieWebApi = movieWebApi;
    }

    public Supplier<Stream<SearchItem>> search(String name) {
        return () -> Stream.iterate(0, prev -> ++prev)
                .map(page -> movieWebApi.search(name, page))

                .takeWhile(movs -> movs.length != 0)
                .flatMap(Stream::of)
                .map(this::parseSearchItemDto);

//        return map(                     // Iterable<SearchItem>
//                this::parseSearchItemDto,
//                flatMap(             // Iterable<SearchItemDto>
//                        Queries::of,
//                        takeWhile(       // Iterable<SearchItemDto[]>
//                                movs -> movs.length != 0,
//                                map(         // Iterable<SearchItemDto[]>
//                                        page -> movieWebApi.search(name, page),
//                                        iterate( // Iterable<Integer>
//                                                0,
//                                                prev -> ++prev)
//
//                                )
//                        )
//                )
//        );
    }

    public Supplier<Stream<SearchItem>> getPersonCreditsCast(int actorId) {
        return () -> Stream.of(movieWebApi.getPersonCreditsCast(actorId)).map(this::parseSearchItemDto);
    }

    public Movie getMovie(int movId) {
        return movies.computeIfAbsent(movId, id -> {
            MovieDto mov = movieWebApi.getMovie(id);
            return parseMovieDto(mov);
        });
    }

    public Supplier<Stream<CastItem>> getMovieCast(int movId) {
        return cast.computeIfAbsent(movId, id ->
                Cache.of(
                        () -> Stream.of(movieWebApi.getMovieCast(id))
                                .map(dto -> parseCastItemDto(dto, id))
                ));
    }

    public Actor getActor(int actorId, String name) {
        return actors.computeIfAbsent(actorId, id -> {
            PersonDto personDto = movieWebApi.getPerson(actorId);
            return parsePersonDto(personDto);
        });
    }

    private Actor parsePersonDto(PersonDto dto) {
        return new Actor(
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
                this.getMovieCast(dto.getId())

        );
    }

    private CastItem parseCastItemDto(CastItemDto dto, int movId) {
        return new CastItem(
                dto.getId(),
                movId,
                dto.getCharacter(),
                dto.getName(),
                () -> getActor(dto.getId(), "")
        );
    }
}
