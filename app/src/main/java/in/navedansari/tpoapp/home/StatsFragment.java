package in.navedansari.tpoapp.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import in.navedansari.tpoapp.GlobalData;
import in.navedansari.tpoapp.LoginScreen;
import in.navedansari.tpoapp.databinding.FragmentStatsBinding;
import in.navedansari.tpoapp.models.DataRequest;
import in.navedansari.tpoapp.models.PlacedIn;
import in.navedansari.tpoapp.models.PlacementRecord;
import in.navedansari.tpoapp.utils.ApiService;
import in.navedansari.tpoapp.utils.Internet;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StatsFragment extends Fragment {
    private FragmentStatsBinding binding;
    private ApiService apiService;
    List<PlacementRecord> placementRecords;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalData.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        binding.refreshButton.setOnClickListener(v -> getData());

        getData();
    }

    private void getData(){
        setControlsEnabled(false);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared_pref", MODE_PRIVATE);
        DataRequest dataRequest = new DataRequest();
        dataRequest.setEmail(sharedPreferences.getString("email", ""));
        dataRequest.setToken(sharedPreferences.getString("token", ""));
        dataRequest.setYear(sharedPreferences.getString("year", ""));
        dataRequest.setType(sharedPreferences.getString("type", ""));
        if(!Internet.internetIsConnected()){
            Toast.makeText(requireContext(), "No Internet", Toast.LENGTH_SHORT).show();
            setControlsEnabled(true);
            return;
        }
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
                    updateUI();
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.scrollView.setVisibility(View.VISIBLE);
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

    private void setControlsEnabled(boolean enabled){
        binding.progressBar.setVisibility(enabled?View.INVISIBLE:View.VISIBLE);
        binding.refreshButton.setVisibility(enabled?View.VISIBLE:View.INVISIBLE);
    }

    private void updateUI(){
        Map<String, Integer> companyWisePlacedStudents = new HashMap<>();
        Map<String, Integer> branchWisePlacedStudents = new HashMap<>();
        Map<String, Integer> branchWisePackagesOffered = new HashMap<>();

        Map<String, Float> branchWiseHighestPackage = new HashMap<>();
        Map<String, Float> branchWisePackagesSum = new HashMap<>();
        Map<String, Float> branchWiseAveragePackage = new HashMap<>();

//------Initializing above maps

        for(int i=0; i<placementRecords.size(); i++) {
            String course = placementRecords.get(i).getCourse();
            String branch = placementRecords.get(i).getBranch();
            branch = course + " " + branch;

            List<String> companies = new ArrayList<>();
            List<Float> packages = new ArrayList<>();
            for (PlacedIn placedIn : placementRecords.get(i).getPlacedIn()) {
                    companies.add(placedIn.getCompany());
                    packages.add(placedIn.getCtc());
            }

            if (branchWisePlacedStudents.containsKey(branch)) {
                branchWisePlacedStudents.put(branch, branchWisePlacedStudents.get(branch) + 1);
            }else{
                branchWisePlacedStudents.put(branch, 1);
            }

            if (branchWisePackagesOffered.containsKey(branch)){
                branchWisePackagesOffered.put(branch, branchWisePackagesOffered.get(branch) + packages.size());
            }else{
                branchWisePackagesOffered.put(branch, packages.size());
            }

            for(String company: companies){
                if(companyWisePlacedStudents.containsKey(company)) {
                    companyWisePlacedStudents.put(company, companyWisePlacedStudents.get(company) + 1);
                }else {
                    companyWisePlacedStudents.put(company, 1);
                }
            }

            float highestPackage = (float)0.0;
            float packagesSum = (float)0.0;
            for(Float packageAmount: packages){
                if(packageAmount>highestPackage)
                    highestPackage = packageAmount;
                packagesSum += packageAmount;
            }

            if(branchWiseHighestPackage.containsKey(branch)){
                if(branchWiseHighestPackage.get(branch)<highestPackage)
                    branchWiseHighestPackage.put(branch, highestPackage);
            }else{
                branchWiseHighestPackage.put(branch, highestPackage);
            }

            if(branchWisePackagesSum.containsKey(branch)) {
                branchWisePackagesSum.put(branch, branchWisePackagesSum.get(branch) + packagesSum);
            }else {
                branchWisePackagesSum.put(branch, packagesSum);
            }
        }

        for(Map.Entry<String, Float> ele: branchWisePackagesSum.entrySet()){
            branchWiseAveragePackage.put(ele.getKey(), ele.getValue()/branchWisePackagesOffered.get(ele.getKey()));
        }

//------Creating array data for all charts

        // Bar Chart arrays
        List companiesList = new ArrayList<>();
        List companyWisePlacedStudentsList = new ArrayList<>();
        int position = 0;
        for(Map.Entry<String,Integer> ele : companyWisePlacedStudents.entrySet()){
            companiesList.add(ele.getKey());
            companyWisePlacedStudentsList.add(new BarEntry(position, ele.getValue()));
            position++;
        }

        // Pie Chart arrays
        List <PieEntry> branchWisePlacedStudentsList = new ArrayList<>();
        List <PieEntry> branchWisePackagesOfferedList = new ArrayList<>();
        for(Map.Entry<String, Integer> ele: branchWisePlacedStudents.entrySet()){
            branchWisePlacedStudentsList.add(new PieEntry(ele.getValue(), ele.getKey()));
        }
        for(Map.Entry<String, Integer> ele: branchWisePackagesOffered.entrySet()){
            branchWisePackagesOfferedList.add(new PieEntry(ele.getValue(), ele.getKey()));
        }

//------Initializing Bar Chart

        BarDataSet barDataSet = new BarDataSet(companyWisePlacedStudentsList, "Company Wise Placed Students");
        barDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        barDataSet.setValueTextSize(10f);
        barDataSet.setValueTextColor(Color.BLACK);
        BarData barData = new BarData(barDataSet);
        barData.setValueFormatter(new IntegerFormatter());
//        barData.setBarWidth(1f);

        BarChart barChartCompanies = getView().findViewById(binding.barChartCompanies.getId());
        int barChartHeight = companiesList.size()*75;
        barChartCompanies.setMinimumHeight(barChartHeight);
        barChartCompanies.setData(barData);
        barChartCompanies.setDescription(null);
        barChartCompanies.setPinchZoom(false);
        barChartCompanies.setScaleEnabled(false);
        barChartCompanies.setDrawBarShadow(false);
        barChartCompanies.setDrawGridBackground(false);

//        barChartCompanies.setTouchEnabled(false);
//        barChartCompanies.getLegend().setEnabled(false);
//        barChartCompanies.getAxisRight().setDrawGridLines(false);
//        barChartCompanies.getAxisRight().setDrawAxisLine(false);
//        barChartCompanies.getAxisRight().setDrawLabels(false);
        barChartCompanies.getAxisLeft().setDrawGridLines(false);
        barChartCompanies.getAxisLeft().setDrawAxisLine(false);
        barChartCompanies.getAxisLeft().setDrawLabels(false);
//        barChartCompanies.getXAxis().setDrawGridLines(false);
        barChartCompanies.getXAxis().setDrawAxisLine(false);
//        barChartCompanies.getXAxis().setDrawLabels(false);
//        barChartCompanies.animate();

//        Legend l = barChartCompanies.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(true);
//        l.setYOffset(20f);
//        l.setXOffset(0f);
//        l.setYEntrySpace(0f);
//        l.setTextSize(8f);

        XAxis xAxis = barChartCompanies.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(false);
        xAxis.setLabelCount(companiesList.size());
        xAxis.setAxisMaximum(companiesList.size());
        xAxis.setAxisMinimum(0);
        xAxis.setTextSize(10f);
        barChartCompanies.setFitBars(true);
        xAxis.setAxisMinimum(-0.5f);

        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(companiesList.size());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(companiesList));

        barChartCompanies.getAxisRight().setEnabled(false);
        YAxis leftAxis = barChartCompanies.getAxisLeft();
//        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
//        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);


//------Initializing Pie Chart 1

        PieDataSet pieDataSetStudents = new PieDataSet(branchWisePlacedStudentsList, "Branch Wise Students Placed");
        PieData pieDataStudents = new PieData(pieDataSetStudents);

        PieChart pieChartStudents = getView().findViewById(binding.pieChartStudents.getId());
        pieChartStudents.setData(pieDataStudents);
        pieDataSetStudents.setValueFormatter(new IntegerFormatter());
        pieDataSetStudents.setDrawValues(true);
        pieDataSetStudents.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSetStudents.setValueTextColor(Color.WHITE);
        pieDataSetStudents.setValueTextSize(12f);
        pieChartStudents.setEntryLabelTextSize(12f);
        pieChartStudents.getDescription().setEnabled(false);
        pieChartStudents.getLegend().setEnabled(false);
        pieChartStudents.setCenterText("Branch Wise Students Placed");
        pieChartStudents.setCenterTextSize(12f);
        pieChartStudents.setCenterTextColor(Color.GRAY);
        pieChartStudents.animate();

//------Initializing Pie Chart 2

        PieDataSet pieDataSetOffers = new PieDataSet(branchWisePackagesOfferedList, "Branch Wise Packages Offered");
        PieData pieDataOffers = new PieData(pieDataSetOffers);

        PieChart pieChartOffers = getView().findViewById(binding.pieChartOffers.getId());
        pieChartOffers.setData(pieDataOffers);
        pieDataSetOffers.setValueFormatter(new IntegerFormatter());
        pieDataSetOffers.setDrawValues(true);
        pieDataSetOffers.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSetOffers.setValueTextColor(Color.WHITE);
        pieDataSetOffers.setValueTextSize(12f);
        pieChartOffers.setEntryLabelTextSize(12f);
        pieChartOffers.getDescription().setEnabled(false);
        pieChartOffers.getLegend().setEnabled(false);
        pieChartOffers.setCenterText("Branch Wise Packages Offered");
        pieChartOffers.setCenterTextSize(12f);
        pieChartOffers.setCenterTextColor(Color.GRAY);
        pieChartOffers.animate();

//------Showing stats in TEXTVIEW
        showPackageStats(
                branchWiseHighestPackage,
                branchWiseAveragePackage,
                branchWisePackagesSum,
                branchWisePackagesOffered,
                branchWisePlacedStudents);
    }

    public class IntegerFormatter extends ValueFormatter {
        private DecimalFormat mFormat;
        public IntegerFormatter() {
            mFormat = new DecimalFormat("###,##0");
        }
        @Override
        public String getBarLabel(BarEntry barEntry) {
            return mFormat.format(barEntry.getY());
        }
        @Override
        public String getFormattedValue(float value) {
            return "" + ((int) value);
        }
    }

    public class FloatFormatter extends ValueFormatter {
        private DecimalFormat mFormat;
        public FloatFormatter() {
            mFormat = new DecimalFormat("0.0");
        }
        @Override
        public String getBarLabel(BarEntry barEntry) {
            return mFormat.format(barEntry.getY());
        }
        @Override
        public String getFormattedValue(float value) {
            return mFormat.format(value);
        }
    }

    private void showPackageStats(Map<String, Float> branchWiseHighestPackage,
                                  Map<String, Float> branchWiseAveragePackage,
                                  Map<String, Float> branchWisePackagesSum,
                                  Map<String, Integer> branchWisePackagesOffered,
                                  Map<String, Integer> branchWisePlacedStudents)
    {
        String str = "";

        for(Map.Entry<String, Float> ele: branchWiseHighestPackage.entrySet()){
            str += "Branch : " + ele.getKey() + "\n" +
                    "Highest Package : " + ele.getValue() + " lpa\n" +
                    "Average Package : " +  String.format("%.2f",branchWiseAveragePackage.get(ele.getKey())) + " lpa\n\n";
        }

        int totalStudentsPlaced = 0, totalPackagesOffered = 0;
        float highestPackage = 0, packagesSum = 0;
        for(Map.Entry<String,Integer> ele: branchWisePlacedStudents.entrySet()){
            totalStudentsPlaced += ele.getValue();
            totalPackagesOffered += branchWisePackagesOffered.get(ele.getKey());
            packagesSum += branchWisePackagesSum.get(ele.getKey());
            highestPackage = Math.max(highestPackage, branchWiseHighestPackage.get(ele.getKey()));
        }
        
        float averagePackage = packagesSum/totalPackagesOffered;
    
        str += "\nTotal students placed : " + totalStudentsPlaced + "\n" +
                "Total packages offered : " + totalPackagesOffered + "\n" +
                "Highest package : " + highestPackage + " lpa\n" +
                "Average package : " +String.format("%.2f",averagePackage) + " lpa\n\n";

        binding.packageStatsTextView.setText(str);
    }
}

