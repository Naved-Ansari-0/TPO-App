package in.navedansari.tpoapp.models;

public class DataRequest {
    private String email;
    private String token;
    private String year;
    private String type;
    private String rollNo;
    private String name;
    private String gender;
    private String course;
    private String branch;
    private String company;
    private String drive;
    private String minctc;
    private String maxctc;

    public DataRequest() {
        this.email = "";
        this.token = "";
        this.year = "";
        this.type = "";
        this.rollNo = "";
        this.name = "";
        this.gender = "";
        this.course = "";
        this.branch = "";
        this.company = "";
        this.drive = "";
        this.minctc = "";
        this.maxctc = "";
    }

    public DataRequest(String email, String token, String year, String type, String rollNo, String name, String gender, String course, String branch, String company, String drive, String minctc, String maxctc) {
        this.email = email;
        this.token = token;
        this.year = year;
        this.type = type;
        this.rollNo = rollNo;
        this.name = name;
        this.gender = gender;
        this.course = course;
        this.branch = branch;
        this.company = company;
        this.drive = drive;
        this.minctc = minctc;
        this.maxctc = maxctc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDrive() {
        return drive;
    }

    public void setDrive(String drive) {
        this.drive = drive;
    }

    public String getMinctc() {
        return minctc;
    }

    public void setMinctc(String minctc) {
        this.minctc = minctc;
    }

    public String getMaxctc() {
        return maxctc;
    }

    public void setMaxctc(String maxctc) {
        this.maxctc = maxctc;
    }
}
