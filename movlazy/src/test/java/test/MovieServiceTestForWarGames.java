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

package test;

import com.google.common.util.concurrent.RateLimiter;
import movlazy.MovieService;
import movlazy.MovieWebApi;
import movlazy.dto.SearchItemDto;
import movlazy.model.Credit;
import movlazy.model.SearchItem;
import org.junit.jupiter.api.Test;
import util.FileRequest;
import util.IRequest;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class MovieServiceTestForWarGames {

    @Test
    public void testSearchMovieInSinglePage() {
        MovieService movieApi = new MovieService(new MovieWebApi(new FileRequest().compose(System.out::println)));
        Supplier<Stream<SearchItem>> movies = movieApi.search("War Games");
        SearchItem m = movies.get().findFirst().get();
        assertEquals("War Games: The Dead Code", m.getTitle());
        assertEquals(6, movies.get().count());// number of returned movies
    }

    @Test
    public void testSearchMovieManyPages() {
        int[] count = {0};
        IRequest req = new FileRequest()
                .compose(System.out::println)
                .compose(__ -> count[0]++);

        MovieService movieApi = new MovieService(new MovieWebApi(req));
        Supplier<Stream<SearchItem>> movies = movieApi.search("candle");
        assertEquals(0, count[0]);
        SearchItem candleshoe = movies.get()
                .filter(m -> m.getTitle().equals("Candleshoe"))
                .findFirst()
                .get();
        assertEquals(2, count[0]); // Found on 2nd page
        assertEquals(59, movies.get().count());// Number of returned movies
        assertEquals(6, count[0]); // 4 requests more to consume all pages
    }
/*
    @Test
    public void testMovieDbApiGetActor() {
        int[] count = {0};
        IRequest req = new FileRequest()
                .compose(System.out::println)
                .compose(__ -> count[0]++);

        MovieWebApi movieServiceApi = new MovieWebApi(req);
        SearchItemDto [] actorMovs = movieServiceApi.getPersonCreditsCast(4756);
        assertNotNull(actorMovs);
        assertEquals("Inspector Gadget", actorMovs[0].getTitle());
        assertEquals(1, count[0]); // 1 request
    }
*/
    @Test
    public void testSearchMovieThenActorsThenMoviesAgain() {
        final RateLimiter rateLimiter = RateLimiter.create(3.0);
        final int[] count = {0};
        IRequest req = new FileRequest()
                .compose(__ -> count[0]++)
                .compose(System.out::println)
                .compose(__ -> rateLimiter.acquire());

        MovieService movieService = new MovieService(new MovieWebApi(req));

        Supplier<Stream<SearchItem>> movies = movieService.search("War Games");
        assertEquals(6, movies.get().count());// number of returned movies
        assertEquals(2, count[0]);         // 2 requests to consume all pages
        /**
         * Iterable<SearchItem> is Lazy and without cache.
         */
        SearchItem warGames = movies.get()
                .filter(m -> m.getTitle().equals("WarGames"))
                .findFirst()
                .get();
        assertEquals(3, count[0]); // 1 more request for 1st page
        assertEquals(860, warGames.getId());
        assertEquals("WarGames", warGames.getTitle());
        assertEquals(3, count[0]); // Keep the same number of requests
        /**
         * getDetails() relation SearchItem ---> Movie is Lazy and supported on Supplier<Movie> with Cache
         */
        assertEquals("WarGames", warGames.getDetails().getOriginalTitle());
        assertEquals(5, count[0]); // 1 more request to get the Movie
        assertEquals("Is it a game, or is it real?", warGames.getDetails().getTagline());
        assertEquals(5, count[0]); // NO more request. It is already in cache
        /**
         * getCredits() relation Movie --->* Credit is Lazy and
         * supported on Supplier<List<Credit>> with Cache
         */
        Supplier<Stream<Credit>> warGamesCast = warGames.getDetails().getCredits();
        assertEquals(5, count[0]); // No requests to get the Movie Cast => It is Lazy
        assertEquals("Matthew Broderick",
                warGamesCast.get().findFirst().get().getName());
        assertEquals(5, count[0]); // 1 more request for warGamesCast.get().
        Stream <Credit> iter = warGamesCast.get().skip(2);
        assertEquals("Ally Sheedy",
                iter.findFirst().get().getName());
        assertEquals(5, count[0]); // NO more request. It is already in cache
        /**
         * Credit ---> Person is Lazy and with Cache for Person but No cache for actor credits
         */
        Credit broderick = warGames.getDetails().getCredits().get().findFirst().get();
        assertEquals(5, count[0]); // NO more request. It is already in cache
        assertEquals("New York City, New York, USA",
                broderick.getDetails().getPlaceOfBirth());
        assertEquals(6, count[0]); // 1 more request for Person Person
        assertEquals("New York City, New York, USA",
                broderick.getDetails().getPlaceOfBirth());
        assertEquals(6, count[0]); // NO more request. It is already in cache
        assertEquals("Inspector Gadget",
                broderick.getDetails().getMovies().get().findFirst().get().getTitle());
        assertEquals(7, count[0]); // 1 more request for Person Credit
        assertEquals("Inspector Gadget",
                broderick.getDetails().getMovies().get().findFirst().get().getTitle());
        assertEquals(8, count[0]); // 1 more request. Person Cast is not in cache

        /**
         * Check Cache from the beginning
         */
        assertEquals("New York City, New York, USA",
                movieService.getMovie(860).getCredits().get().findFirst().get().getDetails().getPlaceOfBirth());
        assertEquals(8, count[0]); // No more requests for the same getMovie.
        /*
         * Now get a new Film
         */
        assertEquals("Predator",
                movieService.getMovie(861).getCredits().get().findFirst().get().getDetails().getMovies().get().findFirst().get().getTitle());
        assertEquals(12, count[0]); // 1 request for Movie + 1 for CastItems + 1 Person + 1 Person Credit
    }

    @Test
    public void testSearchMovieWithManyPages() {
        final RateLimiter rateLimiter = RateLimiter.create(3.0);
        final int[] count = {0};
        IRequest req = new FileRequest()
                .compose(__ -> count[0]++)
                .compose(System.out::println)
                .compose(__ -> rateLimiter.acquire());

        MovieService movapi = new MovieService(new MovieWebApi(req));

        Stream<SearchItem> vs = movapi.search("fire").get();
        assertEquals(1170, vs.count());// number of returned movies
        assertEquals(60, count[0]);    // 2 requests to consume all pages
    }
}
