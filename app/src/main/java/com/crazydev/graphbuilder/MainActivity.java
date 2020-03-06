package com.crazydev.graphbuilder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.crazydev.graphbuilder.appspecific.Graph;
import com.crazydev.graphbuilder.appspecific.OnGraphStatus;
import com.crazydev.graphbuilder.db.MemoryManager;
import com.crazydev.graphbuilder.math.Vector3D;
import com.crazydev.graphbuilder.rendering.OpenGLRenderer;
import com.crazydev.graphbuilder.views.GraphItemDrawerInfo;
import com.crazydev.graphbuilder.views.OnGraphItemEvent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import bobenus.exceptions.ComputationException;
import bobenus.expressionparser.Builder;
import bobenus.expressionparser.Node;

public class MainActivity extends AppCompatActivity implements OnGraphStatus, OnGraphItemEvent {

    private static final int REQUEST_CODE_INPUT_DATA = 0;
    private static final int REQUEST_CODE_SETTINGS   = 1;

    private static final String GRAPHS_PARCELABLE_KEY = "graphs";
    private static final int DIALOG = 1;

    private ArrayList<Graph> graphs;
    private HashMap<Graph, GraphItemDrawerInfo> items = new HashMap<Graph, GraphItemDrawerInfo>();

    public Drawer.Result drawerResult;
    public OpenGLRenderer openGLView;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private boolean pGrid = false;
    public MemoryManager memoryManager;

    private ProgressBar progressBar;
    private AdView adView;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //    MobileAds.initialize(this, getString(R.string.app_id));

        this.adView = (AdView) findViewById(R.id.banner_ad);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("8FD1C12AA8E56B729CD40C57B12A6F63")
                .build();

        adView.loadAd(adRequest);

        SharedPreferences sp = getSharedPreferences(SettingsActivity.LOCALE_PREF_KEY, MODE_PRIVATE);
        String currentLanguage = sp.getString(SettingsActivity.LANGUAGE_KEY, SettingsActivity.ENGLISH_LOCALE);

        this.setLocale(currentLanguage);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.memoryManager = new MemoryManager(this);
        Graph.ID_HASH = this.memoryManager.getLastAutoincrementedIdValue();

        (findViewById(R.id.make_viewshot)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                MainActivity.this.verifyStoragePermissions(MainActivity.this);

                MainActivity.this.openGLView.makeScreenShot();
            }
        });

        this.drawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header).withOnDrawerListener(new Drawer.OnDrawerListener() {

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        //    MainActivity.this.hideKeyboard();

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        //              MainActivity.this.memoryManager.lookIntoDB();

                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        //     MainActivity.this.hideKeyboard();
                    }


                })
                .addDrawerItems(

                ).build();


        this.openGLView = (OpenGLRenderer) findViewById(R.id.open_gl_renderer);


        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.add_floating_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, GraphDataInputActivity.class);


                startActivityForResult(intent, REQUEST_CODE_INPUT_DATA);

            }
        });


        CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox);

        String done = getResources().getString(R.string.done);

        final SharedPreferences sharedPreferences = this.getSharedPreferences("graph_builder", MODE_PRIVATE);
        this.pGrid = sharedPreferences.getBoolean("P_GRID", false);

        MainActivity.this.openGLView.setPGrid(pGrid);
        checkBox.setChecked(pGrid);

        checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                pGrid = !pGrid;
                MainActivity.this.openGLView.setPGrid(pGrid);

                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putBoolean("P_GRID", MainActivity.this.pGrid);
                editor.commit();
            }
        });



        try {
            ArrayList<Graph> graphs = this.memoryManager.getAllGraphs(this, this.openGLView);
            this.graphs = graphs;

            for (int i = 0; i < graphs.size(); i ++) {

                Graph graph = graphs.get(i);

                Vector3D col = graph.getColor();

                int R = (int) (col.x * 255);
                int G = (int) (col.y * 255);
                int B = (int) (col.z * 255);

                int color = (255 << 24) | (R << 16) | (G << 8) | B;

                GraphItemDrawerInfo item = new GraphItemDrawerInfo(this.drawerResult, this, graph.function)
                        .withColor(color);

                this.items.put(graph, item);
                this.items.get(graph).setStatus(Graph.STATUS_DONE);
                this.items.get(graph).setLabels(this.getApplicationContext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setTranslatedTexts();

        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onBackPressed(){
        if (drawerResult.isDrawerOpen()){
            drawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //    outState.putParcelableArrayList(GRAPHS_PARCELABLE_KEY, graphs);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        this.adView.pause();
      //  this.openGLView.onPause(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        this.adView.resume();
     //   this.openGLView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        this.adView.destroy();

        Set<Graph> grs = this.items.keySet();

        Graph graph = null;

        for (Graph g : grs) {
            if (!g.isReady()) {
                g.delete();
            }
        }
        super.onDestroy();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Custom dialog");
        adb.setPositiveButton("yes", null);

        return adb.create();
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        if (id == DIALOG) {
            // Находим TextView для отображения времени и показываем текущее
            // время

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode) {
            case REQUEST_CODE_INPUT_DATA:
                if (data == null) {return;}

                String expression = data.getStringExtra("expression");
                int color         = data.getIntExtra("color", 0);
                double left       = data.getDoubleExtra("left", 0);
                double right      = data.getDoubleExtra("right", 0);

                if (Math.abs(left - right) > 200) {
                    Toast.makeText(this, this.getResources().getString(R.string.error_type_3), Toast.LENGTH_SHORT).show();
                    return;
                }

                Node root = null;

                try {
                    Builder builder = new Builder();
                    root = builder.build(expression);

                } catch (Exception e) {
                    Toast.makeText(this, this.getResources().getString(R.string.error_type_2) + expression, Toast.LENGTH_SHORT).show();

                    Log.d("myparser", "ParserException happened");
                    return;
                }

                Vector3D col = new Vector3D(Color.red(color) / 255.0f,
                        Color.green(color) / 255.0f,
                        Color.blue(color)  / 255.0f);

                Graph graph = null;
                try {
                    graph = new Graph(root, left, right, col, this, this.memoryManager, this.openGLView);

                } catch (ComputationException | NullPointerException e) {
                    throw new RuntimeException(e);

                }

                GraphItemDrawerInfo item = new GraphItemDrawerInfo(this.drawerResult, this, expression).withColor(color);
                this.items.put(graph, item);

                break;

            case REQUEST_CODE_SETTINGS:

                SharedPreferences sp = getSharedPreferences(SettingsActivity.LOCALE_PREF_KEY, MODE_PRIVATE);
                String currentLanguage = sp.getString(SettingsActivity.LANGUAGE_KEY, SettingsActivity.ENGLISH_LOCALE);

                this.setLocale(currentLanguage);
                this.setTranslatedTexts();

                break;
        }



        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void verifyStoragePermissions(Activity activity) {

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }

    }

    @Override
    public void onGraphStatusChanged(Graph graph, String message) {

        GraphItemDrawerInfo item;

        this.displayProgressBar();

        switch (message) {
            case Graph.STATUS_ERROR:
                graph.delete();

                item = this.items.get(graph);

                if (item != null) {
                    item.delete();
                    this.items.remove(graph);
                }

                Toast.makeText(this, this.getResources().getString(R.string.error_type_2) + " " + graph.function, Toast.LENGTH_SHORT).show();

                break;

            default:

                item = this.items.get(graph);

                if (item != null) {

                    item.setStatus(message);
                    item.setLabels(this.getApplicationContext());
                }

                break;
        }

    }

    public void onDelete(View v) {

    }

    public void onTable(View v) {

    }

    @Override
    public void onDelete(GraphItemDrawerInfo graphItemDrawerInfo) {
        Set<Graph> grs = this.items.keySet();

        Graph graph = null;

        for (Graph g : grs) {
            if (this.items.get(g) == graphItemDrawerInfo) {
                graph = g;
                break;
            }
        }

        graph.delete();
        graphItemDrawerInfo.delete();

        this.items.remove(graph);
        this.memoryManager.deleteGraph(graph.id_hash);

        this.displayProgressBar();
    }

    @Override
    public void onDetails(GraphItemDrawerInfo graphItemDrawerInfo) {
        Set<Graph> grs = this.items.keySet();

        Graph graph = null;

        for (Graph g : grs) {
            if (this.items.get(g) == graphItemDrawerInfo) {
                graph = g;
                break;
            }
        }

        if (graph.isReady()) {
            Intent intent = new Intent(this, GraphDataOutputActivity.class);
            intent.putExtra("graph", (Serializable) graph);

            this.startActivityForResult(intent, 1);
        } else {
            Toast.makeText(this, getResources().getString(R.string.calculating), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SETTINGS);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem1 = menu.findItem(R.id.action_settings);
        menuItem1.setTitle(getResources().getString(R.string.action_settings));

      /*  MenuItem menuItem2 = menu.findItem(R.id.action_feedback);
        menuItem2.setTitle(getResources().getString(R.string.feedback));*/
        return super.onPrepareOptionsMenu(menu);
    }

    private void setLocale(String localeName) {

        Locale myLocale = new Locale(localeName);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }

    private void setTranslatedTexts() {

        CheckBox checkBox = (CheckBox) this.findViewById(R.id.checkbox);
        checkBox.setText(getResources().getString(R.string.grid));

        if (this.graphs != null) {
            for (int i = 0; i < this.graphs.size(); i ++) {
                Graph graph = this.graphs.get(i);
                GraphItemDrawerInfo item = this.items.get(graph);

                if (item != null) {
                    item.setLabels(this.getApplicationContext());
                }

            }

            DividerDrawerItem item = new DividerDrawerItem();

            this.drawerResult.addItem(item);
            this.drawerResult.removeItem(this.drawerResult.getDrawerItems().size() - 1);
        }

    }

    private void displayProgressBar() {

        boolean b = true;
        Set<Graph> graphs = this.items.keySet();

        for (Graph g : graphs) {
            if (!g.isReady()) {
                b = false;
                if (this.progressBar.getVisibility() == View.GONE) {
                    this.progressBar.setVisibility(View.VISIBLE);
                }
            }
        }

        if (b) {
            this.progressBar.setVisibility(View.GONE);
        }
    }

}