package com.example.candidateblocking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.hash.Hashing;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "GOOGLEAUTH";
    private GoogleSignInClient gsc;
    private GoogleSignInOptions gso;
    private FirebaseAuth firebaseAuth;
    private SignInButton signInButton;
    private ProgressDialog progressDialog;
    private static String verifyUserID = "";
    private boolean moveToHome = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.signInButton);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Authenticating");


        firebaseAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        checkLogin();

        if(moveToHome==true && account!=null) {
            moveToHome();
        }else if(moveToHome==false && account!=null){
            gsc.signOut();
            signIn();
        }else if(moveToHome==false && account==null){
            signIn();
        }else{
            SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            signIn();
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
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
        String UserType = sharedPreferences.getString("userType", "");
        if(!UserType.equals(""))
            moveToHome = true;
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
                    GoogleSignInAccount account = task.getResult(ApiException.class);
//                    Log.w(TAG, "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                }catch(ApiException e){
                    progressDialog.cancel();
//                    Log.w(TAG, "Google sign in failed", e);
                }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.cancel();
//                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
                            if(account!=null) {
                                progressDialog.setMessage("Verifying User");
                                progressDialog.show();
                                String email = account.getEmail();
                                String salt1 = "";
                                String salt2 = "";
                                String hashedID = Hashing.sha256().hashString(salt1 + email + salt2, StandardCharsets.UTF_8).toString();
                                StringRequest stringRequest = new StringRequest(Request.Method.GET, verifyUserID+"?id="+hashedID , new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        progressDialog.cancel();
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                            String UserType = jsonObject1.getString("userType");
                                            String DataLink = jsonObject1.getString("dataLink");
                                            String FeedbackLink = jsonObject1.getString("feedbackLink");
                                            String SourceCodeLink = jsonObject1.getString("sourceCodeLink");
                                            String Note = jsonObject1.getString("note");
                                            SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("userType", UserType);
                                            editor.putString("dataLink", DataLink);
                                            editor.putString("feedbackLink", FeedbackLink);
                                            editor.putString("sourceCodeLink", SourceCodeLink);
                                            editor.putString("note", Note);
                                            editor.apply();
                                            if(UserType.equals("-1"))
                                                Toast.makeText(getApplicationContext(), "User outside of organization found", Toast.LENGTH_SHORT).show();
                                            else
                                                Toast.makeText(getApplicationContext(), "Credentials found", Toast.LENGTH_SHORT).show();
                                            moveToHome();
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.cancel();
                                        Toast.makeText(getApplicationContext(), "Error : No response", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext().getApplicationContext());
                                requestQueue.add(stringRequest);
                            }
                        }else{
                            progressDialog.cancel();
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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