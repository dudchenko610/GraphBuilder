package com.crazydev.graphbuilder.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.crazydev.graphbuilder.GraphDataInputActivity;
import com.crazydev.graphbuilder.R;


public class AdditionalFunctionsFragment extends Fragment {

    private GraphDataInputActivity appCompatActivity;

    public AdditionalFunctionsFragment(GraphDataInputActivity graphDataInputActivity) {
        this.appCompatActivity = graphDataInputActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_additional_functions, vg, false);
    }

    @Override
    public void onResume() {

     /* appCompatActivity.findViewById(R.id.btn_abs).setOnClickListener(appCompatActivity);
        appCompatActivity.findViewById(R.id.btn_ceil).setOnClickListener(appCompatActivity);
        appCompatActivity.findViewById(R.id.btn_floor).setOnClickListener(appCompatActivity);*/
    //    appCompatActivity.findViewById(R.id.btn_exp).setOnClickListener(appCompatActivity);
        appCompatActivity.findViewById(R.id.btn_cot).setOnClickListener(appCompatActivity);
        appCompatActivity.findViewById(R.id.btn_acot).setOnClickListener(appCompatActivity);
        appCompatActivity.findViewById(R.id.btn_sec).setOnClickListener(appCompatActivity);
        appCompatActivity.findViewById(R.id.btn_cosec).setOnClickListener(appCompatActivity);
        appCompatActivity.findViewById(R.id.btn_sqrt).setOnClickListener (appCompatActivity);
        appCompatActivity.findViewById(R.id.btn_sh).setOnClickListener   (appCompatActivity);
        appCompatActivity.findViewById(R.id.btn_ch).setOnClickListener   (appCompatActivity);


        super.onResume();
    }

}
