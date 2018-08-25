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
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import com.example.android.gourmetcustard.data.CustardContract.CustardEntry;

public class CustardCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link CustardCursorAdapter}.
     *
     * @param context The context
     * @param cursor  The cursor from which to get the data.
     */
    CustardCursorAdapter(Context context, Cursor cursor) {

        super(context, cursor, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.custard_item, parent, false);
    }

    /**
     * This method binds the custard entry (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current custard entry can be set on the name
     * TextView in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Find current Sale/Quantity button.
        final Button saleQtyButton = view.findViewById(R.id.sale_qty_button);

        // Find all the TextViews for displaying item.
        final TextView nameTextView = view.findViewById(R.id.item_name);
        final TextView sizeTextView = view.findViewById(R.id.item_size);
        final TextView priceTextView = view.findViewById(R.id.item_price);
        final TextView dateTextView = view.findViewById(R.id.item_date);

        // Get and assign current values based on column indices.
        final int currentRowId = cursor.getInt(cursor.getColumnIndex(CustardEntry._ID));
        final String name = cursor.getString(cursor.getColumnIndex(CustardEntry.COLUMN_CUSTARD_NAME));
        final double price = cursor.getDouble(cursor.getColumnIndex(CustardEntry.COLUMN_CUSTARD_PRICE));
        final int quantity = cursor.getInt(cursor.getColumnIndex(CustardEntry.COLUMN_CUSTARD_QUANTITY));
        final String date = cursor.getString(cursor.getColumnIndex(CustardEntry.COLUMN_CUSTARD_INVENTORY_DATE));

        // Initialize size String.
        String size = null;

        // Assign size String based on current item's size value.
        switch (cursor.getInt(cursor.getColumnIndex(CustardEntry.COLUMN_CUSTARD_SIZE))) {
            case CustardEntry.CUSTARD_SIZE_PINT:
                size = context.getString(R.string.size_pint);
                break;

            case CustardEntry.CUSTARD_SIZE_QUART:
                size = context.getString(R.string.size_quart);
                break;

            case CustardEntry.CUSTARD_SIZE_HALF_GALLON:
                size = context.getString(R.string.size_half_gallon);
                break;

            case CustardEntry.CUSTARD_SIZE_GALLON:
                size = context.getString(R.string.size_gallon);
                break;
        }

        // Set decimal format to display US dollars.
        DecimalFormat dollars = new DecimalFormat(context.getString(R.string.dollar_pattern));

        // Update TextViews with current cursor values.
        saleQtyButton.setText(String.valueOf(quantity));
        nameTextView.setText(name);
        sizeTextView.setText(size);
        priceTextView.setText(dollars.format(price));
        dateTextView.setText(date);

        // Create and set OnClickListener to SALE button.
        final OnClickListener saleButtonListener = new OnClickListener() {
            @Override
            public void onClick(View v) {

                // If quantity is greater than zero...
                if (quantity > 0) {

                    // Decrement quantity by one (1) unit.
                    final int decrQuantity = quantity - 1;
                    ContentValues values = new ContentValues();
                    values.put(CustardEntry.COLUMN_CUSTARD_QUANTITY, decrQuantity);
                    final Uri updateUri = ContentUris.withAppendedId(CustardEntry.CONTENT_URI, currentRowId);
                    context.getContentResolver().update(updateUri, values, null, null);

                } else {
                    // Toast when quantity has reached zero.
                    Toast.makeText(context, R.string.absolute_zero_attained, Toast.LENGTH_SHORT).show();
                }
            }
        };
        saleQtyButton.setOnClickListener(saleButtonListener);
    }
}
