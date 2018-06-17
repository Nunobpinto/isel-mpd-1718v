package movasync.model;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;


/**
 * @author Miguel Gamboa
 *         created on 04-08-2017
 */
public class Movie {
    private final int id;
    private final String originalTitle;
    private final String tagline;
    private final String overview;
    private final double voteAverage;
    private final String releaseDate;
    private final String posterPath;
    private final CompletableFuture<Stream<Credit>> credits;

    public Movie(
            int id,
            String originalTitle,
            String tagline,
            String overview,
            double voteAverage,
            String releaseDate,
            String posterPath, CompletableFuture<Stream<Credit>> credits)
    {
        this.id = id;
        this.originalTitle = originalTitle;
        this.tagline = tagline;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;

        this.credits = credits;
    }

    public int getId() {
        return id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getTagline() {
        return tagline;
    }

    public String getOverview() {
        return overview;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() { return posterPath; }

    public CompletableFuture<Stream<Credit>> getCredits() {
        return credits;
    }


    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", originalTitle='" + originalTitle + '\'' +
                ", tagline='" + tagline + '\'' +
                ", overview='" + overview + '\'' +
                ", voteAverage=" + voteAverage +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }
}
