package com.example.candidateblocking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.example.candidateblocking.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ProgressBar progressBar;
    TextView textView;
    static String fetched_data;
    private String URL = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.textView);

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
                    replaceFragment(new AboutFragment());
                    break;
                case R.id.searchIcon:
                    replaceFragment(new SearchFragment());
                    break;
            }
            return true;
        });
        fetchDataFromAPI();
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    protected void fetchDataFromAPI ()
    {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println("Response : " + response);
                fetched_data = response;
                progressBar.setVisibility(View.INVISIBLE);
                textView.setText("");
                Toast.makeText(MainActivity.this, "Synced SUCCESSFULLY", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.INVISIBLE);
                textView.setText("");
                Toast.makeText(MainActivity.this, "ERROR while Syncing", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this.getApplicationContext());
        requestQueue.add(stringRequest);
    }

}