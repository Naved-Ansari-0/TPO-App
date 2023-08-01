package com.example.candidateblocking;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private GoogleSignInAccount account;
    private static final int RC_SIGN_IN = 1;
    private SignInButton signInButton;
    private Button privacyPolicy;
    private ProgressDialog progressDialog;
    private static String verifyUserID = "";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        signInButton = findViewById(R.id.signInButton);
        privacyPolicy = findViewById(R.id.privacyPolicy);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Authenticating");

        firebaseAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);

        if(account!=null)
            moveToHome();

        signInButton.setOnClickListener(view -> signIn());
        privacyPolicy.setOnClickListener( view -> {
                String url = "";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        );
    }

    private void moveToHome() {
        startActivity(new Intent(this, Home.class));
        finish();
    }

    private void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_SIGN_IN){
                if(checkConnection()==false)
                    return;
                progressDialog.show();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try{
                    account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                }catch(ApiException e){
                    FirebaseAuth.getInstance().signOut();
                    signOutAndClearSP();
                    progressDialog.cancel();
                }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        progressDialog.cancel();
                        account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
                        if(account!=null) {
                            progressDialog.setMessage("Checking credentials");
                            progressDialog.show();
                            String email = account.getEmail();
                            String salt1 = "";
                            String salt2 = "";
                            String hashedID = Hashing.sha256().hashString(salt1 + email + salt2, StandardCharsets.UTF_8).toString();
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, verifyUserID+"?id="+hashedID+"/"+email, response -> {
                                progressDialog.cancel();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                    String userType = jsonObject1.getString("userType");
                                    String dataLink = jsonObject1.getString("dataLink");
                                    String feedbackLink = jsonObject1.getString("feedbackLink");
                                    String note = jsonObject1.getString("note");
                                    SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("userType", userType);
                                    editor.putString("dataLink", dataLink);
                                    editor.putString("feedbackLink", feedbackLink);
                                    editor.putString("note", note);
                                    editor.apply();
                                    if(userType.equals("-1"))
                                        Toast.makeText(getApplicationContext(), "User outside of organization found", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(getApplicationContext(), "Credentials found", Toast.LENGTH_SHORT).show();
                                    moveToHome();
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }, error -> {
                                FirebaseAuth.getInstance().signOut();
                                signOutAndClearSP();
                                progressDialog.cancel();
                                Toast.makeText(getApplicationContext(), "Error while checking credentials", Toast.LENGTH_SHORT).show();
                            });
                            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext().getApplicationContext());
                            requestQueue.add(stringRequest);
                        }
                    }else{
                        FirebaseAuth.getInstance().signOut();
                        signOutAndClearSP();
                        progressDialog.cancel();
                        Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(view->{
                    FirebaseAuth.getInstance().signOut();
                    signOutAndClearSP();
                    progressDialog.cancel();
                    Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                });
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

    private void signOutAndClearSP(){
        gsc.signOut().addOnCompleteListener(task -> {
            SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        });
    }

}