package com.example.candidateblocking;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.candidateblocking.databinding.ActivityHomeBinding;
import com.example.candidateblocking.home.AdminFragment;
import com.example.candidateblocking.home.RecordFragment;
import com.example.candidateblocking.home.StatsFragment;
import com.example.candidateblocking.utils.DataFromAPIService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private ImageButton accountButton, feedbackButton, infoButton;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private GoogleSignInAccount account;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);

        replaceFragment(new RecordFragment());
        binding.bottomNavigationBar.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.listIcon:
                    replaceFragment(new RecordFragment());
                    break;
                case R.id.statsIcon:
                    replaceFragment(new StatsFragment());
                    break;
                case R.id.adminIcon:
                    replaceFragment(new AdminFragment());
                    break;
            }
            return true;
        });

        accountButton = findViewById(R.id.accountIcon);
        feedbackButton = findViewById(R.id.feedbackIcon);
        infoButton = findViewById(R.id.infoIcon);

        accountButton.setOnClickListener(view -> showAccountDialog());
        feedbackButton.setOnClickListener(view -> showFeedbackDialog());
        infoButton.setOnClickListener(view -> showInfoDialog());

        syncPlacedStudentData();

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.homeFrameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void syncPlacedStudentData(){

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Syncing data for the first time");

        SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
        String dataLink =  sharedPreferences.getString("dataLink", "") + "?UserType=" + sharedPreferences.getString("userType","");
        String oldData = sharedPreferences.getString("placedStudentData", "");

        if(oldData.equals(""))
            progressDialog.show();

        DataFromAPIService dataFromAPIService = new DataFromAPIService(this);
        dataFromAPIService.getStringDataFromApi(dataLink, new DataFromAPIService.VolleyStringResponseListener() {
            @Override
            public void OnError(String message) {
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "Error while syncing data", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void OnResponse(String response) {
                SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("placedStudentData", response);
                long lastSyncedTime = System.currentTimeMillis();
                editor.putLong("lastSyncedTime", lastSyncedTime);
                editor.apply();
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "Data synced successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAccountDialog(){

        accountButton.setImageResource(R.drawable.account_icon_selected);

        AlertDialog.Builder accountDialogBuilder;
        accountDialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        LayoutInflater inflater = getLayoutInflater();
        View view  = inflater.inflate(R.layout.dialog_account, null);
        accountDialogBuilder.setView(view);
        AlertDialog accountDialog = accountDialogBuilder.create();
        accountDialog.setCancelable(true);
        accountDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        accountDialog.show();
        accountDialog.setOnDismissListener(dialog -> accountButton.setImageResource(R.drawable.account_icon_unselected));

        CircleImageView userImage = accountDialog.findViewById(R.id.userImage);
        TextView userName = accountDialog.findViewById(R.id.userName);
        TextView userEmail = accountDialog.findViewById(R.id.userEmail);
        Button logoutButton = accountDialog.findViewById(R.id.logoutButton);

        if(account!=null){
            String name = account.getDisplayName();
            String email = account.getEmail();
            Uri image = account.getPhotoUrl();
            Picasso.get().load(image).into(userImage);
            assert userName != null;
            userName.setText(name);
            assert userEmail != null;
            userEmail.setText(email);
        }

        assert logoutButton != null;
        logoutButton.setOnClickListener(v -> {
            gsc.signOut().addOnCompleteListener(task -> {
                SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(Home.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
            });
        });
    }
    private void showFeedbackDialog(){

        feedbackButton.setImageResource(R.drawable.feedback_icon_selected);

        AlertDialog.Builder feedbackDialogBuilder;
        feedbackDialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        LayoutInflater inflater = getLayoutInflater();
        View view  = inflater.inflate(R.layout.dialog_feedback, null);
        feedbackDialogBuilder.setView(view);
        AlertDialog feedbackDialog = feedbackDialogBuilder.create();
        feedbackDialog.setCancelable(true);
        feedbackDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        feedbackDialog.show();
        feedbackDialog.setOnDismissListener(dialog -> feedbackButton.setImageResource(R.drawable.feedback_icon_unselected));

        Button feedbackSubmitButton = feedbackDialog.findViewById(R.id.feedbackSubmitButton);
        EditText feedbackEditText = feedbackDialog.findViewById(R.id.feedbackEditText);
        assert feedbackSubmitButton != null;
        assert feedbackEditText != null;

        feedbackSubmitButton.setOnClickListener(v -> {
            String feedback = feedbackEditText.getText().toString().trim();
            if(feedback.equals("")){
                Toast.makeText(this, "Enter something to submit", Toast.LENGTH_SHORT).show();
                return;
            }
            String email = account.getEmail();
            assert email != null;
            if(email.equals("")){
                Toast.makeText(this, "Error on retrieving user credentials", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
            String feedbackLink =  sharedPreferences.getString("feedbackLink", "") + "?feedback=" + email + "/" + feedback;

            feedbackDialog.setCancelable(false);
            feedbackSubmitButton.setText("Submitting");
            feedbackSubmitButton.setClickable(false);

            DataFromAPIService dataFromAPIService = new DataFromAPIService(this);
            dataFromAPIService.getStringDataFromApi(feedbackLink, new DataFromAPIService.VolleyStringResponseListener() {
                @Override
                public void OnError(String message) {
                    feedbackDialog.setCancelable(true);
                    feedbackSubmitButton.setText("Submit");
                    feedbackSubmitButton.setClickable(true);
                    Toast.makeText(getApplicationContext(), "Error while submitting feedback", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void OnResponse(String response) {
                    if(response.equals("SUCCESSFUL")){
                        feedbackDialog.cancel();
                        Toast.makeText(getApplicationContext(), "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        feedbackDialog.setCancelable(true);
                        feedbackSubmitButton.setText("Submit");
                        feedbackSubmitButton.setClickable(true);
                        Toast.makeText(getApplicationContext(), "Error, feedback not submitted", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    private void showInfoDialog(){

        infoButton.setImageResource(R.drawable.info_icon_selected);

        AlertDialog.Builder infoDialogBuilder;
        infoDialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        LayoutInflater inflater = getLayoutInflater();
        View view  = inflater.inflate(R.layout.dialog_info, null);
        infoDialogBuilder.setView(view);
        AlertDialog infoDialog = infoDialogBuilder.create();
        infoDialog.setCancelable(true);
        infoDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        infoDialog.show();
        infoDialog.setOnDismissListener(dialog -> infoButton.setImageResource(R.drawable.info_icon_unselected));

        SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
        String note =  sharedPreferences.getString("note", "");

        TextView noteTextView = infoDialog.findViewById(R.id.noteTextView);
        Button website = infoDialog.findViewById(R.id.website);

        assert noteTextView != null;
        noteTextView.setText(note);
        assert website != null;
        website.setOnClickListener(v->{
            String url = "https://www.navedansari.in";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        });

    }

}