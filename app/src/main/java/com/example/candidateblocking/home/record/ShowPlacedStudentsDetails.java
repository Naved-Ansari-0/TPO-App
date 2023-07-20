package com.example.candidateblocking.home.record;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.candidateblocking.R;
import com.example.candidateblocking.utils.ParseJsonString;
import com.example.candidateblocking.adapters.PlacedStudentsRecyclerViewAdapter;
import com.example.candidateblocking.models.PlacedStudentModel;

import java.util.ArrayList;


public class ShowPlacedStudentsDetails extends AppCompatActivity {
    private TextView showDetailsTopTextView;
    private RecyclerView placedStudentsRecyclerView;
    private ArrayList<PlacedStudentModel> filteredList, parsedList;
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_placed_students_details);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        showDetailsTopTextView = findViewById(R.id.showDetailsTopTextView);
        placedStudentsRecyclerView = findViewById(R.id.placedStudentsRecyclerView);
        filteredList = new ArrayList<>();
        parsedList = new ArrayList<>();

        ParseJsonString parseJsonString = new ParseJsonString(this);
        parsedList = parseJsonString.getPlacedStudentList();

        Bundle extras = getIntent().getExtras();
        String fromFragment = extras.getString("FROM_FRAGMENT");

        if (fromFragment.equals("FilterFragment")) {

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
            String searchText = extras.getString("searchText");

            if(searchType.equals("RollNo")) {
                searchByRollNo(searchText);
            } else if(searchType.equals("Name")) {
                searchByName(searchText);
            } else if(searchType.equals("Company")) {
                searchByCompany(searchText);
            }

            if(!searchType.equals("RollNo")) {
                showDetailsTopTextView.setText(filteredList.size() + " entr" +
                        (filteredList.size() == 1 ? "y" : "ies") + " found\nwith " +
                        (searchType.equals("Name") ? "name " : "company ") +
                        "\"" + searchText + "\"");
            }
        }

        if(filteredList.size()==0) {
            showDetailsTopTextView.setText("Nothing to show");
        }

        placedStudentsRecyclerView.setLayoutManager(new LinearLayoutManager(ShowPlacedStudentsDetails.this));
        PlacedStudentsRecyclerViewAdapter adapter = new PlacedStudentsRecyclerViewAdapter();
        placedStudentsRecyclerView.setAdapter(adapter);
        adapter.setPlacedStudents(filteredList);

    }

    private void searchByRollNo(String rollNo){
        for(int i = 0; i< parsedList.size(); i++){
            if(parsedList.get(i).getRollNo().equals(rollNo)) {
                parsedList.get(i).setSerialNo("");
                filteredList.add(parsedList.get(i));
            }
        }
    }

    private void searchByName(String name){
        int count = 0;
        for(int i = 0; i< parsedList.size(); i++){
            if(parsedList.get(i).getName().toLowerCase().contains(name)) {
                count++;
                parsedList.get(i).setSerialNo(String.valueOf(count));
                filteredList.add(parsedList.get(i));
            }
        }
    }

    private void searchByCompany(String company){
        int count = 0;
        for(int i = 0; i< parsedList.size(); i++){
            ArrayList<String> companies = parsedList.get(i).getCompanies();
            for(int j=0; j<companies.size(); j++){
                if(companies.get(j).toLowerCase().contains(company)) {
                    count++;
                    parsedList.get(i).setSerialNo(String.valueOf(count));
                    filteredList.add(parsedList.get(i));
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
        for(int i = 0; i< parsedList.size(); i++){
            if(selectedBranches.contains(parsedList.get(i).getBranch())){
                boolean check = false;

                ArrayList<Float> packages = parsedList.get(i).getPackages();
                ArrayList<String> companies = parsedList.get(i).getCompanies();

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
                    PlacedStudentModel student = parsedList.get(i);
                    student.setCompanies(validCompanies);
                    student.setPackages(validPackages);
                    student.setSerialNo(String.valueOf(countOfStudents));
                    filteredList.add(student);
                }
            }
        }
        int[] count = {countOfStudents, countOfPackages};
        return count;
    }

}