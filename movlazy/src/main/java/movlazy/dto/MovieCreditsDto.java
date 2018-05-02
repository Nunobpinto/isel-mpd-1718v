package movlazy.dto;

public class MovieCreditsDto {
    private final int id;
    private final CastItemDto[] credit;
    private final CrewItemDto[] crew;

    public MovieCreditsDto(int movieId, CastItemDto[] credit, CrewItemDto[] crew) {
        this.id = movieId;
        this.credit = credit;
        this.crew = crew;
    }

    public int getMovieId() {
        return id;
    }

    public CastItemDto[] getCredit() { return credit; }

    public CrewItemDto[] getCrew() {
        return crew;
    }

    @Override
    public String toString() {
        return "MovieCreditsDto{" +
                "id=" + id +
                ", cast='" + credit.toString() +
                ", crew='" + crew.toString() + '\'' +
                '}';
    }
}
