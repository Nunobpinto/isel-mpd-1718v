package movlazy.model;

public class CrewItem {
    private final int id;
    private final int movieId;
    private final String job;
    private final String name;

    public CrewItem(int id, int movieId, String job, String name) {
        this.id = id;
        this.movieId = movieId;
        this.job = job;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getJob() {
        return job;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "CrewItem{" +
                "id=" + id +
                ", movieId=" + movieId +
                ", job='" + job + '\'' +
                ", name=" + name +
                '}';
    }
}