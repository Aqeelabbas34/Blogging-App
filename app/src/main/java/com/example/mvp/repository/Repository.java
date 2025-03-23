package com.example.mvp.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mvp.db.DbHelper;
import com.example.mvp.model.BlogModel;

import java.util.ArrayList;
import java.util.List;


 //Repository class that manages database operations for BlogModel.
 // It provides methods to perform Create, Read, Update, and Delete (CRUD) operations.

public class Repository {
    private final DbHelper dbHelper;

  //Constructor
    public Repository(Context context) {
        dbHelper = new DbHelper(context);
    }


    //  Create (Isert) Function



    public void createBlog(BlogModel post) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DbHelper.COLUMN_TITLE, post.getTitle());
        values.put(DbHelper.COLUMN_CONTENT, post.getContent());
        values.put(DbHelper.COLUMN_IMAGE_PATH, post.getImagePath());
        values.put(DbHelper.COLUMN_DATE_TIME, post.getDateTime());

        db.insert(DbHelper.TABLE_BLOG, null, values);
        db.close();
    }


    //  FETCH (Read)

    public List<BlogModel> fetchBlogs() {
        List<BlogModel> blogList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query to fetch all blog posts, sorted by newest first
        Cursor cursor = db.query(DbHelper.TABLE_BLOG, null, null, null, null, null, "id DESC");

        if (cursor.moveToFirst()) {
            do {
                BlogModel post = new BlogModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_CONTENT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_IMAGE_PATH)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_DATE_TIME))
                );
                blogList.add(post);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return blogList;
    }


    //  UPDATE (edit)


    public void updateBlog(BlogModel post) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DbHelper.COLUMN_TITLE, post.getTitle());
        values.put(DbHelper.COLUMN_CONTENT, post.getContent());
        values.put(DbHelper.COLUMN_IMAGE_PATH, post.getImagePath());
        values.put(DbHelper.COLUMN_DATE_TIME, post.getDateTime());

        db.update(DbHelper.TABLE_BLOG, values, DbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(post.getId())});
        db.close();
    }

    // DELETE (Remove)



    public void deleteBlog(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DbHelper.TABLE_BLOG, DbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

   //DELETE MULTIPLE
    public void deleteBlogs(List<Integer> ids) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int id : ids) {
            db.delete(DbHelper.TABLE_BLOG, DbHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)});
        }

        db.close();
    }
}
