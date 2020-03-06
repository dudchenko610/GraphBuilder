package com.crazydev.graphbuilder.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.crazydev.graphbuilder.GraphDataInputActivity;
import com.crazydev.graphbuilder.R;


public class MainFunctionsFragment extends Fragment {

    private GraphDataInputActivity appCompatActivity;

    public MainFunctionsFragment(GraphDataInputActivity graphDataInputActivity) {
        this.appCompatActivity = graphDataInputActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_functions, vg, false);
    }

    @Override
    public void onResume() {

        appCompatActivity.findViewById(R.id.btn_sin).setOnClickListener  (appCompatActivity);
        appCompatActivity.findViewById(R.id.btn_cos).setOnClickListener  (appCompatActivity);
        appCompatActivity.findViewById(R.id.btn_tan).setOnClickListener  (appCompatActivity);
        appCompatActivity.findViewById(R.id.btn_asin).setOnClickListener (appCompatActivity);
        appCompatActivity.findViewById(R.id.btn_acos).setOnClickListener (appCompatActivity);
        appCompatActivity.findViewById(R.id.btn_atan).setOnClickListener (appCompatActivity);
        appCompatActivity.findViewById(R.id.btn_log).setOnClickListener  (appCompatActivity);
        appCompatActivity.findViewById(R.id.btn_log2).setOnClickListener (appCompatActivity);
        appCompatActivity.findViewById(R.id.btn_log10).setOnClickListener(appCompatActivity);


        super.onResume();
    }
}
