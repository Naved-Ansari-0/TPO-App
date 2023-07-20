package com.example.candidateblocking.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.candidateblocking.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminFragment extends Fragment {

    private TextView lastSyncedTimeTextView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lastSyncedTimeTextView = view.findViewById(R.id.lastSyncedTimeTextView);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("shared_pref", MODE_PRIVATE);
        long lastSyncedTime = sharedPreferences.getLong("lastSyncedTime", -1);
        if(lastSyncedTime!=-1){
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM, yyyy 'at' h:mm a");
            String formattedDate = sdf.format(new Date(lastSyncedTime));
            lastSyncedTimeTextView.setText("Data synced on " + formattedDate.toString());
        }

    }
}