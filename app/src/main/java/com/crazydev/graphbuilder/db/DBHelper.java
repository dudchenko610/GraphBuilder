package com.crazydev.graphbuilder.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "GraphStorage", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE graph_info ("
              + "id integer primary key autoincrement,"
              + "funct            text,"
              + "delimiters       text,"
              + "asymptotes       text,"
              + "zeros            text,"
              + "punctured_points text,"
              + "color_x          float,"
              + "color_y          float,"
              + "color_z          float);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
