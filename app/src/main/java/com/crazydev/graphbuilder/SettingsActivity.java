package com.crazydev.graphbuilder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    public static final String LOCALE_PREF_KEY  = "localePref";
    public static final String LANGUAGE_KEY     = "lang_key";

    public static final String UKRAINIAN_LOCALE = "uk";
    public static final String RUSSIAN_LOCALE   = "ru";
    public static final String ENGLISH_LOCALE   = "en_US";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        SharedPreferences sp = getSharedPreferences(LOCALE_PREF_KEY, MODE_PRIVATE);
        String currentLanguage = sp.getString(LANGUAGE_KEY, ENGLISH_LOCALE);

        this.setLocale(currentLanguage);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        List<String> langs = new ArrayList<String>();
        langs.add(getResources().getString(R.string.change_language));
        langs.add(getResources().getString(R.string.en));
        langs.add(getResources().getString(R.string.ru));
        langs.add(getResources().getString(R.string.uk));

        Spinner spinner = (Spinner) findViewById(R.id.spinner_languages);

        ArrayAdapter<String> languageAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, langs) {

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };


        spinner.setAdapter(languageAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //    String selectedItemText = (String) parent.getItemAtPosition(position);

                Intent refresh = null;

                switch(position) {
                    case 0:

                        break;
                    case 1:
                        SettingsActivity.this.setLocale(ENGLISH_LOCALE);
                        refresh = new Intent(SettingsActivity.this, SettingsActivity.class);
                        refresh.putExtra(LANGUAGE_KEY, ENGLISH_LOCALE);

                        startActivity(refresh);
                        finish();
                        break;
                    case 2:
                        SettingsActivity.this.setLocale(RUSSIAN_LOCALE);
                        refresh = new Intent(SettingsActivity.this, SettingsActivity.class);
                        refresh.putExtra(LANGUAGE_KEY, RUSSIAN_LOCALE);

                        startActivity(refresh);
                        finish();
                        break;
                    case 3:
                        SettingsActivity.this.setLocale(UKRAINIAN_LOCALE);
                        refresh = new Intent(SettingsActivity.this, SettingsActivity.class);
                        refresh.putExtra(LANGUAGE_KEY, UKRAINIAN_LOCALE);

                        startActivity(refresh);
                        finish();
                        break;
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });



    }


    public void setLocale(String localeName) {

        Locale myLocale = new Locale(localeName);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        SharedPreferences.Editor editor = getSharedPreferences(LOCALE_PREF_KEY, MODE_PRIVATE).edit();

        editor.putString(LANGUAGE_KEY, localeName);
        editor.commit();

    }

 /*   public void onBackPressed() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }*/


}
