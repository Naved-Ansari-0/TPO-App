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
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlockFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlockFragment extends Fragment {

    private TextView inputCTC;
    private CheckBox checkBoxCE, checkBoxCSE, checkBoxEE, checkBoxEL, checkBoxME, checkBoxIT, checkBoxMCA;
    private Button nextButton;
    static Set<String> selected_branches = new HashSet<>();
    static float input_ctc;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BlockFragment() {
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
    public static BlockFragment newInstance(String param1, String param2) {
        BlockFragment fragment = new BlockFragment();
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
        return inflater.inflate(R.layout.fragment_block, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputCTC = getView().findViewById(R.id.inputCTC);
        checkBoxCE = getView().findViewById(R.id.checkBoxCE);
        checkBoxCSE = getView().findViewById(R.id.checkBoxCSE);
        checkBoxEE = getView().findViewById(R.id.checkBoxEE);
        checkBoxEL = getView().findViewById(R.id.checkBoxEL);
        checkBoxME = getView().findViewById(R.id.checkBoxME);
        checkBoxIT = getView().findViewById(R.id.checkBoxIT);
        checkBoxMCA = getView().findViewById(R.id.checkBoxMCA);
        nextButton = getView().findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToNextScreen();
            }
        });
    }

    private void moveToNextScreen(){

        if(inputCTC.getText().toString().equals(""))
            return;
        if(!checkBoxCE.isChecked() && !checkBoxCSE.isChecked() &&
                !checkBoxEE.isChecked() && !checkBoxEL.isChecked() &&
                !checkBoxIT.isChecked() && !checkBoxME.isChecked() &&
                !checkBoxMCA.isChecked()
        )
            return;
        input_ctc = Float.parseFloat(inputCTC.getText().toString());
        inputCTC.setText("");
        selected_branches.clear();
        if(checkBoxCE.isChecked()) {
            selected_branches.add("CE");
            checkBoxCE.toggle();
        }
        if(checkBoxCSE.isChecked()) {
            selected_branches.add("CSE");
            checkBoxCSE.toggle();
        }
        if(checkBoxEE.isChecked()) {
            selected_branches.add("EE");
            checkBoxEE.toggle();
        }
        if(checkBoxEL.isChecked()){
            selected_branches.add("EL");
            checkBoxEL.toggle();
        }
        if(checkBoxME.isChecked()) {
            selected_branches.add("ME");
            checkBoxME.toggle();
        }
        if(checkBoxIT.isChecked()) {
            selected_branches.add("IT");
            checkBoxIT.toggle();
        }
        if(checkBoxMCA.isChecked()){
            selected_branches.add("MCA");
            checkBoxMCA.toggle();
        }
        Intent intent = new Intent(getActivity().getApplicationContext(), ShowDetails.class);
        startActivity(intent);
    }

}