package com.example.candidateblocking;

public class Candidate {

    private String Name, RollNo, Branch, Gender, PhoneNo, Email, Packages, Companies, CountNo;

    public Candidate(String name, String rollNo, String branch, String gender, String phoneNo, String email, String packages, String companies, String countNo) {
        Name = name;
        RollNo = rollNo;
        Branch = branch;
        Gender = gender;
        PhoneNo = phoneNo;
        Email = email;
        Packages = packages;
        Companies = companies;
        CountNo = countNo;
    }

    public String getName() {
        return Name;
    }
    public String getRollNo() {
        return RollNo;
    }
    public String getBranch() {
        return Branch;
    }
    public String getGender() {
        return Gender;
    }
    public String getPhoneNo() {
        return PhoneNo;
    }
    public String getEmail() {
        return Email;
    }
    public String getPackages() {
        return Packages;
    }
    public String getCompanies() {
        return Companies;
    }
    public String getCountNo() {
        return CountNo;
    }

}
