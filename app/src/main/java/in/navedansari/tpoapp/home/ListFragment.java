package in.navedansari.tpoapp.home;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import in.navedansari.tpoapp.GlobalData;
import in.navedansari.tpoapp.HomeScreen;
import in.navedansari.tpoapp.LoginScreen;
import in.navedansari.tpoapp.databinding.FragmentListBinding;
import in.navedansari.tpoapp.models.DataRequest;
import in.navedansari.tpoapp.models.PlacementRecord;
import in.navedansari.tpoapp.utils.ApiService;
import in.navedansari.tpoapp.utils.Internet;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListFragment extends Fragment {
    private FragmentListBinding binding;
    private ApiService apiService;
    List<PlacementRecord> placementRecords;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        placementRecords = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalData.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        binding.searchButton.setOnClickListener(v-> checkFilters());

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared_pref", MODE_PRIVATE);
        String year = sharedPreferences.getString("year", "");
        binding.year.setText(year);
    }

    private void checkFilters(){

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared_pref", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String token = sharedPreferences.getString("token", "");
        String type = sharedPreferences.getString("type", "");

        String year = binding.year.getText().toString().trim();
        if(year.equals("")){
            Toast.makeText(requireContext(), "Year field is mandatory", Toast.LENGTH_SHORT).show();
            return;
        }

        String rollNo = binding.rollNo.getText().toString().trim();

        String name = binding.name.getText().toString().trim();

        String gender = "";
        if(binding.maleRadioButton.isChecked())
            gender = binding.maleRadioButton.getText().toString();
        else if(binding.femaleRadioButton.isChecked())
            gender = binding.femaleRadioButton.getText().toString();

        ArrayList <String> courses = new ArrayList();
        if(binding.btechCheckBox.isChecked())
            courses.add(binding.btechCheckBox.getText().toString());
        if(binding.mtechCheckBox.isChecked())
            courses.add(binding.mtechCheckBox.getText().toString());
        if(binding.mcaCheckBox.isChecked())
            courses.add(binding.mcaCheckBox.getText().toString());
        String course = "";
        if(courses.size()>0)
            course = String.join(",", courses);

        ArrayList <String> branches = new ArrayList();
        if(binding.ceCheckBox.isChecked())
            branches.add(binding.ceCheckBox.getText().toString());
        if(binding.cseCheckBox.isChecked())
            branches.add(binding.cseCheckBox.getText().toString());
        if(binding.eeCheckBox.isChecked())
            branches.add(binding.eeCheckBox.getText().toString());
        if(binding.elCheckBox.isChecked())
            branches.add(binding.elCheckBox.getText().toString());
        if(binding.meCheckBox.isChecked())
            branches.add(binding.meCheckBox.getText().toString());
        if(binding.itCheckBox.isChecked())
            branches.add(binding.itCheckBox.getText().toString());
        String branch = "";
        if(branches.size()>0)
            branch = String.join(",", branches);

        String company = binding.company.getText().toString().trim();

        String drive = "";
        if(binding.onCampusRadioButton.isChecked())
            drive = binding.onCampusRadioButton.getText().toString();
        else if(binding.offCampusRadioButton.isChecked())
            drive = binding.offCampusRadioButton.getText().toString();
        else if(binding.poolCampusRadioButton.isChecked())
            drive = binding.poolCampusRadioButton.getText().toString();

        String minctc = binding.minCTC.getText().toString().trim();
        String maxctc = binding.maxCTC.getText().toString().trim();

        DataRequest dataRequest = new DataRequest(
                email,
                token,
                year,
                type,
                rollNo,
                name,
                gender,
                course,
                branch,
                company,
                drive,
                minctc,
                maxctc
        );

        if(!Internet.internetIsConnected()){
            Toast.makeText(requireContext(), "No Internet", Toast.LENGTH_SHORT).show();
            return;
        }

        getData(dataRequest);
    }

    private void setControlsEnabled(boolean enabled){
        binding.year.setEnabled(enabled);
        binding.rollNo.setEnabled(enabled);
        binding.name.setEnabled(enabled);
        binding.maleRadioButton.setEnabled(enabled);
        binding.femaleRadioButton.setEnabled(enabled);
        binding.bothGenderRadioButton.setEnabled(enabled);
        binding.btechCheckBox.setEnabled(enabled);
        binding.mtechCheckBox.setEnabled(enabled);
        binding.mcaCheckBox.setEnabled(enabled);
        binding.ceCheckBox.setEnabled(enabled);
        binding.cseCheckBox.setEnabled(enabled);
        binding.eeCheckBox.setEnabled(enabled);
        binding.elCheckBox.setEnabled(enabled);
        binding.meCheckBox.setEnabled(enabled);
        binding.itCheckBox.setEnabled(enabled);
        binding.company.setEnabled(enabled);
        binding.onCampusRadioButton.setEnabled(enabled);
        binding.offCampusRadioButton.setEnabled(enabled);
        binding.poolCampusRadioButton.setEnabled(enabled);
        binding.allCampusRadioButton.setEnabled(enabled);
        binding.minCTC.setEnabled(enabled);
        binding.maxCTC.setEnabled(enabled);
        binding.searchButton.setEnabled(enabled);
        binding.progressBar.setVisibility(enabled?View.INVISIBLE:View.VISIBLE);
    }

    private void getData(DataRequest dataRequest){
        setControlsEnabled(false);
        Call<List<PlacementRecord>> call = apiService.getData(
                dataRequest.getEmail(),
                dataRequest.getToken(),
                dataRequest.getYear(),
                dataRequest.getType(),
                dataRequest.getRollNo(),
                dataRequest.getName(),
                dataRequest.getGender(),
                dataRequest.getCourse(),
                dataRequest.getBranch(),
                dataRequest.getCompany(),
                dataRequest.getDrive(),
                dataRequest.getMinctc(),
                dataRequest.getMaxctc()
        );
        call.enqueue(new Callback<List<PlacementRecord>>() {
            @Override
            public void onResponse(Call<List<PlacementRecord>> call, Response<List<PlacementRecord>> response) {
                if(!isAdded())
                    return;
                if(response.isSuccessful()){
                    placementRecords = response.body();
                    setControlsEnabled(true);
                    navigateToPlacementRecordsScreen();
                }else{
                    try {
                        Toast.makeText(requireContext(), response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        if(response.code()==401){
                            logout();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    setControlsEnabled(true);
                }
            }
            @Override
            public void onFailure(Call<List<PlacementRecord>> call, Throwable t) {
                if(!isAdded())
                    return;
                Toast.makeText(requireContext(), "Unable to connect with server", Toast.LENGTH_LONG).show();
                setControlsEnabled(true);
            }
        });
    }
    private void logout(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("shared_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(requireContext(), LoginScreen.class));
        requireActivity().finishAffinity();
    }

    private void navigateToPlacementRecordsScreen(){
        Intent intent = new Intent(requireContext(), PlacementRecordsScreen.class);
        PlacementRecord[] placementRecordsArray = placementRecords.toArray(new PlacementRecord[0]);
        intent.putExtra("placementRecords", placementRecordsArray);
        startActivity(intent);
    }


}