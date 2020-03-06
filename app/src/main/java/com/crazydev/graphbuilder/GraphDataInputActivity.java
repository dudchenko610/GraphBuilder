package com.crazydev.graphbuilder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.crazydev.graphbuilder.views.AdditionalFunctionsFragment;
import com.crazydev.graphbuilder.views.ColorPickerView;
import com.crazydev.graphbuilder.views.ConstantsFragment;
import com.crazydev.graphbuilder.views.MainFunctionsFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GraphDataInputActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private EditText editText;
    private EditText editTextFrom;
    private EditText editTextTo;

    private EditText toType;

    private ColorPickerView colorPickerView;

    private TabAdapter adapter;
    private TabAdapter emptyAdapter;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Button[] elements;

    private int DIALOG_WARNING = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_input_activity);

        SharedPreferences sp = getSharedPreferences(SettingsActivity.LOCALE_PREF_KEY, MODE_PRIVATE);
        String currentLanguage = sp.getString(SettingsActivity.LANGUAGE_KEY, SettingsActivity.ENGLISH_LOCALE);

        Locale myLocale = new Locale(currentLanguage);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_graph);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        //    getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        colorPickerView = (ColorPickerView) findViewById(R.id.colorPickerView);

        adapter = new TabAdapter(getSupportFragmentManager());
        emptyAdapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainFunctionsFragment(this), getResources().getString(R.string.main_functions));
        adapter.addFragment(new AdditionalFunctionsFragment(this),  getResources().getString(R.string.additional_functions));
        adapter.addFragment(new ConstantsFragment(this),  getResources().getString(R.string.constants));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        editText = (EditText) findViewById(R.id.edit_t);
        editText.requestFocus();
        toType = editText;

        editTextFrom = (EditText) findViewById(R.id.edit_from);
        editTextTo = (EditText) findViewById(R.id.edit_to);
        editTextFrom.setSelection(3);
        editTextTo.setSelection(2);

        editText.setOnTouchListener(this);
        editTextFrom.setOnTouchListener(this);
        editTextTo.setOnTouchListener(this);

        elements = new Button[8];
        setupButtons();

    }

    private void setupButtons() {

        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.btn_0).setOnClickListener(this);
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
        findViewById(R.id.btn_5).setOnClickListener(this);
        findViewById(R.id.btn_6).setOnClickListener(this);
        findViewById(R.id.btn_7).setOnClickListener(this);
        findViewById(R.id.btn_8).setOnClickListener(this);
        findViewById(R.id.btn_9).setOnClickListener(this);
        findViewById(R.id.btn_dot).setOnClickListener(this);
        findViewById(R.id.btn_minus).setOnClickListener(this);

        elements[0] = (Button) findViewById(R.id.btn_grad_deg);
        elements[1] = (Button) findViewById(R.id.btn_par_open);
        elements[2] = (Button) findViewById(R.id.btn_par_close);
        elements[3] = (Button) findViewById(R.id.btn_degree);
        elements[4] = (Button) findViewById(R.id.btn_multiply);
        elements[5] = (Button) findViewById(R.id.btn_divide);
        elements[6] = (Button) findViewById(R.id.btn_plus);
        elements[7] = (Button) findViewById(R.id.btn_X);

        for (Button button : elements) {
            button.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            case R.id.btn_ok:

                double left  = 0;
                double right = 0;

                try {
                    left  = Double.parseDouble(editTextFrom.getText().toString());
                    right = Double.parseDouble(editTextTo.getText().toString());

                } catch(Exception e) {

                    editTextFrom.setText("-10");
                    editTextTo.setText("10");

                    showDialog(DIALOG_WARNING);
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra("left", left);
                intent.putExtra("right", right);
                intent.putExtra("expression", editText.getText().toString());
                intent.putExtra("color", colorPickerView.getColor());

                setResult(RESULT_OK, intent);
                finish();

                break;

            case R.id.btn_delete:

                int end = toType.getSelectionEnd();

                if (end == 0) {
                    break;
                }

                String result = "";

                String text = toType.getText().toString();

                result += text.substring(0, end - 1);
                result += text.substring(end, text.length());

                toType.setText(result);
                toType.setSelection(end - 1);

                break;

            case R.id.btn_grad_deg:
                break;

            default:

                int end2      = toType.getSelectionEnd();
                String text2  = toType.getText().toString();
                String before = text2.substring(0, end2);

                String after = text2.substring(end2, text2.length());

                String label = ((Button) view).getText().toString();

                String s = "";

                switch (view.getId()) {

                    case R.id.btn_PI:
                        s = "PI";
                        break;
                    case R.id.btn_E:
                        s = "E";
                        break;
                    case R.id.btn_F:
                        s = "F";
                        break;

                    default:
                        s = label;
                }

                toType.setText(before + s + after);
                toType.setSelection(end2 + s.length());

                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.onTouchEvent(event);
        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        switch (v.getId()) {
            case R.id.edit_t:
                for (Button button : elements) {
                    button.setEnabled(true);
                }

                viewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(viewPager);

                toType = editText;

                break;
            case R.id.edit_from:
                setupEditText(editTextFrom);
                break;
            case R.id.edit_to:
                setupEditText(editTextTo);
                break;
        }

        return true;
    }

    private void setupEditText(EditText e) {
        for (Button button : elements) {
            button.setEnabled(false);
        }

        viewPager.setAdapter(emptyAdapter);
        tabLayout.setupWithViewPager(viewPager);

        toType = e;
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_WARNING) {
            AlertDialog.Builder adb = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyTheme));
            // заголовок
            adb.setTitle(R.string.dialog_title);
            // сообщение
            adb.setMessage(getResources().getString(R.string.error_type_1));
            // иконка
            adb.setIcon(android.R.drawable.ic_dialog_info);
            // кнопка положительного ответа
            adb.setPositiveButton(R.string.ok, null);
            // создаем диалог
            return adb.create();
        }
        return super.onCreateDialog(id);
    }


    private class TabAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        TabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            //     Log.d("mylog", "position - " + position);

            return mFragmentList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }


}
