package movweb;

import com.google.common.util.concurrent.RateLimiter;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import movasync.MovieService;
import movasync.MovieWebApi;
import routes.MovieRouter;
import util.HttpRequest;
import util.IRequest;

public class WebApp {

    public static void main(String[] args) throws Exception {
        Vertx vertx = Vertx.vertx();
        final RateLimiter rateLimiter = RateLimiter.create(5.0);
        IRequest httpRequest = new HttpRequest()
                .compose(System.out::println)
                .compose(__-> rateLimiter.acquire());
        Router router = MovieRouter.router(vertx, new MovieService(new MovieWebApi(httpRequest)));

        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(3000);
    }
}
