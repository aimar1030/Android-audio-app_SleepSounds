package com.zenlabs.sleepsounds.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zenlabs.sleepsounds.model.MAlarm;

import java.util.ArrayList;

/**
 * Created by fedoro on 5/26/16.
 */
public class AlarmDBHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "AlarmDB";
    private static final String TABLE_VALUE = "alarm";

    public AlarmDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create ListViewModel table
        String CREATE_LISTVIEWMODEL_TABLE = "CREATE TABLE "+TABLE_VALUE+" ( "
                +"ID INTEGER PRIMARY KEY AUTOINCREMENT, " +"category_id TEXT, "+"fade_in_time INTEGER DEFAULT 0, "+"snooze BOOLEAN DEFAULT FALSE, "+"minutes_fire INTEGER DEFAULT 0, "+"hours_fire INTEGER DEFAULT 0, "+"status INTEGER DEFAULT 0, "+"is_timer BOOLEAN DEFAULT FALSE, "+"is_power BOOLEAN DEFAULT FALSE, "+"is_counter BOOLEAN DEFAULT FALSE, "+"fire_seconds INTEGER DEFAULT 0, "+"unique_id TEXT)";

        // create ListViewModel table
        db.execSQL(CREATE_LISTVIEWMODEL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS AlarmDB");

        // create fresh books table
        this.onCreate(db);
    }

    // ListViewModel Table Columns names
    private static final String KEY_ID = "ID";
    private static final String KEY_UNIQUEID = "unique_id";
    private static final String KEY_CATEGORY_ID = "category_id";
    private static final String KEY_FADE_IN_TIME = "fade_in_time";
    private static final String KEY_SNOOZE = "snooze";
    private static final String KEY_MINUTES_FIRE = "minutes_fire";
    private static final String KEY_HOURE_FIRE = "hours_fire";
    private static final String KEY_STATUS = "status";
    private static final String KEY_IS_TIMER = "is_timer";
    private static final String KEY_IS_POWER = "is_power";
    private static final String KEY_IS_COUNTER = "is_counter";
    private static final String KEY_FIRE_SECONDS = "fire_seconds";

    private static final String[] COLUMNS = {KEY_ID, KEY_CATEGORY_ID, KEY_FADE_IN_TIME, KEY_SNOOZE, KEY_MINUTES_FIRE, KEY_HOURE_FIRE, KEY_STATUS, KEY_IS_TIMER, KEY_IS_POWER, KEY_IS_COUNTER, KEY_FIRE_SECONDS, KEY_UNIQUEID};

    public void addAlarm(MAlarm model){

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_ID, model.category_id);
        values.put(KEY_FADE_IN_TIME, model.fade_in_time);
        values.put(KEY_SNOOZE, model.isSnooze);
        values.put(KEY_MINUTES_FIRE, model.minutes_fire);
        values.put(KEY_HOURE_FIRE, model.hours_fire);
        values.put(KEY_STATUS, model.status);
        values.put(KEY_IS_TIMER, model.isTimer);
        values.put(KEY_IS_POWER, model.isPower);
        values.put(KEY_IS_COUNTER, model.isCounter);
        values.put(KEY_FIRE_SECONDS, model.fire_seconds);
        values.put(KEY_UNIQUEID, model.unique_id);

        // 3. insert
        db.insert(TABLE_VALUE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public MAlarm getModel(int index){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_VALUE, // a. table
                        COLUMNS, // b. column names
                        " ID = ?", // c. selections
                        new String[] { String.valueOf(index) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        MAlarm temp = new MAlarm();
        temp.category_id = cursor.getString(1);
        temp.fade_in_time = cursor.getInt(2);
        temp.isSnooze = cursor.getInt(3) > 0;
        temp.minutes_fire = cursor.getInt(4);
        temp.hours_fire = cursor.getInt(5);
        temp.status = cursor.getInt(6);
        temp.isTimer = cursor.getInt(7) > 0;
        temp.isPower = cursor.getInt(8) > 0;
        temp.isCounter = cursor.getInt(9) > 0;
        temp.fire_seconds = cursor.getLong(10);
        temp.unique_id = cursor.getString(11);

        db.close();
        // 5. return book
        return temp;
    }

    // Get All Books
    public ArrayList<MAlarm> getAllModels() {
        ArrayList<MAlarm> models=new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM "+TABLE_VALUE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        MAlarm temp = null;
        if (cursor.moveToFirst()) {
            do {
                temp = new MAlarm();
                temp.category_id = cursor.getString(1);
                temp.fade_in_time = cursor.getInt(2);
                temp.isSnooze = cursor.getInt(3) > 0;
                temp.minutes_fire = cursor.getInt(4);
                temp.hours_fire = cursor.getInt(5);
                temp.status = cursor.getInt(6);
                temp.isTimer = cursor.getInt(7) > 0;
                temp.isPower = cursor.getInt(8) > 0;
                temp.isCounter = cursor.getInt(9) > 0;
                temp.fire_seconds = cursor.getLong(10);
                temp.unique_id = cursor.getString(11);
                models.add(temp);
            } while (cursor.moveToNext());
        }
        db.close();

        // return books
        return models;
    }
    // Updating single book
    public int update_model(MAlarm model) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        /* 2. create ContentValues to add key "column"/value */
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_ID, model.category_id);
        values.put(KEY_FADE_IN_TIME, model.fade_in_time);
        values.put(KEY_SNOOZE, model.isSnooze);
        values.put(KEY_MINUTES_FIRE, model.minutes_fire);
        values.put(KEY_HOURE_FIRE, model.hours_fire);
        values.put(KEY_STATUS, model.status);
        values.put(KEY_IS_TIMER, model.isTimer);
        values.put(KEY_IS_POWER, model.isPower);
        values.put(KEY_IS_COUNTER, model.isCounter);
        values.put(KEY_FIRE_SECONDS, model.fire_seconds);
        values.put(KEY_UNIQUEID, model.unique_id);

        // 3. updating row
        int i = db.update(TABLE_VALUE, //table
                values, // column/value
                KEY_UNIQUEID+" = ?", // selections
                new String[] { model.unique_id}); //selection args
        // 4. close
        db.close();

        return i;

    }

    // Deleting single book
    public void delete_model(MAlarm model) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. delete
        db.delete(TABLE_VALUE,
                KEY_UNIQUEID+" = ?",
                new String[] { String.valueOf(model.unique_id) });

        // 3. close
        db.close();
    }

    public void delete_all(){
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. delete
        db.delete(TABLE_VALUE,null,null);
        // 3. close
        db.close();
    }
}
