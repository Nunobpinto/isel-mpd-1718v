package movasync.dto;

public class CrewItemDto {
    private String credit_id;
    private String department;
    private int id;
    private String job;
    private String name;

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

    public void setName(String name) {
        this.name = name;
    }

    public String getCreditId() {
        return credit_id;
    }

    public void setCreditId(String credit_id) {
        this.credit_id = credit_id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
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
