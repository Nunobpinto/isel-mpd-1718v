package movlazy.dto;

public class PersonCreditsDto {
    private final int id;
    private CrewItemDto[] crew;
    private SearchItemDto[] cast;

    public PersonCreditsDto(int id, CrewItemDto[] crew, SearchItemDto[] cast) {
        this.id = id;
        this.crew = crew;
        this.cast = cast;
    }

    public int getId() {
        return id;
    }

    public CrewItemDto[] getCrew() {
        return crew;
    }

    public SearchItemDto[] getCast() {
        return cast;
    }
}
