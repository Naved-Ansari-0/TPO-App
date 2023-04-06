package com.example.candidateblocking;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParseJsonString extends AppCompatActivity {

    private Context context;
    public ParseJsonString(Context context){
        this.context = context;
    }
    public ArrayList<PlacedStudentModel> getPlacedStudentList() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
        String placedStudentData = sharedPreferences.getString("placedStudentData", "");

        ArrayList<PlacedStudentModel> placedStudents = new ArrayList<>();

        try{
            JSONObject object = new JSONObject(placedStudentData);
            JSONArray array = object.getJSONArray("data");
            if(array.length()>0){
                for(int i=0; i<array.length(); i++){
                    JSONObject record = array.getJSONObject(i);
                    String Name = record.getString("Name").trim();
                    String RollNo = record.getString("RollNo").trim();
                    String Branch = record.getString("Branch").trim();
                    String Gender = record.getString("Gender").trim();
                    String PhoneNo = record.getString("PhoneNo").trim();
                    String Email = record.getString("Email").trim();
                    String[] packagesList = record.getString("Packages").replace("+","").split("/",0);
                    String[] companiesList = record.getString("Companies").split("/",0);
                    String CountNo = Integer.toString(-1);
                    ArrayList<Float> Packages = new ArrayList<>();
                    ArrayList<String> Companies = new ArrayList<>();
                    for(int j=0; j<packagesList.length; j++){
                        Packages.add(Float.parseFloat(packagesList[j].trim()));
                        Companies.add(companiesList[j].trim());
                    }
                    placedStudents.add(new PlacedStudentModel(Name, RollNo, Branch, Gender, PhoneNo, Email, Companies, Packages, CountNo));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return placedStudents;
    }

}
