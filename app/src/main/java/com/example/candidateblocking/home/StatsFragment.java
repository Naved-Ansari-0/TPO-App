package com.example.candidateblocking.home;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.candidateblocking.R;
import com.example.candidateblocking.utils.ParseJsonString;
import com.example.candidateblocking.models.PlacedStudentModel;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class StatsFragment extends Fragment {
    ArrayList <PlacedStudentModel> placedStudentList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ParseJsonString parseJsonString = new ParseJsonString(requireContext());
        placedStudentList = parseJsonString.getPlacedStudentList();

        Map<String, Integer> companyWisePlacedStudents = new HashMap<>();
        Map<String, Integer> branchWisePlacedStudents = new HashMap<>();
        Map<String, Integer> branchWisePackagesOffered = new HashMap<>();

        Map<String, Float> branchWiseHighestPackage = new HashMap<>();
        Map<String, Float> branchWisePackagesSum = new HashMap<>();
        Map<String, Float> branchWiseAveragePackage = new HashMap<>();

//------Initializing above maps---------------------------------

        for(int i = 0; i< placedStudentList.size(); i++){
            String branch = placedStudentList.get(i).getBranch();
            ArrayList<Float> packages = placedStudentList.get(i).getPackages();
            ArrayList<String> companies = placedStudentList.get(i).getCompanies();

            if(branchWisePlacedStudents.containsKey(branch))
                branchWisePlacedStudents.put(branch, branchWisePlacedStudents.get(branch)+1);
            else
                branchWisePlacedStudents.put(branch, 1);

            if(branchWisePackagesOffered.containsKey(branch))
                branchWisePackagesOffered.put(branch, branchWisePackagesOffered.get(branch)+packages.size());
            else
                branchWisePackagesOffered.put(branch, packages.size());

            for(String company: companies){
                if(companyWisePlacedStudents.containsKey(company))
                    companyWisePlacedStudents.put(company, companyWisePlacedStudents.get(company)+1);
                else
                    companyWisePlacedStudents.put(company, 1);
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
            }else
                branchWiseHighestPackage.put(branch, highestPackage);
            if(branchWisePackagesSum.containsKey(branch))
                branchWisePackagesSum.put(branch, branchWisePackagesSum.get(branch)+packagesSum);
            else
                branchWisePackagesSum.put(branch, packagesSum);

        }

        for(Map.Entry<String, Float> ele: branchWisePackagesSum.entrySet()){
            branchWiseAveragePackage.put(ele.getKey(), ele.getValue()/branchWisePackagesOffered.get(ele.getKey()));
        }

//------Creating array data for all charts----------------------------------------------

        // Bar Chart arrays
        ArrayList companiesList = new ArrayList<>();
        ArrayList companyWisePlacedStudentsList = new ArrayList<>();
        int position = 0;
        for(Map.Entry<String,Integer> ele : companyWisePlacedStudents.entrySet()){
            companyWisePlacedStudentsList.add(new BarEntry(position, ele.getValue()));
            companiesList.add(ele.getKey());
            position++;
        }

        // Pie Chart arrays
        ArrayList <PieEntry> branchWisePlacedStudentsList = new ArrayList<>();
        ArrayList <PieEntry> branchWisePackagesOfferedList = new ArrayList<>();
        for(Map.Entry<String, Integer> ele: branchWisePlacedStudents.entrySet()){
            branchWisePlacedStudentsList.add(new PieEntry(ele.getValue(), ele.getKey()));
        }
        for(Map.Entry<String, Integer> ele: branchWisePackagesOffered.entrySet()){
            branchWisePackagesOfferedList.add(new PieEntry(ele.getValue(), ele.getKey()));
        }

        // Bar Chart arrays
        ArrayList branches = new ArrayList();
        ArrayList branchWiseHighestPackageList = new ArrayList<>();
        ArrayList branchWiseAveragePackageList = new ArrayList<>();
        position = 0;
        for(Map.Entry<String, Float> ele: branchWiseHighestPackage.entrySet()){
            branches.add(ele.getKey());
            branchWiseHighestPackageList.add(new BarEntry(position, ele.getValue()));
            position++;
        }
        position = 0;
        for(Map.Entry<String, Float> ele: branchWiseAveragePackage.entrySet()){
            branchWiseAveragePackageList.add(new BarEntry(position, ele.getValue()));
            position++;
        }

//------Initializing Bar Chart 1-------------------------------------------------------

        BarDataSet barDataSet = new BarDataSet(companyWisePlacedStudentsList, "Company Wise Placed Students");
        barDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        barDataSet.setValueTextSize(10f);
        barDataSet.setValueTextColor(Color.BLACK);
        BarData barData = new BarData(barDataSet);
        barData.setValueFormatter(new IntegerFormatter());
//        barData.setBarWidth(1f);

        BarChart barChartCompanies = view.findViewById(R.id.barChartCompanies);
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


//------Initializing Pie Chart 1-------------------------------

        PieDataSet pieDataSetStudents = new PieDataSet(branchWisePlacedStudentsList, "Branch Wise Students Placed");
        PieData pieDataStudents = new PieData(pieDataSetStudents);

        PieChart pieChartStudents = view.findViewById(R.id.pieChartStudents);
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

//------Initializing Pie Chart 2--------------------------------

        PieDataSet pieDataSetOffers = new PieDataSet(branchWisePackagesOfferedList, "Branch Wise Packages Offered");
        PieData pieDataOffers = new PieData(pieDataSetOffers);

        PieChart pieChartOffers = view.findViewById(R.id.pieChartOffers);
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


//------Initializing Bar Chart 2-----------------------------------

        BarDataSet highestPackageDataSet = new BarDataSet(branchWiseHighestPackageList, "Highest Package");
        highestPackageDataSet.setColor(Color.RED);
        highestPackageDataSet.setValueTextSize(10f);
        highestPackageDataSet.setValueTextColor(Color.BLACK);

        BarDataSet averagePackageDataSet = new BarDataSet(branchWiseAveragePackageList, "Average Package");
        averagePackageDataSet.setColor(Color.BLUE);
        averagePackageDataSet.setValueTextSize(10f);
        averagePackageDataSet.setValueTextColor(Color.BLACK);

        barData = new BarData(highestPackageDataSet, averagePackageDataSet);
        barData.setValueFormatter(new FloatFormatter());
        barData.setBarWidth(1f);

        BarChart  barChartPackages = view.findViewById(R.id.barChartPackages);
        int barChartWidth = branches.size();
        barChartPackages.setMinimumWidth(barChartWidth);
        barChartPackages.setData(barData);
        barChartPackages.setDescription(null);
        barChartPackages.setPinchZoom(false);
        barChartPackages.setScaleEnabled(false);
        barChartPackages.setDrawBarShadow(false);
        barChartPackages.setDrawGridBackground(false);

//        barChartPackages.setTouchEnabled(false);
//        barChartPackages.getLegend().setEnabled(false);
//        barChartPackages.getAxisRight().setDrawGridLines(false);
//        barChartPackages.getAxisRight().setDrawAxisLine(false);
//        barChartPackages.getAxisRight().setDrawLabels(false);
        barChartPackages.getAxisLeft().setDrawGridLines(false);
        barChartPackages.getAxisLeft().setDrawAxisLine(false);
        barChartPackages.getAxisLeft().setDrawLabels(false);
//        barChartPackages.getXAxis().setDrawGridLines(false);
        barChartPackages.getXAxis().setDrawAxisLine(false);
//        barChartPackages.getXAxis().setDrawLabels(false);
//        barChartPackages.animate();

//        Legend l = barChartPackages.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(true);
//        l.setYOffset(20f);
//        l.setXOffset(0f);
//        l.setYEntrySpace(0f);
//        l.setTextSize(8f);

        xAxis = barChartPackages.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(false);
        xAxis.setLabelCount(companiesList.size());
        xAxis.setAxisMaximum(companiesList.size());
        xAxis.setAxisMinimum(0);
        xAxis.setTextSize(10f);
        barChartPackages.setFitBars(true);
        xAxis.setAxisMinimum(-0.5f);
        barChartPackages.groupBars(0, 1f, 0f);

        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(companiesList.size());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index % 3 == 1 && index < branches.size()*3) {
                    return branches.get(index/3).toString();
                } else {
                    return "";
                }
            }
        });

        barChartPackages.getAxisRight().setEnabled(false);
        leftAxis = barChartCompanies.getAxisLeft();
//        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
//        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);

        barChartPackages.setVisibility(View.GONE);

//------Showing stats in TEXT----------------------------------------
        showDetailedStats(branchWiseHighestPackage,branchWiseAveragePackage,
                branchWisePackagesSum, branchWisePackagesOffered, branchWisePlacedStudents);

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

    private void showDetailedStats(Map<String, Float> branchWiseHighestPackage,
                                   Map<String, Float> branchWiseAveragePackage,
                                   Map<String, Float> branchWisePackagesSum,
                                   Map<String, Integer> branchWisePackagesOffered,
                                   Map<String, Integer> branchWisePlacedStudents){
        String str = "";

        for(Map.Entry<String, Float> ele: branchWiseHighestPackage.entrySet()){
            str += "Branch : " + ele.getKey() + "\n" +
                    "Highest Package : " + ele.getValue() + " lpa\n" +
                    "Average Package : " +  String.format("%.2f",branchWiseAveragePackage.get(ele.getKey())) + " lpa\n\n";
        }

        int totalPlaced = 0, totalPackages = 0, packagesSum = 0;
        float highestPackage = 0;
        for(Map.Entry<String,Integer> ele: branchWisePlacedStudents.entrySet()){
            totalPlaced += ele.getValue();
            totalPackages += branchWisePackagesOffered.get(ele.getKey());
            packagesSum += branchWisePackagesSum.get(ele.getKey());
            highestPackage = Math.max(highestPackage, branchWiseHighestPackage.get(ele.getKey()));
        }
        
        //to display average package upto two decimal places
        float averagePackage = (float) ((packagesSum*1.0)/(totalPackages*1.0));
    
        str += "\nTotal students placed : " + totalPlaced + "\n" +
                "Total packages offered : " + totalPackages + "\n" +
                "Highest package : " + highestPackage + "\n" +
                "Average package : " +String.format("%.2f",averagePackage);

        TextView textView = getView().findViewById(R.id.highestAveragePackageTextView);
        textView.setText(str);

    }

}

