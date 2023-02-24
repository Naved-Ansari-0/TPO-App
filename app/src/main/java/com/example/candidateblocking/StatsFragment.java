package com.example.candidateblocking;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment {

    TextView totalStudentPlaced, totalPackagesOffered;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StatsFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        totalStudentPlaced = getView().findViewById(R.id.studentPlaced);
        totalPackagesOffered = getView().findViewById(R.id.packagesOffered);

        int noOfStudentPlaced = 0;
        int noOfPackagesOffered = 0;

        Map<String,Integer> BranchWiseCount = new HashMap<>();
        Map<String,Integer> CompanyWiseCount = new HashMap<>();
        Map<String, Float> BranchWiseHighest = new HashMap<>();
        Map<String, Float> BranchWiseSum = new HashMap<>();
        Map<String, Integer> BranchWisePackage = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(Home.fetched_data);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String branch = jsonObject1.getString("Branch");
                    String companies = jsonObject1.getString("Companies");
                    String companyList[] = companies.split("/", 0);
                    String packages = jsonObject1.getString("Packages");
                    String[] packagesList = packages.split("/", 0);
                    float max = 0;
                    float sum = 0;
                    for(String p: packagesList){
                        String t = p.replace("+", "");
                        float _package = Float.parseFloat(t);
                        if(_package>max)
                            max = _package;
                        sum += _package;
                    }
                    if(BranchWiseCount.containsKey(branch)){
                        BranchWiseCount.put(branch, BranchWiseCount.get(branch)+1);
                        if(max>BranchWiseHighest.get(branch))
                            BranchWiseHighest.put(branch, max);
                        BranchWiseSum.put(branch, BranchWiseSum.get(branch)+sum);
                        BranchWisePackage.put(branch, BranchWisePackage.get(branch)+packagesList.length);
                    }else{
                        BranchWiseCount.put(branch, 1);
                        BranchWiseHighest.put(branch, max);
                        BranchWiseSum.put(branch, sum);
                        BranchWisePackage.put(branch, packagesList.length);
                    }
                    for(int company=0; company<companyList.length; company++){
                        if(CompanyWiseCount.containsKey(companyList[company])){
                            CompanyWiseCount.put(companyList[company], CompanyWiseCount.get(companyList[company])+1);
                        }else{
                            CompanyWiseCount.put(companyList[company], 1);
                        }
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }

        ArrayList CompanyWiseCountList = new ArrayList<>();
        ArrayList CompanyNamesList = new ArrayList<>();
        int x = 0;
        System.out.println("\nCompany wise no of placed students");
        for(Map.Entry<String,Integer> ele : CompanyWiseCount.entrySet()){
            System.out.println(ele.getKey() + " : " + ele.getValue());
            CompanyWiseCountList.add(new BarEntry(x, ele.getValue()));
            String[] temp = ele.getKey().split(" ", 0);
            CompanyNamesList.add(temp[0]);
            x++;
            noOfPackagesOffered += ele.getValue();
        }

        BarChart barChart = getView().findViewById(R.id.barChart);

        barChart.setDescription(null);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);

        BarDataSet barDataSet = new BarDataSet(CompanyWiseCountList, "Company Wise");
        BarData barData = new BarData(barDataSet);

        barData.setValueFormatter(new IntegerFormatter());
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        barDataSet.setValueTextSize(12f);
        barDataSet.setValueTextColor(Color.BLACK);

//        barChart.setTouchEnabled(false);
        barChart.getLegend().setEnabled(false);
//        barChart.getAxisRight().setDrawGridLines(false);
//        barChart.getAxisRight().setDrawAxisLine(false);
//        barChart.getAxisRight().setDrawLabels(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getAxisLeft().setDrawLabels(false);
//        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setDrawAxisLine(false);
//        barChart.getXAxis().setDrawLabels(false);
//        barChart.animate();


//        Legend l = barChart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(true);
//        l.setYOffset(20f);
//        l.setXOffset(0f);
//        l.setYEntrySpace(0f);
//        l.setTextSize(8f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(false);
        xAxis.setLabelCount(CompanyNamesList.size());
        xAxis.setAxisMaximum(CompanyNamesList.size());
        xAxis.setAxisMinimum(0);
        xAxis.setTextSize(10f);
        barChart.setFitBars(true);


        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(CompanyNamesList.size());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(CompanyNamesList));

        barChart.getAxisRight().setEnabled(false);
        YAxis leftAxis = barChart.getAxisLeft();
//        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
//        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);



        PieChart pieChart = getView().findViewById(R.id.pieChart);
        ArrayList <PieEntry> BranchWiseCountList = new ArrayList<>();
//        BranchWiseCount.forEach((k,v) ->
//                BranchWiseCountList.add(new PieEntry(v, k))
//                        );
        System.out.println("\nBranch wise no of placed students");
        for(Map.Entry<String, Integer> ele: BranchWiseCount.entrySet()){
            System.out.println(ele.getKey() + " : " + ele.getValue());
            BranchWiseCountList.add(new PieEntry(ele.getValue(), ele.getKey()));
            noOfStudentPlaced += ele.getValue();
        }

        System.out.println("\nBranch wise highest package (CTC in lpa)");
        for(Map.Entry<String, Float> ele: BranchWiseHighest.entrySet()){
            System.out.println(ele.getKey() + " : " + ele.getValue());
        }

        System.out.println("\nBranch wise average package (CTC in lpa)");
        for(Map.Entry<String, Float> ele: BranchWiseSum.entrySet()){
            System.out.println(ele.getKey() + " : " + ele.getValue()/BranchWisePackage.get(ele.getKey()));
        }

        System.out.println("\nBranch wise no of packages offered");
        for(Map.Entry<String, Integer> ele: BranchWisePackage.entrySet()){
            System.out.println(ele.getKey() + " : " + ele.getValue());
        }

        System.out.println("\nTotal students placed : " + noOfStudentPlaced);
        System.out.println("Total packages offered : " +  noOfPackagesOffered);


        PieDataSet pieDataSet = new PieDataSet(BranchWiseCountList, "Branch Wise");
        pieDataSet.setValueFormatter(new IntegerFormatter());
        pieDataSet.setDrawValues(true);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(12f);
        pieChart.setEntryLabelTextSize(12f);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setCenterText("Branch Wise");
        pieChart.setCenterTextSize(12f);
        pieChart.setCenterTextColor(Color.GRAY);
        pieChart.animate();

        totalStudentPlaced.setText("Total Students Placed : " + noOfStudentPlaced);
        totalPackagesOffered.setText("Total Packages Offered : " + noOfPackagesOffered);
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

}

