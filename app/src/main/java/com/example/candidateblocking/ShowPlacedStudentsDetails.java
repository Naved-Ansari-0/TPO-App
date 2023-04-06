package com.example.candidateblocking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
public class ShowPlacedStudentsDetails extends AppCompatActivity {
    private RecyclerView placedStudentsRecyclerView;
    private ArrayList<PlacedStudentModel> placedStudents, parsedPlacedStudentList;
    private TextView showDetailsTopTextView;
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_placed_students_details);

        placedStudentsRecyclerView = findViewById(R.id.placedStudentsRecyclerView);
        showDetailsTopTextView = findViewById(R.id.showDetailsTopTextView);
        placedStudents = new ArrayList<>();
        parsedPlacedStudentList = new ArrayList<>();

        ParseJsonString parseJsonString = new ParseJsonString(this);
        parsedPlacedStudentList = parseJsonString.getPlacedStudentList();

        Bundle extras = getIntent().getExtras();
        String fromFragment = extras.getString("FROM_FRAGMENT");

        if (fromFragment.equals("ListFragment")) {

            ArrayList<String> selectedBranches = extras.getStringArrayList("selectedBranches");
            float[] ctcRangeArray = extras.getFloatArray("ctcRangeArray");

            String branchSelected = selectedBranches.get(0);
            for(int i=1; i<selectedBranches.size(); i++)
                branchSelected += ", " + selectedBranches.get(i);

            String ctcRange = "[" + ctcRangeArray[0] + "-" + ctcRangeArray[1] + "] lpa";

            int count[] = listByBranchAndCTC(selectedBranches, ctcRangeArray);

            showDetailsTopTextView.setText(
                    "Branch selected : " + branchSelected +"\n"+
                    "CTC range selected : " + ctcRange + "\n" +
                    "Total students placed : " + count[0]  + "\n" +
                    "Total packages offered : " + count[1] + "\n"
                    );

        }else if (fromFragment.equals("SearchFragment")) {

            String searchType = extras.getString("searchType");
            String searchValue = extras.getString("searchValue");

            if(searchType.equals("RollNo")) {
                searchByRollNo(searchValue);
            } else if(searchType.equals("Name")) {
                searchByName(searchValue);
            } else if(searchType.equals("Company")) {
                searchByCompany(searchValue);
            }

            if(!searchType.equals("RollNo")) {
                showDetailsTopTextView.setText(placedStudents.size() + " entr" +
                        (placedStudents.size() == 1 ? "y" : "ies") + " found \n with " +
                        (searchType.equals("Name") ? "name " : "company ") +
                        "\"" + searchValue + "\"");
            }
        }

        if(placedStudents.size()==0) {
            showDetailsTopTextView.setText("Nothing to show");
        }

        placedStudentsRecyclerView.setLayoutManager(new LinearLayoutManager(ShowPlacedStudentsDetails.this));
        PlacedStudentsRecyclerViewAdapter adapter = new PlacedStudentsRecyclerViewAdapter();
        placedStudentsRecyclerView.setAdapter(adapter);
        adapter.setPlacedStudents(placedStudents);
    }

    private void searchByRollNo(String rollNo){
        rollNo = rollNo.trim();
        for(int i=0; i<parsedPlacedStudentList.size(); i++){
            if(parsedPlacedStudentList.get(i).getRollNo().equals(rollNo)) {
                parsedPlacedStudentList.get(i).setCountNo("");
                placedStudents.add(parsedPlacedStudentList.get(i));
            }
        }
    }

    private void searchByName(String name){
        name = name.trim().toLowerCase();
        int count = 0;
        for(int i=0; i<parsedPlacedStudentList.size(); i++){
            if(parsedPlacedStudentList.get(i).getName().toLowerCase().contains(name)) {
                count++;
                parsedPlacedStudentList.get(i).setCountNo(String.valueOf(count));
                placedStudents.add(parsedPlacedStudentList.get(i));
            }
        }
    }

    private void searchByCompany(String company){
        company = company.trim().toLowerCase();
        int count = 0;
        for(int i=0; i<parsedPlacedStudentList.size(); i++){
            ArrayList<String> companies = parsedPlacedStudentList.get(i).getCompanies();
            for(int j=0; j<companies.size(); j++){
                if(companies.get(j).toLowerCase().contains(company)) {
                    count++;
                    parsedPlacedStudentList.get(i).setCountNo(String.valueOf(count));
                    placedStudents.add(parsedPlacedStudentList.get(i));
                    break;
                }
            }
        }
    }

    private int[] listByBranchAndCTC(ArrayList<String> selectedBranches, float[] ctcRangeArray){
        int countOfStudents = 0;
        int countOfPackages = 0;
        float minPackage = ctcRangeArray[0];
        float maxPackage = ctcRangeArray[1];
        for(int i=0; i<parsedPlacedStudentList.size(); i++){
            if(selectedBranches.contains(parsedPlacedStudentList.get(i).getBranch())){
                boolean check = false;

                ArrayList<Float> packages = parsedPlacedStudentList.get(i).getPackages();
                ArrayList<String> companies = parsedPlacedStudentList.get(i).getCompanies();

                ArrayList<Float> validPackages = new ArrayList<>();
                ArrayList<String> validCompanies = new ArrayList<>();

                for(int j=0; j<packages.size(); j++){
                    if(minPackage<=packages.get(j) && packages.get(j)<=maxPackage){
                        check = true;
                        countOfPackages++;
                        validPackages.add(packages.get(j));
                        validCompanies.add(companies.get(j));
                    }
                }

                if(check) {
                    countOfStudents++;
                    PlacedStudentModel student = parsedPlacedStudentList.get(i);
                    student.setCompanies(validCompanies);
                    student.setPackages(validPackages);
                    student.setCountNo(String.valueOf(countOfStudents));
                    placedStudents.add(student);
                }
            }
        }
        int[] count = {countOfStudents, countOfPackages};
        return count;
    }
}