package com.example.android.gourmetcustard;

/*
 * Grow With Google Challenge Scholarship: Android Basics
 * Project: 9
 * Version: 3.0
 * App Name: Gourmet Custard
 * Author: Joseph McDonald
 */

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.app.LoaderManager;
import android.content.CursorLoader;

import com.example.android.gourmetcustard.data.CustardContract.CustardEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // CustardLoader ID CONSTANT.
    private static final int CUSTARD_LOADER_ID = 0;

    // Declare CustardCursorAdapter.
    private CustardCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the ListView which will be populated with the custard data.
        final ListView custardListView = findViewById(R.id.custard_list_view);

        // Find and set empty table view to the ListView, so that it displays when list has 0 items.
        final View emptyView = findViewById(R.id.empty_view);
        custardListView.setEmptyView(emptyView);

        // Instantiate new CursorAdapter and set to the ListView.
        cursorAdapter = new CustardCursorAdapter(this, null);
        custardListView.setAdapter(cursorAdapter);

        // Setup item click listener.
        OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.setData(ContentUris.withAppendedId(CustardEntry.CONTENT_URI, id));
                startActivity(intent);
            }
        };
        custardListView.setOnItemClickListener(itemClickListener);

        // Setup add FAB listener and start EditActivity.
        OnClickListener fabAddListener = new OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, EditActivity.class));
            }
        };
        final FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(fabAddListener);

        // Initialize Loader.
        getLoaderManager().initLoader(CUSTARD_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Project the custard inventory table columns.
        String[] projection = {
                CustardEntry._ID,
                CustardEntry.COLUMN_CUSTARD_NAME,
                CustardEntry.COLUMN_CUSTARD_SIZE,
                CustardEntry.COLUMN_CUSTARD_PRICE,
                CustardEntry.COLUMN_CUSTARD_QUANTITY,
                CustardEntry.COLUMN_CUSTARD_INVENTORY_DATE,
                CustardEntry.COLUMN_CUSTARD_SUPPLIER_NAME,
                CustardEntry.COLUMN_CUSTARD_SUPPLIER_PHONE};

        return new CursorLoader(this,
                CustardEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        cursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu options from the res/menu/menu_main.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu.
        switch (item.getItemId()) {

            // Respond to click on the "Insert Dummy Entry" menu option.
            case R.id.action_insert_dummy_entry:
                insertDummyEntry();
                return true;

            // Respond to click on the "Delete All Entries" menu option.
            case R.id.action_delete_all_entries:
                deleteAllEntries();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // Insert dummy entry into the custard_inventory table for record create functionality.
    private void insertDummyEntry() {

        // Default custard input quantity CONSTANT.
        final int DEFAULT_QUANTITY = 12;

        // Get today's date/time.
        SimpleDateFormat date = new SimpleDateFormat(getString(R.string.date_pattern), Locale.US);
        String now = date.format(new Date());

        // Create new map of values, where column names are the keys.
        ContentValues values = new ContentValues();
        values.put(CustardEntry.COLUMN_CUSTARD_NAME, CustardEntry.CUSTARD_CHERRY_AMARETTO_CHEESECAKE);
        values.put(CustardEntry.COLUMN_CUSTARD_SIZE, CustardEntry.CUSTARD_SIZE_PINT);
        values.put(CustardEntry.COLUMN_CUSTARD_PRICE, CustardEntry.CUSTARD_PRICE_PINT);
        values.put(CustardEntry.COLUMN_CUSTARD_QUANTITY, DEFAULT_QUANTITY);
        values.put(CustardEntry.COLUMN_CUSTARD_INVENTORY_DATE, now);
        values.put(CustardEntry.COLUMN_CUSTARD_SUPPLIER_NAME, CustardEntry.SUPPLIER_NAME_KOPPS);
        values.put(CustardEntry.COLUMN_CUSTARD_SUPPLIER_PHONE, CustardEntry.SUPPLIER_PHONE_KOPPS);

        // Insert new row, returning the URI of the new row.
        Uri returnUri = getContentResolver().insert(CustardEntry.CONTENT_URI, values);

        // For confirmation.
        Log.v(getString(R.string.main_activity), getString(R.string.new_entry) + returnUri);
    }

    // Quickly delete all entries in the custard_inventory table.
    private void deleteAllEntries() {

        int rowsDeleted = getContentResolver().delete(CustardEntry.CONTENT_URI, null, null);

        Log.v(getString(R.string.main_activity), rowsDeleted + getString(R.string.entries_deleted));
    }
}