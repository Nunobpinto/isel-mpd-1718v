package movasync.model;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * @author Miguel Gamboa
 *         created on 04-08-2017
 */
public class Person {
    private final int id;
    private final String name;
    private final CompletableFuture<Stream<SearchItem>> movies;
    private final String placeOfBirth;
    private final String biography;
    private final String profilePath;


    public Person(int id, String name, String placeOfBirth, String biography, CompletableFuture<Stream<SearchItem>> movies, String profilePath) {
        this.id = id;
        this.name = name;
        this.movies = movies;
        this.placeOfBirth = placeOfBirth;
        this.biography = biography;
        this.profilePath = profilePath;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public String getBiography() {
        return biography;
    }

    public String getProfilePath() { return profilePath; }

    public CompletableFuture<Stream<SearchItem>> getMovies() {
        return movies;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", movies=" + movies +
                '}';
    }
}
