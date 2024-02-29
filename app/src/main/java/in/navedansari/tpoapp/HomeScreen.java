package in.navedansari.tpoapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import in.navedansari.tpoapp.databinding.ActivityHomeScreenBinding;
import in.navedansari.tpoapp.home.AdminFragment;
import in.navedansari.tpoapp.home.ListFragment;
import in.navedansari.tpoapp.home.StatsFragment;
import in.navedansari.tpoapp.models.LoginRequest;
import in.navedansari.tpoapp.models.LoginResponse;
import in.navedansari.tpoapp.utils.ApiService;
import in.navedansari.tpoapp.utils.Internet;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeScreen extends AppCompatActivity {
    private ActivityHomeScreenBinding binding;
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeScreenBinding.inflate(getLayoutInflater());
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
                case R.id.adminIcon:
                    replaceFragment(new AdminFragment());
                    break;
            }
            return true;
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalData.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        binding.accountIcon.setOnClickListener(view -> showAccountDialog());
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(binding.frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
    private void showAccountDialog(){
        binding.accountIcon.setImageResource(R.drawable.account_icon_selected);

        AlertDialog.Builder accountDialogBuilder;
        accountDialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        LayoutInflater inflater = getLayoutInflater();
        View view  = inflater.inflate(R.layout.dialog_account, null);
        accountDialogBuilder.setView(view);
        AlertDialog accountDialog = accountDialogBuilder.create();
        accountDialog.setCancelable(true);
        accountDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        accountDialog.show();
        accountDialog.setOnDismissListener(dialog -> binding.accountIcon.setImageResource(R.drawable.account_icon_unselected));

        SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
        String emailText = sharedPreferences.getString("email", "");

        TextView email = accountDialog.findViewById(R.id.email);
        Button logoutButton = accountDialog.findViewById(R.id.logoutButton);
        Button changePasswordButton = accountDialog.findViewById(R.id.changePasswordButton);

        email.setText(emailText);
        logoutButton.setOnClickListener(v -> logout());
        changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder changePasswordDialogBuilder;
        changePasswordDialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        LayoutInflater inflater = getLayoutInflater();
        View view  = inflater.inflate(R.layout.dialog_change_password, null);
        changePasswordDialogBuilder.setView(view);
        AlertDialog changePasswordDialog = changePasswordDialogBuilder.create();
        changePasswordDialog.setCancelable(false);
        changePasswordDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        changePasswordDialog.show();

        TextView currentPassword = changePasswordDialog.findViewById(R.id.currentPassword);
        TextView newPassword = changePasswordDialog.findViewById(R.id.newPassword);
        ImageButton cancelButton = changePasswordDialog.findViewById(R.id.cancelButton);
        Button changePasswordButton = changePasswordDialog.findViewById(R.id.changePasswordButton);

        cancelButton.setOnClickListener(v -> changePasswordDialog.dismiss());

        changePasswordButton.setOnClickListener(v -> {
            String password = currentPassword.getText().toString().trim();
            String newpassword = newPassword.getText().toString().trim();
            if(password.equals("") || newpassword.equals("")){
                Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if(newpassword.length()<8){
                Toast.makeText(this, "New Password is too short", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!Internet.internetIsConnected()){
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");
            cancelButton.setEnabled(false);
            currentPassword.setEnabled(false);
            newPassword.setEnabled(false);
            changePasswordButton.setEnabled(false);
            changePasswordButton.setText("Changing...");
            LoginRequest loginRequest = new LoginRequest(email, password, newpassword);
            Call<LoginResponse> call = apiService.login(loginRequest);
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if(response.isSuccessful()){
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", response.body().getToken());
                        editor.apply();
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        changePasswordDialog.dismiss();
                    }else{
                        try {
                            Toast.makeText(getApplicationContext(), response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        cancelButton.setEnabled(true);
                        currentPassword.setEnabled(true);
                        newPassword.setEnabled(true);
                        changePasswordButton.setEnabled(true);
                        changePasswordButton.setText("Change");
                    }
                }
                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Unable to connect with server", Toast.LENGTH_SHORT).show();
                    cancelButton.setEnabled(true);
                    currentPassword.setEnabled(true);
                    newPassword.setEnabled(true);
                    changePasswordButton.setEnabled(true);
                    changePasswordButton.setText("Change");
                }
            });
        });
    }

    private void logout(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(this, LoginScreen.class));
        finishAffinity();
    }
}