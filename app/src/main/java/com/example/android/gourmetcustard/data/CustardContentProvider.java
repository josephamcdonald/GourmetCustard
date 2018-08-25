package com.example.android.gourmetcustard.data;

/*
 * Grow With Google Challenge Scholarship: Android Basics
 * Project: 9
 * Version: 3.0
 * App Name: Gourmet Custard
 * Author: Joseph McDonald
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.gourmetcustard.data.CustardContract.CustardEntry;

import java.util.Objects;

public class CustardContentProvider extends ContentProvider {

    private static final String LOG_TAG = CustardContentProvider.class.getSimpleName();

    private CustardDbHelper dbHelper;

    // URI matcher code for the content URI for the custard_inventory table.
    private static final int CUSTARD = 100;

    // URI matcher code for the content URI for a single entry in the custard_inventory table.
    private static final int CUSTARD_ID = 101;

    // UriMatcher object to match a content URI to a corresponding code.
    // The input passed into the constructor represents the code to return for the root URI.
    // It's common to use NO_MATCH as the input for this case.
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        uriMatcher.addURI(CustardContract.CONTENT_AUTHORITY, CustardContract.PATH_CUSTARD, CUSTARD);
        uriMatcher.addURI(CustardContract.CONTENT_AUTHORITY, CustardContract.PATH_CUSTARD + "/#", CUSTARD_ID);
    }

    @Override
    public boolean onCreate() {

        dbHelper = new CustardDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Create and/or open the database in read mode.
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor;

        int match = uriMatcher.match(uri);
        switch (match) {

            case CUSTARD:
                cursor = db.query(CustardEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case CUSTARD_ID:
                selection = CustardEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(CustardEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI: " + uri);
        }

        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = uriMatcher.match(uri);
        switch (match) {

            case CUSTARD:
                return CustardEntry.CONTENT_LIST_TYPE;

            case CUSTARD_ID:
                return CustardEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final int match = uriMatcher.match(uri);

        switch (match) {
            case CUSTARD:
                return insertCustardEntry(uri, values);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        // Get the database in write mode.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        final int match = uriMatcher.match(uri);

        int rowsDeleted;

        switch (match) {

            case CUSTARD:
                // Delete all rows that match the selection and selection args.
                rowsDeleted = db.delete(CustardEntry.TABLE_NAME, selection, selectionArgs);

                if (rowsDeleted != 0) {
                    Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
                }

                return rowsDeleted;

            case CUSTARD_ID:
                // Delete a single row given by the ID in the URI.
                selection = CustardEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(CustardEntry.TABLE_NAME, selection, selectionArgs);

                if (rowsDeleted != 0) {
                    Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
                }

                return rowsDeleted;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = uriMatcher.match(uri);

        switch (match) {

            case CUSTARD:
                return updateCustardEntry(uri, values, selection, selectionArgs);

            case CUSTARD_ID:
                // For the CUSTARD_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = CustardEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateCustardEntry(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    // Insert custard entry into the database with the given content values. Return the new content URI
    // for that specific row in the database.
    private Uri insertCustardEntry(Uri uri, ContentValues values) {

        // If the quantity is provided, check that it's greater than or equal to 0.
        Integer quantity = values.getAsInteger(CustardEntry.COLUMN_CUSTARD_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Custard entry requires valid quantity");
        }

        // Get the database in write mode.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Insert the new CUSTARD with the given values
        long id = db.insert(CustardEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    // Update custard entry in the database with the given content values. Apply the changes to the rows
    // specified in the selection and selection arguments (which could be 0 or 1 or more entries).
    // Return the number of rows that were successfully updated.
    private int updateCustardEntry(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If there are no values to update, then go no further.
        if (values.size() == 0) {
            return 0;
        }

        // If the {@link CustardEntry#COLUMN_CUSTARD_QUANTITY} key is present,
        // check that the quantity value is valid.
        if (values.containsKey(CustardEntry.COLUMN_CUSTARD_QUANTITY)) {
            // Check that the quantity is greater than or equal to 0.
            Integer quantity = values.getAsInteger(CustardEntry.COLUMN_CUSTARD_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Custard entry requires valid quantity");
            }
        }

        // Get the database in write mode.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsUpdated = db.update(CustardEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
