package com.example.mvp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    //database name and version
    public static final String DB_NAME = "blog_app.db";
    public static final int DB_VERSION = 1;

    //table name and column names
    public static final String TABLE_BLOG = "blog";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_IMAGE_PATH = "image_path";
    public static final String COLUMN_DATE_TIME = "date_time";

    //SQL statement to create blog table
    private static final String CREATE_TABLE_BLOG =
            "CREATE TABLE " + TABLE_BLOG + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +//autoincrement for unique id
                    COLUMN_TITLE + " TEXT, " + //column for title
                    COLUMN_CONTENT + " TEXT, " + //column for content
                    COLUMN_IMAGE_PATH + " TEXT, " + //column for image path
                    COLUMN_DATE_TIME + " TEXT" + //column for date and time
                    ")";

    //constructor for dbhelper
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BLOG);//create blog table
    }


   //called when database needs to be upgraded (e.g. from version 1 to 2)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOG);
        onCreate(db); //recreate the table
    }
}

