package com.crazydev.graphbuilder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.crazydev.graphbuilder.appspecific.Graph;

import java.util.ArrayList;
import java.util.Locale;

import bobenus.math.Asymptote;
import bobenus.math.Segment;

public class GraphDataOutputActivity extends AppCompatActivity {

    private Graph graph;
    private LayoutInflater inflater;

    public static String DOWN_RIGHT_ARROW = String.valueOf('\u2798');
    public static String UP_RIGHT_ARROW   = String.valueOf('\u279a');
    public static String HALFSPHERE_UP    = String.valueOf('\u25E1');
    public static String HALFSPHERE_DOWN  = String.valueOf('\u25E0');
    public static String SHTRIH_1         = String.valueOf('\u2032');
    public static String SHTRIH_2         = String.valueOf('\u2033');

    private String asymptote;
    private String zeros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_output_activity);

        SharedPreferences sp = getSharedPreferences(SettingsActivity.LOCALE_PREF_KEY, MODE_PRIVATE);
        String currentLanguage = sp.getString(SettingsActivity.LANGUAGE_KEY, SettingsActivity.ENGLISH_LOCALE);

        Locale myLocale = new Locale(currentLanguage);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        this.asymptote = getResources().getString(R.string.asymptote);
        this.zeros     = getResources().getString(R.string.zeros2);

        this.inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Intent intent = this.getIntent();
        this.graph = (Graph) intent.getSerializableExtra("graph");

        final HorizontalScrollView scroll = (HorizontalScrollView) findViewById(R.id.horizontal_sxroll_view);
        final LinearLayout container1 = (LinearLayout) findViewById(R.id.data_output_activity1);
        final LinearLayout container2 = (LinearLayout) findViewById(R.id.data_output_activity2);
        final LinearLayout container3 = (LinearLayout) findViewById(R.id.data_output_activity3);

   //     final String asymptoteLabel = "Asymptotes";
   //     final String zeroLabel      = "Zeros";

        final int dataColor = getResources().getColor(R.color.background_color_data);

        final ArrayList<Segment> allowableRange = this.graph.allowableRange;

        final int inPixels       = (int) getResources().getDimension(R.dimen.cell_width);
        final int subWidth       = (int) getResources().getDimension(R.dimen.sub_cell_width);
        final int asymptoteWidth = (int) getResources().getDimension(R.dimen.asymptote_cell_width);
        final int zerosWidth     = (int) getResources().getDimension(R.dimen.zeros_cell_width);

        scroll.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    scroll.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

            //    Log.d("tagggg", "height = " + scroll.getHeight());

                LinearLayout.LayoutParams childParams;
                LinearLayout verticalContainer = (LinearLayout) inflater.inflate(R.layout.vertical_data_container, null);
                childParams = new LinearLayout.LayoutParams(asymptoteWidth / 2, scroll.getHeight() / 2);
                verticalContainer.setBackgroundColor(getResources().getColor(R.color.background_color));
                verticalContainer.setLayoutParams(childParams);

                TextView odz = (TextView) inflater.inflate(R.layout.text_view_one_dim_item, null);

                childParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                childParams.weight = 1;

                odz.setLayoutParams(childParams);
                odz.setTextSize(12);
                odz.setText("X");
                verticalContainer.addView(odz);


                odz = (TextView) inflater.inflate(R.layout.text_view_one_dim_item, null);

                childParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                childParams.weight = 1;

                odz.setLayoutParams(childParams);
                odz.setTextSize(12);
                odz.setText("F" + SHTRIH_1 +"(x)");
                verticalContainer.addView(odz);

                odz = (TextView) inflater.inflate(R.layout.text_view_one_dim_item, null);

                childParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                childParams.weight = 1;

                odz.setLayoutParams(childParams);
                odz.setTextSize(12);
                odz.setText("F"+ SHTRIH_2 +"(x)");
                verticalContainer.addView(odz);


                container1.addView(verticalContainer);

                verticalContainer = (LinearLayout) inflater.inflate(R.layout.vertical_data_container, null);
                childParams = new LinearLayout.LayoutParams(asymptoteWidth / 2, scroll.getHeight() / 4);
                verticalContainer.setBackgroundColor(getResources().getColor(R.color.background_color));
                verticalContainer.setLayoutParams(childParams);

                childParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                childParams.weight = 1;

                odz = (TextView) inflater.inflate(R.layout.text_view_one_dim_item, null);
                odz.setLayoutParams(childParams);
                odz.setText(GraphDataOutputActivity.this.asymptote);

                verticalContainer.addView(odz);
                container2.addView(verticalContainer);


                verticalContainer = (LinearLayout) inflater.inflate(R.layout.vertical_data_container, null);
                childParams = new LinearLayout.LayoutParams(asymptoteWidth / 2, scroll.getHeight() / 4);
                verticalContainer.setBackgroundColor(getResources().getColor(R.color.background_color));
                verticalContainer.setLayoutParams(childParams);

                childParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                childParams.weight = 1;

                odz = (TextView) inflater.inflate(R.layout.text_view_one_dim_item, null);
                odz.setLayoutParams(childParams);
                odz.setText(GraphDataOutputActivity.this.zeros);

                verticalContainer.addView(odz);
                container3.addView(verticalContainer);

                for (int i = 0; i < allowableRange.size(); i ++) {
                    Segment segment = allowableRange.get(i);

                    verticalContainer = (LinearLayout) inflater.inflate(R.layout.vertical_data_container, null);
                    verticalContainer.setBackgroundColor(dataColor);

                    // ODZ
                    odz = (TextView) inflater.inflate(R.layout.text_view_one_dim_item, null);

                    childParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    childParams.weight = 1;

                    odz.setLayoutParams(childParams);
                    odz.setText(segment.toString());
                    verticalContainer.addView(odz);

                    int width = inPixels;

                    // First Derivative

                    LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.text_view_many_dim_item, null);
                    childParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    childParams.weight = 1;
                    layout.setLayoutParams(childParams);

                    ArrayList<String> firstDerivative = segment.getFirstDerivativeData();

                    int c = 0;
                    for (int j = 0; j < firstDerivative.size(); j ++) {

                        childParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        childParams.weight = 1;

                        odz = (TextView) inflater.inflate(R.layout.text_view_one_dim_item, null);
                        odz.setLayoutParams(childParams);
                        String s = firstDerivative.get(j);

                        switch(s) {
                            case "+":
                                odz.setText(UP_RIGHT_ARROW);
                                break;

                            case "-":
                                odz.setText(DOWN_RIGHT_ARROW);
                                break;
                            default:
                                odz.setText(s);
                        }


                        layout.addView(odz);

                        c += subWidth;
                    }

                    if (c > width) {
                        width = c;
                    }

                    verticalContainer.addView(layout);

                    layout = (LinearLayout) inflater.inflate(R.layout.text_view_many_dim_item, null);
                    childParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    childParams.weight = 1;
                    layout.setLayoutParams(childParams);

                    ArrayList<String> secondDerivative = segment.getSecondDerivativeData();
                    c = 0;
                    for (int j = 0; j < secondDerivative.size(); j ++) {

                        childParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        childParams.weight = 1;

                        odz = (TextView) inflater.inflate(R.layout.text_view_one_dim_item, null);
                        odz.setLayoutParams(childParams);
                        String s = secondDerivative.get(j);

                        switch(s) {
                            case "+":
                                odz.setText(HALFSPHERE_UP);
                                break;

                            case "-":
                                odz.setText(HALFSPHERE_DOWN);
                                break;
                            default:
                                odz.setText(s);
                        }


                        layout.addView(odz);

                        c += subWidth;
                    }

                    if (c > width) {
                        width = c;
                    }

                    verticalContainer.addView(layout);

                    childParams = new LinearLayout.LayoutParams(width, scroll.getHeight() / 2);
                    verticalContainer.setLayoutParams(childParams);

                    container1.addView(verticalContainer);
                }

                ArrayList<Asymptote> asymptotes = graph.asymptotes;

                for (int i = 0; i < asymptotes.size(); i ++) {
                    verticalContainer = (LinearLayout) inflater.inflate(R.layout.vertical_data_container, null);
                    childParams = new LinearLayout.LayoutParams(asymptoteWidth, scroll.getHeight() / 4);
                    verticalContainer.setLayoutParams(childParams);

                    childParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    childParams.weight = 1;

                    odz = (TextView) inflater.inflate(R.layout.text_view_one_dim_item, null);
                    odz.setLayoutParams(childParams);

                    odz.setText(asymptotes.get(i).toString());

                    verticalContainer.addView(odz);

                    container2.addView(verticalContainer);
                }

                ArrayList<Double> zeros = graph.zeros;

                for (int i = 0; i < zeros.size(); i ++) {
                    verticalContainer = (LinearLayout) inflater.inflate(R.layout.vertical_data_container, null);
                    childParams = new LinearLayout.LayoutParams(zerosWidth, scroll.getHeight() / 4);
                    verticalContainer.setLayoutParams(childParams);

                    childParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    childParams.weight = 1;

                    odz = (TextView) inflater.inflate(R.layout.text_view_one_dim_item, null);
                    odz.setLayoutParams(childParams);

                    odz.setText(zeros.get(i).toString());

                    verticalContainer.addView(odz);

                    container3.addView(verticalContainer);
                }

            }
        });

    }

}
