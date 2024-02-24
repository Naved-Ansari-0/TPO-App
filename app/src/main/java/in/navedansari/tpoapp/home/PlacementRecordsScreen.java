package in.navedansari.tpoapp.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.Arrays;
import in.navedansari.tpoapp.adapters.PlacementRecordRecViewAdapter;
import in.navedansari.tpoapp.databinding.ActivityPlacementRecordsScreenBinding;
import in.navedansari.tpoapp.models.PlacementRecord;

public class PlacementRecordsScreen extends AppCompatActivity {
    private ActivityPlacementRecordsScreenBinding binding;
    ArrayList <PlacementRecord> placementRecords;
    PlacementRecordRecViewAdapter adapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlacementRecordsScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra("placementRecords");
        PlacementRecord[] placementRecordsArray = Arrays.copyOf(parcelables, parcelables.length, PlacementRecord[].class);
        placementRecords = new ArrayList<>(Arrays.asList(placementRecordsArray));

        binding.recordsCount.setText(String.valueOf("Total : " + placementRecords.size()));
        binding.recordsRecView.setLayoutManager(new LinearLayoutManager(PlacementRecordsScreen.this));
        adapter = new PlacementRecordRecViewAdapter();
        binding.recordsRecView.setAdapter(adapter);
        adapter.setPlacedStudents(placementRecords);
    }
}