package com.zenlabs.sleepsounds.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zenlabs.sleepsounds.model.MSound;

import java.util.ArrayList;

/**
 * Created by fedoro on 5/26/16.
 */
public class SoundDBHelper extends SQLiteOpenHelper{

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "SleepSoundDB";
    private static final String TABLE_VALUE = "sound";

    public SoundDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create ListViewModel table
        String CREATE_LISTVIEWMODEL_TABLE = "CREATE TABLE "+TABLE_VALUE+"( "
                +"ID INTEGER PRIMARY KEY AUTOINCREMENT, " +"name TEXT, "+"background TEXT, "+"isUsable BOOLEAN DEFAULT FALSE, "+"sound TEXT, "+"type INTEGER DEFAULT 0, "+"uniqueId TEXT, "+"volume FLOAT DEFAULT 0)";

        // create ListViewModel table
        db.execSQL(CREATE_LISTVIEWMODEL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS SleepSoundDB");

        // create fresh books table
        this.onCreate(db);
    }

    // ListViewModel Table Columns names
    private static final String KEY_ID = "ID";
    private static final String KEY_NAME = "name";
    private static final String KEY_BACKGROUND = "background";
    private static final String KEY_ISUSABLE = "isUsable";
    private static final String KEY_SOUND = "sound";
    private static final String KEY_TYPE = "type";
    private static final String KEY_UNIQUEID = "uniqueId";
    private static final String KEY_VOLUME = "volume";

    private static final String[] COLUMNS = {KEY_ID,KEY_NAME,KEY_BACKGROUND, KEY_ISUSABLE, KEY_SOUND, KEY_TYPE, KEY_UNIQUEID, KEY_VOLUME};

    public void addSound(MSound model){

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME,model.getName());
        values.put(KEY_BACKGROUND,model.getBackground());
        values.put(KEY_ISUSABLE,model.isUsable);
        values.put(KEY_SOUND, model.getSound());
        values.put(KEY_TYPE, model.type);
        values.put(KEY_UNIQUEID, model.getUniqueId());
        values.put(KEY_VOLUME, model.volume);

        // 3. insert
        db.insert(TABLE_VALUE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public MSound getModel(int index){

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
        MSound temp = new MSound();
        temp.setName(cursor.getString(1));
        temp.setBackground(cursor.getString(2));
        temp.isUsable = cursor.getInt(3) > 0;
        temp.setSound(cursor.getString(4));
        temp.type = cursor.getInt(5);
        temp.setUniqueId(cursor.getString(6));
        temp.volume = cursor.getFloat(7);

        db.close();
        // 5. return book
        return temp;
    }

    // Get All Books
    public ArrayList<MSound> getAllModels() {
        ArrayList<MSound> models=new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM "+TABLE_VALUE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        MSound temp = null;
        if (cursor.moveToFirst()) {
            do {
                temp = new MSound();
                temp.setName(cursor.getString(1));
                temp.setBackground(cursor.getString(2));
                temp.isUsable = cursor.getInt(3) > 0;
                temp.setSound(cursor.getString(4));
                temp.type = cursor.getInt(5);
                temp.setUniqueId(cursor.getString(6));
                temp.volume = cursor.getFloat(7);
                models.add(temp);
            } while (cursor.moveToNext());
        }
        db.close();

        // return books
        return models;
    }
    // Updating single book
    public int update_model(MSound model) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        /* 2. create ContentValues to add key "column"/value */
        ContentValues values = new ContentValues();
        values.put(KEY_NAME,model.getName());
        values.put(KEY_BACKGROUND, model.getBackground());
        values.put(KEY_ISUSABLE, model.isUsable);
        values.put(KEY_SOUND, model.getSound());
        values.put(KEY_TYPE, model.type);
        values.put(KEY_UNIQUEID, model.getUniqueId());
        values.put(KEY_VOLUME, model.volume);

        // 3. updating row
        int i = db.update(TABLE_VALUE, //table
                values, // column/value
                KEY_UNIQUEID+" = ?", // selections
                new String[] { model.getUniqueId()}); //selection args
        // 4. close
        db.close();

        return i;

    }

    // Deleting single book
    public void delete_model(MSound model) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. delete
        db.delete(TABLE_VALUE,
                KEY_UNIQUEID+" = ?",
                new String[] { String.valueOf(model.getUniqueId()) });

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
