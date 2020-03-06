package com.crazydev.graphbuilder.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.crazydev.graphbuilder.appspecific.Graph;
import com.crazydev.graphbuilder.appspecific.OnGraphStatus;
import com.crazydev.graphbuilder.math.Vector3D;
import com.crazydev.graphbuilder.rendering.OpenGLRenderer;

import java.util.ArrayList;

import bobenus.collections.BobenusList;
import bobenus.math.Asymptote;
import bobenus.math.Segment;

public class MemoryManager {

    private DBHelper dbHelper;

    public MemoryManager(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public void saveGraph(Graph graph) {

        try {
            ContentValues cv  = new ContentValues();
            SQLiteDatabase db = this.dbHelper.getWritableDatabase();

            Log.d("zbs", "asymptotes = " + graph.asymptotes.toString());

            cv.put("funct"           , graph.function);
            cv.put("delimiters"      , graph.allowableRange.toString());
            cv.put("asymptotes"      , graph.asymptotes.toString());
            cv.put("zeros"           , graph.zeros.toString());
            cv.put("punctured_points", graph.puncturedPoints.toString());
            cv.put("color_x"         , graph.color.x);
            cv.put("color_y"         , graph.color.y);
            cv.put("color_z"         , graph.color.z);

            long rowID = db.insert("graph_info", null, cv);
            dbHelper.close();
        } catch(Exception e) {

        }


    }

    public void updateDate(Graph graph) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv  = new ContentValues();

        int updCount = db.update("graph_info", cv, "id = ?", new String[] {String.valueOf(graph.id_hash)});
        dbHelper.close();
    }

    public void deleteGraph(int id) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        int delCount = db.delete("graph_info", "id = " + id, null);
        dbHelper.close();
    }

    public void lookIntoDB() {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        Cursor c = db.query("graph_info", null, null, null, null, null, null);

        ArrayList<Graph> graphs = new ArrayList<Graph>();

        if (c.moveToFirst()) {
            // определить номера столбцов по имени в выборке
            int idColIndex               = c.getColumnIndex("id");
            int functColIndex            = c.getColumnIndex("funct");
            int delimitersColIndex       = c.getColumnIndex("delimiters");
            int asymptotesColIndex       = c.getColumnIndex("asymptotes");
            int zerosColIndex            = c.getColumnIndex("zeros");
            int punctured_pointsColIndex = c.getColumnIndex("punctured_points");
            int color_xColIndex          = c.getColumnIndex("color_x");
            int color_yColIndex          = c.getColumnIndex("color_y");
            int color_zColIndex          = c.getColumnIndex("color_z");

            do {
                String function   = c.getString(functColIndex);
                int id            = c.getInt(idColIndex);
                String delimiters = c.getString(delimitersColIndex);
                String asymptotes = c.getString(asymptotesColIndex);
                String zeros      = c.getString(zerosColIndex);
                float color_x     = c.getFloat(color_xColIndex);
                float color_y     = c.getFloat(color_yColIndex);
                float color_z     = c.getFloat(color_zColIndex);

                Log.d("tahh","ID = " + id + " function" + function);

            } while (c.moveToNext());

        } else {
            Log.d("TAGG", "0 rows");
        }

        c.close();

        dbHelper.close();

    }

    public ArrayList<Graph> getAllGraphs(OnGraphStatus onGraphStatus, OpenGLRenderer openGLRenderer) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        Cursor c = db.query("graph_info", null, null, null, null, null, null);

        ArrayList<Graph> graphs = new ArrayList<Graph>();

        if (c.moveToFirst()) {
            // определить номера столбцов по имени в выборке
            int idColIndex               = c.getColumnIndex("id");
            int functColIndex            = c.getColumnIndex("funct");
            int delimitersColIndex       = c.getColumnIndex("delimiters");
            int asymptotesColIndex       = c.getColumnIndex("asymptotes");
            int zerosColIndex            = c.getColumnIndex("zeros");
            int punctured_pointsColIndex = c.getColumnIndex("punctured_points");
            int color_xColIndex          = c.getColumnIndex("color_x");
            int color_yColIndex          = c.getColumnIndex("color_y");
            int color_zColIndex          = c.getColumnIndex("color_z");


            do {
                String function   = c.getString(functColIndex);
                int id            = c.getInt(idColIndex);
                String delimiters = c.getString(delimitersColIndex);
                String asymptotes = c.getString(asymptotesColIndex);
                String zeros      = c.getString(zerosColIndex);
                String puncturedP = c.getString(punctured_pointsColIndex);
                float color_x     = c.getFloat(color_xColIndex);
                float color_y     = c.getFloat(color_yColIndex);
                float color_z     = c.getFloat(color_zColIndex);

                try {
                    Vector3D color = new Vector3D(color_x, color_y, color_z);

                    String del[] = delimiters.split("l");
                    BobenusList dels = new BobenusList();

                    //       Log.d("debvd", function);
                    Log.d("debvd", del.length + "");

                    for (int i = 0; i < del.length; i ++) {

                        try {
                            if (del[i] != "") {
                                dels.add(new Segment(del[i].trim()));
                            }
                        } catch (Exception e) {
                            ///////// Pizdets here
                        }

                    }

                    Log.d("zbs", "asymptotes = " + asymptotes);

                    asymptotes = asymptotes.substring(1, asymptotes.length() - 1);

                    del = asymptotes.split(",");
                    ArrayList<Asymptote> asympts = new ArrayList<Asymptote>();

                    for (int i = 0; i < del.length; i ++) {

                        if (del[i] != "") {
                            asympts.add(new Asymptote(del[i].trim()));
                        }
                    }

                    zeros = zeros.substring(1, zeros.length() - 1);
                    del = zeros.split(",");
                    ArrayList<Double> zers = new ArrayList<Double>();

                    for (int i = 0; i < del.length; i ++) {

                        if (del[i] != "") {
                            zers.add(Double.parseDouble(del[i].trim()));
                        }

                    }

                    puncturedP = puncturedP.substring(1, puncturedP.length() - 1);
                    del = puncturedP.split(",");
                    ArrayList<Double> puncturedPoints = new ArrayList<Double>();

                    for (int i = 0; i < del.length; i ++) {

                        if (del[i] != "") {
                            puncturedPoints.add(Double.parseDouble(del[i].trim()));
                        }

                    }

                    Graph graph = new Graph(function, color, dels, zers, puncturedPoints, asympts, id, onGraphStatus, openGLRenderer);
                    graphs.add(graph);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } while (c.moveToNext());

        } else {
            Log.d("TAGG", "0 rows");
        }

        c.close();

        dbHelper.close();

        return graphs;
    }

    public int getLastAutoincrementedIdValue() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            Cursor cursor = db.query("sqlite_sequence", new String[]{"seq"}, "name = ?", new String[]{"graph_info"}, null, null, null);

            if (cursor.moveToFirst()) {
                int idColIndex = cursor.getColumnIndex("seq");
                int res        = cursor.getInt(idColIndex);
                cursor.close();

                db.close();

                return res;
            }


        } catch (Exception e) {
            return 0;
        }

        return 0;

    }

    public long getGraphCount () {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, "graph_info");
        db.close();

        return count;
    }

}
