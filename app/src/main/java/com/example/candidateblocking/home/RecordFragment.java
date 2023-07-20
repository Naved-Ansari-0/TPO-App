package com.example.candidateblocking.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.candidateblocking.R;
import com.example.candidateblocking.home.record.FilterFragment;
import com.example.candidateblocking.home.record.SearchFragment;
import com.example.candidateblocking.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


public class RecordFragment extends Fragment {

    private TabLayout recordTabLayout;
    private ViewPager recordViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recordTabLayout = view.findViewById(R.id.recordTabLayout);
        recordViewPager = view.findViewById(R.id.recordViewPager);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new FilterFragment());
        fragments.add(new SearchFragment());

        ArrayList<String> fragmentsTitles = new ArrayList<>();
        fragmentsTitles.add("Filter");
        fragmentsTitles.add("Search");

        recordViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), fragments, fragmentsTitles));
        recordTabLayout.setupWithViewPager(recordViewPager);

    }
}