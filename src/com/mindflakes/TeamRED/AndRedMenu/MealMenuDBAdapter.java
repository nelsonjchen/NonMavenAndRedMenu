/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.mindflakes.TeamRED.AndRedMenu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.mindflakes.TeamRED.menuClasses.*;
import java.util.ArrayList;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class MealMenuDBAdapter {

    public static final String KEY_MEALMENU_NAME = "name";
    public static final String KEY_MEALMENU_MEALNAME = "mealname";
    public static final String KEY_MEALMENU_START = "start";
    public static final String KEY_MEALMENU_END = "end";
    public static final String KEY_MEALMENU_MOD = "mod";
    public static final String KEY_ROWID = "_id";
    
    public static final String KEY_VENUE_NAME = "name";
    public static final String KEY_VENUE_MENUROWID = "menurowid";
    
    public static final String KEY_FOODITEM_NAME = "name";
    //Should be 0 if not either, 1 if vegetarian, and 2 if vegan (vegan implies vegetarian)
    public static final String KEY_FOODITEM_FOOD_TYPE = "foodtype";
    public static final String KEY_FOODITEM_VENUEROWID = "venuerowid";

    private static final String TAG = "MealMenuDBAdapter";
    private DatabaseHelper mDbHelper;


    private SQLiteDatabase mDb;

    private static final String MENU_DATABASE_TABLE = "menutable";
    private static final String VENUE_DATABASE_TABLE = "venuetable";
    private static final String FOOD_DATABASE_TABLE = "foodtable";
    
    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;
    
    /**
     * Database creation sql statement
     */
    private static final String MENU_DATABASE_CREATE =
        "create table "+MENU_DATABASE_TABLE+" ("+KEY_ROWID+" integer primary key autoincrement, "
        + KEY_MEALMENU_NAME + " text not null, "
        + KEY_MEALMENU_NAME+" text not null, "
        + KEY_MEALMENU_START+" integer not null, "
        + KEY_MEALMENU_END+" integer not null, "
        + KEY_MEALMENU_MOD+" integer not null);";

    private static final String VENUE_DATABASE_CREATE =
        "create table "+VENUE_DATABASE_TABLE+" ("+KEY_ROWID+" integer primary key autoincrement, "
        + KEY_VENUE_NAME + " text not null, "
        + KEY_VENUE_MENUROWID+" integer not null);";
    
    private static final String FOOD_DATABASE_CREATE =
        "create table "+FOOD_DATABASE_TABLE+" ("+KEY_ROWID+" integer primary key autoincrement, "
        + KEY_FOODITEM_NAME + " text not null, "
        + KEY_FOODITEM_FOOD_TYPE+" integer not null, "
        + KEY_FOODITEM_VENUEROWID+" integer not null);";
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

    	DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(MENU_DATABASE_CREATE);
            db.execSQL(VENUE_DATABASE_CREATE);
            db.execSQL(FOOD_DATABASE_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+MENU_DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+VENUE_DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+FOOD_DATABASE_TABLE);
            onCreate(db);
        }
    }
    
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public MealMenuDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public MealMenuDBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the noteh
     * @return rowId or -1 if failed
     */
    public long addMenu(MealMenu menu) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_MEALMENU_NAME, menu.getCommonsName());
        initialValues.put(KEY_MEALMENU_MEALNAME, menu.getMealName());
        initialValues.put(KEY_MEALMENU_START, menu.getMealInterval().getStartMillis());
        initialValues.put(KEY_MEALMENU_END, menu.getMealInterval().getEndMillis());
        initialValues.put(KEY_MEALMENU_MOD, menu.getModDate().getMillis());

        long menuID =  mDb.insert(MENU_DATABASE_TABLE, null, initialValues);
        for(Venue ven:menu.getVenues()){
        initialValues = new ContentValues();
        initialValues.put(KEY_VENUE_NAME,ven.getName());
        initialValues.put(KEY_VENUE_MENUROWID, menuID);
        long venueID=mDb.insert(VENUE_DATABASE_TABLE, null, initialValues);
        for(FoodItem:)
        }
    }

    /**
     * Delete the note with the given rowId
     *  
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllNotes() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_BODY}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_TITLE, KEY_BODY}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateNote(long rowId, String title, String body) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
