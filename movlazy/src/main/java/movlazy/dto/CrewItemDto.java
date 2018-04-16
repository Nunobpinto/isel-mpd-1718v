package movlazy.dto;

public class CrewItemDto {
    private final String credit_id;
    private final String department;
    private final int id;
    private final String job;
    private final String name;

    public CrewItemDto(String credit_id, String department, int id, String job, String name) {
        this.credit_id = credit_id;
        this.department = department;
        this.id = id;
        this.job = job;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCredit_id() {
        return credit_id;
    }

    public String getDepartment() {
        return department;
    }

    public int getId() {
        return id;
    }

    public String getJob() {
        return job;
    }

    @Override
    public String toString() {
        return "CrewItemDto{" +
                "credit_id=" + credit_id +
                ", department='" + department +
                ", job='" + job + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
