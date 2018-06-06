package movasync.model;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * @author Miguel Gamboa
 *         created on 04-08-2017
 */
public class Credit {
    private int id;
    private int movieId;
    private String department;
    private String job;
    private String character;
    private String name;
    private CompletableFuture<Person> details;


    public Credit(int id, int movieId, String department, String job, String character, String name, CompletableFuture<Person> details) {
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

    public CompletableFuture<Person> getDetails() {
        return details;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDetails(CompletableFuture<Person> details) {
        this.details = details;
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
