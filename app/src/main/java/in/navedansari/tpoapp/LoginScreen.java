package in.navedansari.tpoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.time.LocalDate;
import in.navedansari.tpoapp.databinding.ActivityLoginScreenBinding;
import in.navedansari.tpoapp.models.LoginRequest;
import in.navedansari.tpoapp.models.LoginResponse;
import in.navedansari.tpoapp.utils.ApiService;
import in.navedansari.tpoapp.utils.Internet;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginScreen extends AppCompatActivity {
    private ActivityLoginScreenBinding binding;
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalData.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        binding.loginButton.setOnClickListener(view -> login());
    }
    private void login(){
        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        if(email.equals("")){
            Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show();
            return;
        }else if(password.equals("")){
            Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show();
            return;
        }else if(!Internet.internetIsConnected()){
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            return;
        }
        setControlsEnabled(false);
        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<LoginResponse> call = apiService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()){
                    SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", email);
                    editor.putString("token", response.body().getToken());
                    LocalDate currentDate = LocalDate.now();
                    int currentYear = currentDate.getYear();
                    editor.putString("year", String.valueOf(currentYear));
                    editor.putString("type", "placement");
                    editor.apply();
                    navigateToHomeScreen();
                }else{
                    try {
                        Toast.makeText(getApplicationContext(), response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    setControlsEnabled(true);
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Unable to connect with server", Toast.LENGTH_SHORT).show();
                setControlsEnabled(true);
            }
        });
    }
    private void setControlsEnabled(boolean enabled){
        binding.progressBar.setVisibility(enabled?View.INVISIBLE:View.VISIBLE);
        binding.loginButton.setEnabled(enabled);
        binding.email.setEnabled(enabled);
        binding.password.setEnabled(enabled);
    }
    private void navigateToHomeScreen(){
        startActivity(new Intent(this, HomeScreen.class));
        finishAffinity();
    }
}