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
import android.widget.TextView;
import in.navedansari.tpoapp.databinding.ActivityHomeScreenBinding;
import in.navedansari.tpoapp.home.AdminFragment;
import in.navedansari.tpoapp.home.ListFragment;
import in.navedansari.tpoapp.home.StatsFragment;

public class HomeScreen extends AppCompatActivity {
    private ActivityHomeScreenBinding binding;
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

        email.setText(emailText);
        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(this, LoginScreen.class));
            finishAffinity();
        });
    }
}