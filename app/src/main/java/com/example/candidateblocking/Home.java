package com.example.candidateblocking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
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
import com.example.candidateblocking.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private ProgressBar syncProgressBar;
    private TextView syncText;
    private String dataLink;
    public static String fetched_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        syncProgressBar = findViewById(R.id.syncProgressBar);
        syncText = findViewById(R.id.syncText);

        replaceFragment(new BlockFragment());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.listIcon:
                    replaceFragment(new BlockFragment());
                    break;
                case R.id.statsIcon:
                    replaceFragment(new StatsFragment());
                    break;
                case R.id.aboutIcon:
                    replaceFragment(new InfoFragment());
                    break;
                case R.id.searchIcon:
                    replaceFragment(new SearchFragment());
                    break;
            }
            return true;
        });

        dataLink = getDataLink();

        fetchDataFromAPI();
    }
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
    private String getDataLink(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
        return sharedPreferences.getString("dataLink", "") + "?UserType=" + sharedPreferences.getString("userType","");
    }
    public void fetchDataFromAPI ()
    {
        syncProgressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, dataLink, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                fetched_data = response;
                syncProgressBar.setVisibility(View.INVISIBLE);
                syncText.setText("");
                Toast.makeText(getApplicationContext(), "Synced SUCCESSFULLY", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                syncProgressBar.setVisibility(View.INVISIBLE);
                syncText.setText("");
                Toast.makeText(getApplicationContext(), "ERROR while Syncing", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext().getApplicationContext());
        requestQueue.add(stringRequest);
    }
}