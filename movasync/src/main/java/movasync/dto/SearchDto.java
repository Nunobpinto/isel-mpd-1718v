package movasync.dto;

public class SearchDto {
    private final int page;
    private final int total_results;
    private final int total_pages;
    private final SearchItemDto[] results;

    public SearchDto(int page, int total_results, int total_pages, SearchItemDto[] results) {
        this.page = page;
        this.total_results = total_results;
        this.total_pages = total_pages;
        this.results = results;
    }

    public int getPage() {
        return page;
    }

    public int getTotalResults() {
        return total_results;
    }

    public int getTotalPages() {
        return total_pages;
    }

    public SearchItemDto[] getResults() {
        return results;
    }

    @Override
    public String toString() {
        return "SearchDto{" +
                "page='" + page + '\'' +
                ", total_results='" + total_results + '\'' +
                ", total_pages='" + total_pages + '\'' +
                ", results=" + results.toString() +
                '}';
    }

}
