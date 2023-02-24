package com.example.candidateblocking;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.tasks.Task;
import com.google.common.hash.Hashing;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private static String verifyUserID = "";
    private static String userType = "userType";
    private static String dataLink = "dataLink";
    private static String feedbackLink = "feedbackLink";
    private static String sourceCodeLink = "sourceCodeLink";
    private static String note = "note";
    private boolean moveToHome = false;
    private SignInButton loginButton;
    private ProgressBar verifyProgressBar;
    private TextView verifyText, loginText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.loginButton);
        verifyProgressBar = findViewById(R.id.verifyProgressBar);
        verifyText = findViewById(R.id.verifyText);
        loginText = findViewById(R.id.loginText);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        checkLogin();

        if(moveToHome==true && account!=null) {
            moveToHome();
        }else if(moveToHome==false && account!=null){
            gsc.signOut();
//            signIn();
        }else if(moveToHome==false && account==null){
//            signIn();
        }else{
            SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
//            signIn();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gsc.signOut();
                SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                signIn();
            }
        });
    }

    private void checkLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
        String UserType = sharedPreferences.getString(userType, "");
        if(!UserType.equals(""))
            moveToHome = true;
    }

    private void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1000){
            try {

                if(checkConnection()==false){
//                    loginButton.setVisibility(View.VISIBLE);
                    return;
                }
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                task.getResult(ApiException.class);
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                if(account!=null) {
                    String email = account.getEmail();
                    String salt1 = "";
                    String salt2 = "";
                    String hashedID = Hashing.sha256().hashString(salt1 + email + salt2, StandardCharsets.UTF_8).toString();
                    loginButton.setVisibility(View.GONE);
                    loginText.setVisibility(View.GONE);
                    verifyProgressBar.setVisibility(View.VISIBLE);
                    verifyText.setText("Checking credentials");
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, verifyUserID+"?id="+hashedID , new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                String UserType = jsonObject1.getString(userType);
                                String DataLink = jsonObject1.getString(dataLink);
                                String FeedbackLink = jsonObject1.getString(feedbackLink);
                                String SourceCodeLink = jsonObject1.getString(sourceCodeLink);
                                String Note = jsonObject1.getString(note);
                                SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(userType, UserType);
                                editor.putString(dataLink, DataLink);
                                editor.putString(feedbackLink, FeedbackLink);
                                editor.putString(sourceCodeLink, SourceCodeLink);
                                editor.putString(note, Note);
                                editor.apply();
                                verifyProgressBar.setVisibility(View.INVISIBLE);
                                if(UserType.equals("-1"))
                                    Toast.makeText(getApplicationContext(), "User outside of organization found", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getApplicationContext(), "Credentials found", Toast.LENGTH_SHORT).show();
                                loginButton.setVisibility(View.VISIBLE);
                                loginText.setVisibility(View.VISIBLE);
                                verifyText.setText("");
                                moveToHome();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            verifyProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Error : No response", Toast.LENGTH_SHORT).show();
                            loginButton.setVisibility(View.VISIBLE);
                            loginText.setVisibility(View.VISIBLE);
                            verifyText.setText("");
                        }
                    });
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext().getApplicationContext());
                    requestQueue.add(stringRequest);
                }else{

                }
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void moveToHome() {
        finish();
        Intent intent = new Intent(getApplication().getApplicationContext(), Home.class);
        startActivity(intent);
    }
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkConnection(){
        if(isNetworkConnected() && internetIsConnected())
                return true;
        else{
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}