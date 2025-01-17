package com.skillcourt.ui.main;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.lang.annotation.Target;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SkillCourt.db";
    public static final String TABLE_SESSION = "skillcourt_session";
    public static final String S_COL_1 = "SESSION_ID";
    public static final String S_COL_2 = "SESSION_DATE";
    public static final String TABLE_NAME = "skillcourt_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "DATE";
    public static final String COL_3 = "TIME";
    public static final String COL_4 = "SCORE";
    public static final String COL_5 = "HIT";
    public static final String COL_6 = "SESSIONPLAYER_ID";
    public static final String COL_7 = "NOTES";
    public static final String COL_8 = "GAMETYPE";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_SESSION + " (SESSION_ID INTEGER PRIMARY KEY, " +
                "SESSION_DATE TEXT)");

        sqLiteDatabase.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "DATE TEXT, " +
                "TIME TEXT, " +
                "SCORE TEXT, " +
                "HIT TEXT, " +
                "SESSIONPLAYER_ID INTEGER, " +
                "NOTES TEXT, " +
                "GAMETYPE TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertSessionData(Integer id, String session_Date)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(S_COL_1, id);
        contentValues.put(S_COL_2, session_Date);

        long result = sqLiteDatabase.insert(TABLE_SESSION, null, contentValues);

        if(result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    public boolean insertData(String date, String time, String score, String hit,Integer sessionPlayerID, String notes, String gameType)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, date);
        contentValues.put(COL_3, time);
        contentValues.put(COL_4, score);
        contentValues.put(COL_5, hit);
        contentValues.put(COL_6, sessionPlayerID);
        contentValues.put(COL_7, notes);
        contentValues.put(COL_8, gameType);

        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        if(result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public Cursor getAllPlayerData()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor res = sqLiteDatabase.rawQuery("select * from "+TABLE_NAME+" ORDER BY ID DESC", null);
        return res;
    }

    public Cursor getPlayerSessionDATA(String sId)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor res = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME +" WHERE SESSIONPLAYER_ID=" + sId + " ORDER BY ID DESC", null);
        return res;
    }

    public Cursor getAllSessionData()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor res = sqLiteDatabase.rawQuery("select * from "+TABLE_SESSION+" ORDER BY SESSION_ID DESC", null);
        return res;
    }

    public Cursor getAllSessionID(String id)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor res = sqLiteDatabase.rawQuery("select * from "+TABLE_SESSION+" WHERE SESSION_ID=" + id, null);
        return res;
    }


    public Integer deleteData(String id)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }

    public Integer deleteSessionData(String id)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_SESSION, "SESSION_ID = ?", new String[]{id});
    }

    public Integer deletePlayerInSessionData(String id)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_NAME, "SESSIONPLAYER_ID = ?", new String[]{id});
    }

    public boolean updatePlayerNotes(String id, String date, String time, String score, String hit, String sessionPlayerID, String notes, String gameType)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, date);
        contentValues.put(COL_3, time);
        contentValues.put(COL_4, score);
        contentValues.put(COL_5, hit);
        contentValues.put(COL_6, sessionPlayerID);
        contentValues.put(COL_7, notes);
        contentValues.put(COL_8, gameType);
        sqLiteDatabase.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});
        return true;
    }

    public Cursor getPlayerSessionGameTypeDATA(String sId, String gameType)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor res = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME +" WHERE SESSIONPLAYER_ID = ? AND GAMETYPE = ?" + " ORDER BY ID DESC", new String[] {sId, gameType});
        return res;
    }

}
