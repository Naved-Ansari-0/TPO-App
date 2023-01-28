package com.example.candidateblocking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends Fragment {
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private CircleImageView userImage;
    private TextView userName, userEmail, note;

    private EditText feedbackEditText;
    private ImageButton sourceCodeLinkButton;
    private Button logoutButton, feedbackButton;
    private String sourceCodeLink, feedbackLink;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
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
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(getActivity(), gso);

        userImage = getView().findViewById(R.id.userImage);
        userName = getView().findViewById(R.id.userName);
        userEmail = getView().findViewById(R.id.userEmail);

        note = getView().findViewById(R.id.note);

        sourceCodeLinkButton = getView().findViewById(R.id.sourceCodeLinkButton);

        feedbackButton = getView().findViewById(R.id.feedbackButton);
        logoutButton = getView().findViewById(R.id.logoutButton);

        feedbackEditText = getView().findViewById(R.id.feedbackEditText);

        setLinksAndNote();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());

        if(account!=null){
            String name = account.getDisplayName();
            String email = account.getEmail();
            Uri image = account.getPhotoUrl();
            Picasso.get().load(image).into(userImage);
            userName.setText(name);
            userEmail.setText(email);
        }
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        sourceCodeLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sourceCodeLink));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(intent, "Select Browser"));
                }catch (Exception e){

                }
            }
        });
        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String feedback = feedbackEditText.getText().toString();
                if(feedback.equals("")){
                    Toast.makeText(getContext(), "Enter something to submit", Toast.LENGTH_SHORT).show();
                    return;
                }
                String email = account.getEmail();

                feedbackButton.setEnabled(false);
                feedbackButton.setText("Submitting");
                StringRequest stringRequest = new StringRequest(Request.Method.GET, feedbackLink+"?feedback="+email+"/"+feedback, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("SUCCESSFUL")) {
                            feedbackEditText.setText("");
                            Toast.makeText(getContext(), "Feedback recorded successfully", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getContext(), "Feedback not recorded", Toast.LENGTH_SHORT).show();
                        }
                        feedbackButton.setEnabled(true);
                        feedbackButton.setText("Submit");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        feedbackButton.setEnabled(true);
                        feedbackButton.setText("Submit");
                        Toast.makeText(getContext(), "Error while submitting feedback", Toast.LENGTH_SHORT).show();
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
                requestQueue.add(stringRequest);
            }
        });
    }
    private void setLinksAndNote(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
        sourceCodeLink = sharedPreferences.getString("sourceCodeLink", "");
        feedbackLink = sharedPreferences.getString("feedbackLink", "");
        note.setText(sharedPreferences.getString("note",""));
    }

    public void signOut(){
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().finish();
                startActivity(intent);
            }
        });
    }

}