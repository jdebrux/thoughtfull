package com.example.thoughtfull2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.thoughtfull2.models.GratitudeModel;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {
    //constant variable database
    // below variable is for our database name.
    private static final String DB_NAME = "gratitudeDB";
    //database version
    private static final int DB_VERSION = 1;
    //table name
    private static final String TABLE_NAME = "gratefulTable";
    //column ID
    private static final String ID_COL = "id";
    // item description column
    private static final String NAME_COL = "description";

    // creating a constructor for our database handler.
    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_NAME + "("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT)";
        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
    }

    // this method is use to add new course to our sqlite database.
    public void addNewGratitude(String description) {

        //initialise variable for database
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // passing description along with key value pair
        values.put(NAME_COL, description);

        //passing content values to our table.
        db.insert(TABLE_NAME, null, values);

        //close database after insertion
        db.close();
    }

    // we have created a new method for reading all the courses.
    public ArrayList<GratitudeModel> readCourses() {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();

        // on below line we are creating a cursor with query to read data from database.
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        // on below line we are creating a new array list.
        ArrayList<GratitudeModel> courseModalArrayList = new ArrayList<>();

        // moving our cursor to first position.
        if (cursor.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                courseModalArrayList.add(new GratitudeModel(cursor.getString(1)));
            } while (cursor.moveToNext());
            // moving our cursor to next.
        }
        //closing our cursor and returning our array list.
        cursor.close();
        return courseModalArrayList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
