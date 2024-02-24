package in.navedansari.tpoapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        if(email==null){
            navigateToLoginScreen();
        }else{
            navigateToHomeScreen();
        }
    }
    private void navigateToLoginScreen() {
        startActivity(new Intent(this, LoginScreen.class));
        finishAffinity();
    }
    private void navigateToHomeScreen() {
        startActivity(new Intent(this, HomeScreen.class));
        finishAffinity();
    }
}