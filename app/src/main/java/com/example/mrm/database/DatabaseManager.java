package com.example.mrm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;


public class DatabaseManager {
    public static final String DB_NAME = "graphulatory database";
    public static final String DB_TABLE = "functions";
    public static final int DB_VERSION = 6;
    public static final String CREATE_TABLE =
            "CREATE TABLE " + DB_TABLE +
                    " (slot INTEGER PRIMARY KEY, function TEXT, color INT, active INT);";
    private SQLiteOpenHelper helper;
    private SQLiteDatabase db;
    private Context context;

    public void init_helper(){
        helper = new SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(CREATE_TABLE);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                Log.w("Functions table", "Upgrading database i.e. dropping table and recreating it");
                db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
                onCreate(db);
            }
            public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                onUpgrade(db, oldVersion, newVersion);
            }

        };
    }

    public DatabaseManager(Context context){
        this.context = context;
        init_helper();
        this.db = helper.getWritableDatabase();

    }
    public DatabaseManager openReadable() throws android.database.SQLException{
        init_helper();
        db = helper.getReadableDatabase();
        return this;
    }
    public void close(){
        helper.close();
    }

    public void addRow(int slot, String expr, int color, boolean active){
        ContentValues newFunSlot = new ContentValues();
        newFunSlot.put("slot", slot);
        newFunSlot.put("function",expr);
        newFunSlot.put("color", color);
        newFunSlot.put("active", active);
        try{db.insertWithOnConflict(DB_TABLE, "slot", newFunSlot, SQLiteDatabase.CONFLICT_REPLACE);}
        catch (Exception e){
            Log.e("Error inserting rows ", e.toString());
            e.printStackTrace();
        }
    }

    public void save(LinkedList<ContentValues> list){
        for(ContentValues cv : list){
            try{db.insertWithOnConflict(DB_TABLE, "slot", cv, SQLiteDatabase.CONFLICT_REPLACE);}
            catch (Exception e){
                Log.e("Error inserting rows ", e.toString());
                e.printStackTrace();
            }
        }
    }


    public LinkedList<ContentValues> getRows(){
        String[] columns = {"slot", "function","color", "active"};
        Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, null);
        LinkedList<ContentValues> list = new LinkedList<>();
        try {
            cursor.moveToFirst();
            while (!(cursor.isAfterLast())) {
                ContentValues funSlot = new ContentValues();
                funSlot.put("slot",cursor.getInt(0));
                funSlot.put("function", cursor.getString(1));
                funSlot.put("color", cursor.getInt(2));
                funSlot.put("active", cursor.getInt(3) > 0);
                list.push(funSlot);
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
            return list;
        }
    }

    public void dropTable(){
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);

    }
    public void createTable(){
        db.execSQL(CREATE_TABLE);
    }

}
