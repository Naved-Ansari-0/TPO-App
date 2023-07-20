package com.example.candidateblocking.home.record;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.candidateblocking.R;


public class SearchFragment extends Fragment {
    private EditText searchStudentEditText;
    private RadioButton rollNoRadioButton, nameRadioButton, companyRadioButton;
    private Button searchStudentByButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sub_fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchStudentEditText = view.findViewById(R.id.searchStudentEditText);
        rollNoRadioButton = view.findViewById(R.id.rollNoRadioButton);
        nameRadioButton = view.findViewById(R.id.nameRadioButton);
        companyRadioButton = view.findViewById(R.id.companyRadioButton);
        searchStudentByButton = view.findViewById(R.id.searchStudentByButton);

        searchStudentByButton.setOnClickListener(view1 -> {

            String searchText, searchType;
            searchText = searchStudentEditText.getText().toString().trim().toLowerCase();
            if(searchText.equals("")){
                Toast.makeText(requireContext(), "Enter some text to search.", Toast.LENGTH_SHORT).show();
                return;
            }

            if(rollNoRadioButton.isChecked()){
                searchType = "RollNo";
            }else if(nameRadioButton.isChecked()){
                searchType = "Name";
            }else if(companyRadioButton.isChecked()){
                searchType = "Company";
            }else{
                Toast.makeText(requireContext(), "Choose an option for search type.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(requireContext(), ShowPlacedStudentsDetails.class);
            Bundle extras = new Bundle();
            extras.putString("FROM_FRAGMENT", "SearchFragment");
            extras.putString("searchType", searchType);
            extras.putString("searchText", searchText);
            intent.putExtras(extras);
            startActivity(intent);
        });

    }

}