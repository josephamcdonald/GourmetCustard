package com.example.android.gourmetcustard.data;

/*
 * Grow With Google Challenge Scholarship: Android Basics
 * Project: 9
 * Version: 3.0
 * App Name: Gourmet Custard
 * Author: Joseph McDonald
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.gourmetcustard.data.CustardContract.CustardEntry;

public class CustardDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Gourmet Custard database name.
    private static final String DATABASE_NAME = "gourmet_custard.db";

    // Custard inventory table creation.
    private static final String SQL_CREATE_CUSTARD_TABLE = "CREATE TABLE "
            + CustardEntry.TABLE_NAME + " ("
            + CustardEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CustardEntry.COLUMN_CUSTARD_NAME + " TEXT NOT NULL, "
            + CustardEntry.COLUMN_CUSTARD_SIZE + " INTEGER NOT NULL, "
            + CustardEntry.COLUMN_CUSTARD_PRICE + " DECIMAL(4,2) NOT NULL, "
            + CustardEntry.COLUMN_CUSTARD_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
            + CustardEntry.COLUMN_CUSTARD_INVENTORY_DATE + " TEXT NOT NULL, "
            + CustardEntry.COLUMN_CUSTARD_SUPPLIER_NAME + " TEXT NOT NULL, "
            + CustardEntry.COLUMN_CUSTARD_SUPPLIER_PHONE + " TEXT NOT NULL);";

    // For future use.
    // private static final String DATABASE_ALTER_CUSTARD_TABLE_1 =   /* future use */

    CustardDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_CUSTARD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For future use.
        // if (oldVersion < 2) {
        //     db.execSQL(DATABASE_ALTER_CUSTARD_TABLE_1);
        // }
        // if (oldVersion < 3) {
        //     db.execSQL(DATABASE_ALTER_CUSTARD_TABLE_2);
        // }
    }
}