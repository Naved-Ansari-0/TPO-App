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

    public void setName(String name) {
        Name = name;
    }

    public String getRollNo() {
        return RollNo;
    }

    public void setRollNo(String rollNo) {
        RollNo = rollNo;
    }

    public String getBranch() {
        return Branch;
    }

    public void setBranch(String branch) {
        Branch = branch;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getPhoneNo() {
        return PhoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        PhoneNo = phoneNo;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPackages() {
        return Packages;
    }

    public void setPackages(String packages) {
        Packages = packages;
    }

    public String getCompanies() {
        return Companies;
    }

    public void setCompanies(String companies) {
        Companies = companies;
    }

    public String getCountNo() {
        return CountNo;
    }

    public void setCountNo(String countNo) {
        CountNo = countNo;
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "Name=" + Name +
                ", RollNo=" + RollNo +
                ", Branch=" + Branch +
                ", Gender=" + Gender +
                ", PhoneNo=" + PhoneNo +
                ", Email=" + Email +
                ", Packages=" + Packages +
                ", Companies=" + Companies +
                ", CountNo=" + CountNo +
                '}';
    }
}
