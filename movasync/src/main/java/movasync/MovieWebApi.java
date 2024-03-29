/*
 * Copyright (c) 2018 Miguel Gamboa
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
import util.IRequest;

import com.google.gson.Gson;
import java.io.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;

/**
 * @author Miguel Gamboa
 * created on 16-02-2017
 */

public class MovieWebApi {
    /**
     * Constants
     * <p>
     * To format messages URLs use {@link MessageFormat#format(String, Object...)} method.
     */
    private static final String API_KEY;
    private static final String MOVIE_DB_HOST = "https://api.themoviedb.org/3/";
    private static final String MOVIE_DB_SEARCH = "search/movie?api_key={0}&query={1}&page={2}";
    private static final String MOVIE_DB_MOVIE = "movie/{1,number,#}?api_key={0}";
    private static final String MOVIE_DB_MOVIE_CREDITS = "movie/{1,number,#}/credits?api_key={0}";
    private static final String MOVIE_DB_PERSON = "person/{1,number,#}?api_key={0}";
    private static final String MOVIE_DB_PERSON_CREDITS = "person/{1,number,#}/movie_credits?api_key={0}";

    static {
        try {
            URL keyFile = ClassLoader.getSystemResource("apiKey.txt");
            if (keyFile == null) {
                throw new IllegalStateException(
                        "YOU MUST GET a KEY in developers.themoviedb.org/3 and place it in src/main/resources/apiKey.txt");
            } else {
                InputStream keyStream = keyFile.openStream();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(keyStream))) {
                    API_KEY = reader.readLine();
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private final IRequest req;
    private final Gson gson;

    /*
     * Constructors
     */
    public MovieWebApi(IRequest req) {
        this.req = req;
        this.gson = new Gson();
    }

    /**
     * E.g. https://api.themoviedb.org/3/search/movie?api_key=***************&query=war+games&page=1
     */
    public CompletableFuture<SearchDto> search(String title, int page) {
        String path = MessageFormat.format(MOVIE_DB_HOST + MOVIE_DB_SEARCH, API_KEY, title.replace(" ", "+"), page);
        return httpGet(path, SearchDto.class);
    }

    /**
     * E.g. https://api.themoviedb.org/3/movie/860?api_key=***************
     */
    public CompletableFuture<MovieDto> getMovie(int id) {
        String path = MessageFormat.format(MOVIE_DB_HOST + MOVIE_DB_MOVIE, API_KEY, id);
        return httpGet(path, MovieDto.class);
    }

    /**
     * E.g. https://api.themoviedb.org/3/movie/860/credits?api_key=***************
     */
    public CompletableFuture<MovieCreditsDto> getMovieCredits(int movieId) {
        String path = MessageFormat.format(MOVIE_DB_HOST + MOVIE_DB_MOVIE_CREDITS, API_KEY, movieId);
        return httpGet(path, MovieCreditsDto.class);
    }

    /**
     * E.g. https://api.themoviedb.org/3/person/4756?api_key=***************
     */
    public CompletableFuture<PersonDto> getPerson(int personId) {
        String path = MessageFormat.format(MOVIE_DB_HOST + MOVIE_DB_PERSON, API_KEY, personId);
        return httpGet(path, PersonDto.class);
    }

    /**
     * E.g. https://api.themoviedb.org/3/person/4756/movie_credits?api_key=***************
     */
    public CompletableFuture<SearchItemDto[]> getPersonCreditsCast(int personId) {
        String path = MessageFormat.format(MOVIE_DB_HOST + MOVIE_DB_PERSON_CREDITS, API_KEY, personId);
        return httpGet(path, PersonCreditsDto.class)
                .thenApply(PersonCreditsDto::getCast);
    }

    private <T> CompletableFuture<T> httpGet(String path, Class<T> klass) {
        return req
                .getBody(path)
                .thenApply(body -> gson.fromJson(body, klass));
    }

}