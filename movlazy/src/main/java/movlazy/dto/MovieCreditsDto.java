package movlazy.dto;

public class MovieCreditsDto {
    private final int id;
    private final CastItemDto[] cast;
    private final CrewItemDto[] crew;

    public MovieCreditsDto(int movieId, CastItemDto[] cast, CrewItemDto[] crew) {
        this.id = movieId;
        this.cast = cast;
        this.crew = crew;
    }

    public int getMovieId() {
        return id;
    }

    public CastItemDto[] getCast() {
        return cast;
    }

    public CrewItemDto[] getCrew() {
        return crew;
    }

    @Override
    public String toString() {
        return "MovieCreditsDto{" +
                "id=" + id +
                ", cast='" + cast.toString() +
                ", crew='" + crew.toString() + '\'' +
                '}';
    }
}
