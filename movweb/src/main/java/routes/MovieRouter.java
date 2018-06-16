package routes;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.FaviconHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.TemplateEngine;
import io.vertx.ext.web.templ.HandlebarsTemplateEngine;
import movasync.MovieService;
import movasync.MovieWebApi;
import util.HttpRequest;

import java.security.cert.CollectionCertStoreParameters;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MovieRouter {

    private final MovieService movie;
    private final TemplateEngine engine = HandlebarsTemplateEngine.create();

    public MovieRouter(MovieService movie) {
        this.movie = movie;
    }

    public static Router router(Vertx vertx) {
        return router(vertx, new MovieService(new MovieWebApi(new HttpRequest())));
    }

    public static Router router(Vertx vertx, MovieService movie) {
        Router router = Router.router(vertx);
        MovieRouter handlers = new MovieRouter(movie);
        //router.route().handler(FaviconHandler.create("movies.ico"));
        router.route("/home").handler(handlers::home);
        router.route("/movies").handler(handlers::movieSearch);
        router.route("/movies/:id").handler(handlers::movieDetails);
        router.route("/movies/:id/credits").handler(handlers::movieCredits);
        router.route("/person/:id").handler(handlers::personDetails);
        router.route("/person/:id/movies").handler(handlers::personMovies);
        return router;
    }

    private void home(RoutingContext routingContext) {
        HttpServerRequest req = routingContext.request();
        HttpServerResponse res = routingContext.response();
        res.putHeader("content-type", "text/html");
        engine.render(routingContext, "templates", "/home.hbs", view -> {
            if (view.succeeded())
                res.end(view.result());
            else routingContext.fail(view.cause());
        });
    }

    private void movieSearch(RoutingContext routingContext) {
        HttpServerRequest req = routingContext.request();
        HttpServerResponse res = routingContext.response();
        res.putHeader("content-type", "text/html");
        String name = req.getParam("name");
        movie
                .search(name)
                .thenAccept(strm -> {
                    routingContext.put("movieList", strm.toArray());
                    engine.render(routingContext, "templates", "/searchedMovies.hbs", view -> {
                        if (view.succeeded())
                            res.end(view.result());
                        else routingContext.fail(view.cause());
                    });
                });
    }

    private void movieDetails(RoutingContext routingContext) {
        HttpServerRequest req = routingContext.request();
        HttpServerResponse res = routingContext.response();
        res.putHeader("content-type", "text/html");
        int movieId = Integer.parseInt(req.getParam("id"));
        movie
                .getMovie(movieId)
                .thenAccept(m -> {
                    m.getCredits()
                            .thenAccept(strmCredits -> {
                                routingContext.put("movie", m);
                                routingContext.put("movieCredits", strmCredits.collect(Collectors.toList()));
                                engine.render(routingContext, "templates", "/movieDetails.hbs", view -> {
                                    if (view.succeeded())
                                        res.end(view.result());
                                    else routingContext.fail(view.cause());
                                });
                            });
                });
    }

    private void movieCredits(RoutingContext routingContext) {
        HttpServerRequest req = routingContext.request();
        HttpServerResponse res = routingContext.response();
        res.putHeader("content-type", "text/html");
        int movieId = Integer.parseInt(req.getParam("id"));
        movie
                .getMovieCredits(movieId)
                .thenAccept(credits -> {
                    routingContext.put("credits", credits);
                    engine.render(routingContext, "templates", "/movieCredits.hbs", view -> {
                        if (view.succeeded())
                            res.end(view.result());
                        else routingContext.fail(view.cause());
                    });
                });
    }

    private void personDetails(RoutingContext routingContext) {
        HttpServerRequest req = routingContext.request();
        HttpServerResponse res = routingContext.response();
        res.putHeader("content-type", "text/html");
        int personId = Integer.parseInt(req.getParam("id"));
        movie
                .getPerson(personId)
                .thenAccept(person -> {
                    person.getMovies().thenAccept(strmMovies -> {
                        routingContext.put("person", person);
                        routingContext.put("personMovies", strmMovies.collect(Collectors.toList()));
                        engine.render(routingContext, "templates", "/personDetails.hbs", view -> {
                            if (view.succeeded())
                                res.end(view.result());
                            else routingContext.fail(view.cause());
                        });
                    });
                });
    }

    private void personMovies(RoutingContext routingContext) {
        HttpServerRequest req = routingContext.request();
        HttpServerResponse res = routingContext.response();
        res.putHeader("content-type", "text/html");
        int personId = Integer.parseInt(req.getParam("id"));
        movie
                .getPersonCreditsCast(personId)
                .thenAccept(personMovies -> {
                    routingContext.put("personMovies", personMovies);
                    engine.render(routingContext, "templates", "/personMovies.hbs", view -> {
                        if (view.succeeded())
                            res.end(view.result());
                        else routingContext.fail(view.cause());
                    });
                });
    }

}
