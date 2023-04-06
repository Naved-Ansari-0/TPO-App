package com.example.candidateblocking;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.candidateblocking.databinding.ActivityHomeBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private String placedStudentDataLink;
    public static String placedStudentData;
    private ImageButton accountButton, notificationButton, feedbackButton;
    private ProgressDialog progressDialog;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new ListFragment());
        binding.bottomNavigationBar.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.listIcon:
                    replaceFragment(new ListFragment());
                    break;
                case R.id.statsIcon:
                    replaceFragment(new StatsFragment());
                    break;
                case R.id.infoIcon:
                    replaceFragment(new InfoFragment());
                    break;
                case R.id.searchIcon:
                    replaceFragment(new SearchFragment());
                    break;
                case R.id.adminIcon:
                    replaceFragment(new AdminFragment());
                    break;
            }
            return true;
        });

        notificationButton = findViewById(R.id.notificationIcon);
        accountButton = findViewById(R.id.accountIcon);
        feedbackButton = findViewById(R.id.feedbackIcon);

        accountButton.setOnClickListener(view -> showAccountDialog());
        notificationButton.setOnClickListener(view -> showNotificationDialog());
        feedbackButton.setOnClickListener(view -> showFeedbackDialog());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Wait till the data gets synced");

        placedStudentDataLink = getPlacedStudentDataAPILink();
        getPlacedStudentDataFromAPILink();
    }
    public void onBackPressed(){
        this.moveTaskToBack(true);
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.homeMiddleFrameLayout, fragment);
        fragmentTransaction.commit();
    }
    private String getPlacedStudentDataAPILink(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
        return sharedPreferences.getString("dataLink", "") +
                "?UserType=" + sharedPreferences.getString("userType","");
    }
    public void getPlacedStudentDataFromAPILink(){
        progressDialog.show();
        DataFromAPIService dataFromAPIService = new DataFromAPIService(this);
        dataFromAPIService.getStringDataFromApi(placedStudentDataLink, new DataFromAPIService.VolleyStringResponseListener() {
            @Override
            public void OnError(String message) {
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "Error while syncing data", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void OnResponse(String response) {
                placedStudentData = response;
                storePlacedStudentDataLocally();
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "Data synced successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storePlacedStudentDataLocally() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("placedStudentData", placedStudentData);
        editor.apply();
    }

    private void showAccountDialog(){
        accountButton.setImageResource(R.drawable.account_icon_selected);

        AlertDialog.Builder accountDialogBuilder;
        accountDialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        LayoutInflater inflater = getLayoutInflater();
        View view  = inflater.inflate(R.layout.account_dialog, null);
        accountDialogBuilder.setView(view);
        accountDialogBuilder.setCancelable(true);
        AlertDialog accountDialog = accountDialogBuilder.create();
        accountDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        accountDialog.show();
        accountDialog.setOnDismissListener(dialog -> accountButton.setImageResource(R.drawable.account_icon_unselected));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient gsc = GoogleSignIn.getClient(Home.this, gso);

        CircleImageView userImage = accountDialog.findViewById(R.id.userImage);
        TextView userName = accountDialog.findViewById(R.id.userName);
        TextView userEmail = accountDialog.findViewById(R.id.userEmail);
        Button logoutButton = accountDialog.findViewById(R.id.logoutButton);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Home.this);
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
            Toast.makeText(Home.this, "Logout  clicked", Toast.LENGTH_SHORT).show();
            gsc.signOut().addOnCompleteListener(task -> {
                Intent intent = new Intent(Home.this, MainActivity.class);
                Home.this.finish();
                startActivity(intent);
            });
        });

    }
    private void showFeedbackDialog(){
        feedbackButton.setImageResource(R.drawable.feedback_icon_selected);

        AlertDialog.Builder feedbackDialogBuilder;
        feedbackDialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        LayoutInflater inflater = getLayoutInflater();
        View view  = inflater.inflate(R.layout.feedback_dialog, null);
        feedbackDialogBuilder.setView(view);
        feedbackDialogBuilder.setCancelable(true);
        AlertDialog feedbackDialog = feedbackDialogBuilder.create();
        feedbackDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        feedbackDialog.show();
        feedbackDialog.setOnDismissListener(dialog -> feedbackButton.setImageResource(R.drawable.feedback_icon_unselected));

        Button feedbackSubmitButton = feedbackDialog.findViewById(R.id.feedbackSubmitButton);
        assert feedbackSubmitButton != null;
        feedbackSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Home.this, "Feedback submit button clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void showNotificationDialog(){
        notificationButton.setImageResource(R.drawable.notification_icon_selected);

        AlertDialog.Builder notificationDialogBuilder = new AlertDialog.Builder(this);
        notificationDialogBuilder.setMessage("No notifications");
        notificationDialogBuilder.setCancelable(true);

        notificationDialogBuilder.setNegativeButton("Close",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notificationButton.setImageResource(R.drawable.notification_icon_unselected);
                        dialog.cancel();
                    }
                });

        notificationDialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                notificationButton.setImageResource(R.drawable.notification_icon_unselected);
            }
        });

        AlertDialog notificationDialog = notificationDialogBuilder.create();
        notificationDialog.show();
    }

}