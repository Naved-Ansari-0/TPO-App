package com.example.candidateblocking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
public class ShowDetails extends AppCompatActivity {

    private RecyclerView candidateRecyclerView;
    private ArrayList<Candidate> candidates;
    private TextView candidateCount;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);

        candidateRecyclerView = findViewById(R.id.candidateRecyclerView);
        candidateCount = findViewById(R.id.candidateCount);
        candidates = new ArrayList<>();

        try{
            JSONObject jsonObject = new JSONObject(Home.fetched_data);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            int count = 0;
            if(jsonArray.length()>0){
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    if(BlockFragment.selected_branches.size()==0){
                        String searchText = SearchFragment.input_search.toLowerCase();
                        if(SearchFragment.searchInputType.equals("RollNo")){
                            if(jsonObject1.getString("RollNo").equals(searchText));
                            else
                                continue;
                        }
                        if(jsonObject1.getString(SearchFragment.searchInputType).toLowerCase().indexOf(searchText)==-1)
                            continue;
                    }else if(BlockFragment.selected_branches.contains(jsonObject1.getString("Branch"))){
                            String packages = jsonObject1.getString("Packages");
                            String[] packagesList = packages.split("/", 0);
                            boolean valid_candidate = false;
                            for(String p: packagesList){
                                String t = p.replace("+", "");
                                if(Float.parseFloat(t)>BlockFragment.input_ctc){
                                    valid_candidate = true;
                                    break;
                                }
                            }
                            if(valid_candidate == false) {
                                continue;
                            }
                    } else{
                        continue;
                    }
                    count++;
                    String Name = jsonObject1.getString("Name");
                    String RollNo = jsonObject1.getString("RollNo");
                    String Branch = jsonObject1.getString("Branch");
                    String Gender = jsonObject1.getString("Gender");
                    String PhoneNo = jsonObject1.getString("PhoneNo");
                    String Email = jsonObject1.getString("Email");
                    String Packages = jsonObject1.getString("Packages");
                    String Companies = jsonObject1.getString("Companies");
                    String CountNo = Integer.toString(count);
//                    System.out.println(count + " " + Name + " " + RollNo + " " + Branch + " " + Gender + " " + PhoneNo + " " + Email + " " + Packages + " " + Companies);
                    candidates.add(new Candidate(Name, RollNo, Branch, Gender, PhoneNo, Email, Packages, Companies, CountNo));
                }
            }
            if(count==0)
                candidateCount.setText("Nothing to show");
            else if(BlockFragment.selected_branches.size()>0)
                candidateCount.setText("Total : " + count);
            else if (!SearchFragment.searchInputType.equals("RollNo")){
                candidateCount.setText(count + " entr" + (count==1?"y":"ies") + " found \n with " +
                        (SearchFragment.searchInputType.equals("Name")?"name ":"company ") +
                        "\"" + SearchFragment.input_search + "\"");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        candidateRecyclerView.setLayoutManager(new LinearLayoutManager(ShowDetails.this));
        CandidateRecViewAdapter adapter = new CandidateRecViewAdapter();
        candidateRecyclerView.setAdapter(adapter);
        adapter.setCandidates(candidates);
        BlockFragment.selected_branches.clear();
    }
}