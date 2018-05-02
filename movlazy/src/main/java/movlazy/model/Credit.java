package movlazy.model;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Miguel Gamboa
 *         created on 04-08-2017
 */
public class Credit {
    private final int id;
    private final int movieId;
    private final String department;
    private final String job;
    private final String character;
    private final String name;
    private final Supplier<Person> details;


    public Credit(int id, int movieId, String department, String job, String character, String name, Supplier<Person> details) {
        this.id = id;
        this.movieId = movieId;
        this.department = department;
        this.job = job;
        this.character = character;
        this.name = name;
        this.details = details;
    }

    public int getId() {
        return id;
    }

    public String getCharacter() {
        return character;
    }

    public String getDepartment() { return department; }

    public String getJob() { return job; }

    public String getName() {
        return name;
    }

    public int getMovieId() {
        return movieId;
    }

    public Person getDetails() {
        return details.get();
    }

    @Override
    public String toString() {
        return "Credit{" +
                "id=" + id +
                ", movieId=" + movieId +
                ", department=" + department +
                ", job=" + job +
                ", character='" + character + '\'' +
                ", name='" + name + '\'' +
                ", getPersonCreditsCast=" + details +
                '}';
    }
}
