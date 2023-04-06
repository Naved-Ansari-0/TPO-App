package com.example.candidateblocking;

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

import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

    private RangeSlider ctcRangeSlider;
    private TextView selectedCTCRangeTextView;
    private LinearLayout chooseBranchLinearLayout;
    private Button nextButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlockFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ctcRangeSlider = getView().findViewById(R.id.ctcRangeSlider);
        selectedCTCRangeTextView = getView().findViewById(R.id.selectedCTCRangeTextView);
        chooseBranchLinearLayout = getView().findViewById(R.id.chooseBranchLinearLayout);
        nextButton = getView().findViewById(R.id.nextButton);

        ArrayList<String> branchList = new ArrayList<>();
        branchList.add("CE");
        branchList.add("CSE");
        branchList.add("EE");
        branchList.add("EL");
        branchList.add("ME");
        branchList.add("IT");
        branchList.add("MCA");

        for(int i=0; i<branchList.size(); i++){
            CheckBox checkBox = new CheckBox(getActivity().getApplicationContext());
            checkBox.setText(branchList.get(i));
            chooseBranchLinearLayout.addView(checkBox);
        }

        ctcRangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                List<Float> ctcRange = ctcRangeSlider.getValues();
                String str = "Selected range [" + ctcRange.get(0) + "-" + ctcRange.get(1) + "] lpa";
                selectedCTCRangeTextView.setText(str);
            }
        });

        nextButton.setOnClickListener(view1 -> moveToShowPlacedStudentsDetailsScreen());
    }

    private void moveToShowPlacedStudentsDetailsScreen(){

        List<Float> ctcRangeList = ctcRangeSlider.getValues();
        float[] ctcRangeArray = {ctcRangeList.get(0), ctcRangeList.get(1)};
        ArrayList<String> selectedBranches = new ArrayList<>();

        boolean moveToNextScreen = false;
        for(int i=0; i<chooseBranchLinearLayout.getChildCount(); i++){
            CheckBox checkBox = (CheckBox) chooseBranchLinearLayout.getChildAt(i);
            if(checkBox.isChecked()) {
                moveToNextScreen = true;
                String branch = checkBox.getText().toString();
                selectedBranches.add(branch);
            }
        }

        if(!moveToNextScreen)
            return;

        Intent intent = new Intent(getActivity().getApplicationContext(), ShowPlacedStudentsDetails.class);
        Bundle extras = new Bundle();
        extras.putString("FROM_FRAGMENT", "ListFragment");
        extras.putStringArrayList("selectedBranches", selectedBranches);
        extras.putFloatArray("ctcRangeArray", ctcRangeArray);
        intent.putExtras(extras);
        startActivity(intent);
    }

}