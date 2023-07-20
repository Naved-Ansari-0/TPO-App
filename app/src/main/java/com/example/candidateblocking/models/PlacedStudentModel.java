package com.example.candidateblocking.models;

import java.util.ArrayList;

public class PlacedStudentModel {
    private String Name, RollNo, Branch, Gender, PhoneNo, Email, SerialNo;
    private ArrayList<String> Companies;
    private ArrayList<Float> Packages;

    public PlacedStudentModel(String name, String rollNo, String branch, String gender, String phoneNo, String email, ArrayList<String> companies, ArrayList<Float> packages, String serialNo) {
        Name = name;
        RollNo = rollNo;
        Branch = branch;
        Gender = gender;
        PhoneNo = phoneNo;
        Email = email;
        Packages = packages;
        Companies = companies;
        SerialNo = serialNo;
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
    public String getSerialNo() {
        return SerialNo;
    }
    public ArrayList<String> getCompanies() {return Companies;}
    public ArrayList<Float> getPackages() {
        return Packages;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    public void setCompanies(ArrayList<String> companies) {
        Companies = companies;
    }

    public void setPackages(ArrayList<Float> packages) {
        Packages = packages;
    }
}
