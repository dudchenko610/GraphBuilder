package com.crazydev.graphbuilder.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.crazydev.graphbuilder.GraphDataInputActivity;
import com.crazydev.graphbuilder.R;


public class ConstantsFragment extends Fragment {

    private GraphDataInputActivity appCompatActivity;

    public ConstantsFragment(GraphDataInputActivity graphDataInputActivity) {
        this.appCompatActivity = graphDataInputActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_constants, vg, false);
    }

    @Override
    public void onResume() {
        appCompatActivity.findViewById(R.id.btn_PI).setOnClickListener(appCompatActivity);
        appCompatActivity.findViewById(R.id.btn_E).setOnClickListener(appCompatActivity);
        appCompatActivity.findViewById(R.id.btn_F).setOnClickListener(appCompatActivity);

        super.onResume();
    }
}
