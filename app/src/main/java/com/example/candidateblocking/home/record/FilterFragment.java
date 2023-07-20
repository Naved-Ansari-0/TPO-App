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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.candidateblocking.R;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;

public class FilterFragment extends Fragment {
    private RangeSlider ctcRangeSlider;
    private TextView selectedCTCRangeTextView;
    private LinearLayout chooseBranchLinearLayout;
    private Button nextButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sub_fragment_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ctcRangeSlider = view.findViewById(R.id.ctcRangeSlider);
        selectedCTCRangeTextView = view.findViewById(R.id.selectedCTCRangeTextView);
        chooseBranchLinearLayout = view.findViewById(R.id.chooseBranchLinearLayout);
        nextButton = view.findViewById(R.id.nextButton);

        ArrayList<String> branchList = new ArrayList<>();
        branchList.add("CE");
        branchList.add("CSE");
        branchList.add("EE");
        branchList.add("EL");
        branchList.add("ME");
        branchList.add("IT");
        branchList.add("MCA");

        for(int i=0; i<branchList.size(); i++){
            CheckBox checkBox = new CheckBox(requireContext());
            checkBox.setText(branchList.get(i));
            chooseBranchLinearLayout.addView(checkBox);
        }

        ctcRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> ctcRange = ctcRangeSlider.getValues();
            selectedCTCRangeTextView.setText("Selected range [" + ctcRange.get(0) + "-" + ctcRange.get(1) + "] lpa");
        });

        nextButton.setOnClickListener(view1 -> moveToShowPlacedStudentsDetailsScreen());
    }

    private void moveToShowPlacedStudentsDetailsScreen(){

        List<Float> ctcRangeList = ctcRangeSlider.getValues();
        float[] ctcRangeArray = {ctcRangeList.get(0), ctcRangeList.get(1)};

        ArrayList<String> selectedBranches = new ArrayList<>();
        for(int i=0; i<chooseBranchLinearLayout.getChildCount(); i++){
            CheckBox checkBox = (CheckBox) chooseBranchLinearLayout.getChildAt(i);
            if(checkBox.isChecked()) {
                selectedBranches.add(checkBox.getText().toString());
            }
        }

        if(selectedBranches.size()==0) {
            Toast.makeText(requireContext(), "Select at least one branch", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(requireContext(), ShowPlacedStudentsDetails.class);
        Bundle extras = new Bundle();
        extras.putString("FROM_FRAGMENT", "FilterFragment");
        extras.putStringArrayList("selectedBranches", selectedBranches);
        extras.putFloatArray("ctcRangeArray", ctcRangeArray);
        intent.putExtras(extras);
        startActivity(intent);
    }

}