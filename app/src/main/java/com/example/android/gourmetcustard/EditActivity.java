package com.example.android.gourmetcustard;

/*
 * Grow With Google Challenge Scholarship: Android Basics
 * Project: 9
 * Version: 3.0
 * App Name: Gourmet Custard
 * Author: Joseph McDonald
 */

import android.app.AlertDialog;
import android.content.ContentValues;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.content.CursorLoader;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.gourmetcustard.data.CustardContract.CustardEntry;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Spinner field to obtain the custard product name.
    private TextView nameEditText;

    // Spinner field to obtain the custard product size.
    private Spinner sizeSpinner;

    // TextView for product price.
    private EditText priceEditText;

    // EditText field to obtain the custard quantity.
    private EditText quantityEditText;

    // TextView for inventory date.
    private TextView inventoryDateTextView;

    // TextView for Supplier Name.
    private EditText supplierNameEditText;

    // TextView for Supplier Phone.
    private EditText supplierPhoneEditText;

    // Custard product name.
    private String productName;

    // Custard product size.
    private int productSize;

    // Custard product price.
    private double productPrice;

    // Custard product quantity;
    private int productQuantity;

    // Custard inventory date.
    private String inventoryDate;

    // Supplier Name.
    private String supplierName;

    // Suppler Phone Number.
    private String supplierPhone;

    // Add quantity button.
    private Button plusButton;

    // Subtract quantity button.
    private Button minusButton;

    // Zero quantity toast.
    private Toast zeroQuantityToast;

    // Content Uri for current custard entry.
    private Uri currentCustardUri;

    // Initialize entryHadChanged boolean to false.
    private boolean entryHasChanged = false;

    // CustardLoader ID CONSTANT.
    private static final int EXISTING_CUSTARD_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        currentCustardUri = intent.getData();

        // If current URI is null, ADD the entry, else EDIT the entry.
        if (currentCustardUri == null) {

            setTitle(getString(R.string.edit_activity_add_entry));
            // Invalidate the options menu, so the "Delete" menu option is hidden.
            invalidateOptionsMenu();

        } else {
            setTitle(getString(R.string.edit_activity_edit_entry));
            getLoaderManager().initLoader(EXISTING_CUSTARD_LOADER_ID, null, this);
        }

        // Find and assign the custard edit entry views.
        nameEditText = findViewById(R.id.edit_text_name);
        sizeSpinner = findViewById(R.id.edit_spinner_size);
        priceEditText = findViewById(R.id.edit_text_price);
        quantityEditText = findViewById(R.id.edit_text_quantity);
        inventoryDateTextView = findViewById(R.id.edit_view_inventory_date);
        supplierNameEditText = findViewById(R.id.edit_text_supplier_name);
        supplierPhoneEditText = findViewById(R.id.edit_text_supplier_phone);
        plusButton = findViewById(R.id.edit_plus_button);
        minusButton = findViewById(R.id.edit_minus_button);

        // Set Listeners to the input fields.
        nameEditText.setOnClickListener(editTextListener);
        sizeSpinner.setOnTouchListener(spinnerListener);
        priceEditText.setOnClickListener(editTextListener);
        quantityEditText.setOnClickListener(editTextListener);
        supplierNameEditText.setOnClickListener(editTextListener);
        supplierPhoneEditText.setOnClickListener(editTextListener);

        // Obtain date/time, format it and update inventory date text view.
        SimpleDateFormat date = new SimpleDateFormat(getString(R.string.date_pattern), Locale.US);
        inventoryDate = date.format(new Date());
        inventoryDateTextView.setText(inventoryDate);

        // Obtain custard size and quantity.
        obtainSize();
        obtainQuantity();

        // Setup call FAB and dial Supplier.
        OnClickListener fabCallListener = new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse(getString(R.string.tel) + supplierPhone));
                if (callIntent.resolveActivity(getPackageManager()) != null) {

                    startActivity(callIntent);
                }
            }
        };
        final FloatingActionButton fabCall = findViewById(R.id.fab_call);
        fabCall.setOnClickListener(fabCallListener);
    }

    private void obtainSize() {

        // Create adapter for size spinner. The list options are from the String array it will use
        // the spinner will use the default layout.
        ArrayAdapter sizeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_product_size_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 custard size per line.
        sizeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the size spinner.
        sizeSpinner.setAdapter(sizeSpinnerAdapter);

        // Create and set the item selected listener to the size spinner.
        OnItemSelectedListener sizeSpinnerListener = new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Update product size selected.
                productSize = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // Pint is the most popular product size so it is the default selection.
                productSize = CustardEntry.CUSTARD_SIZE_PINT;
            }
        };
        sizeSpinner.setOnItemSelectedListener(sizeSpinnerListener);
    }

    private void obtainQuantity() {

        // Toast when quantity has reached zero.
        zeroQuantityToast = Toast.makeText(this, getString(R.string.absolute_zero_attained),
                Toast.LENGTH_SHORT);

        // Setup OnClickListener to increase/decrease quantity by one (1) unit.
        OnClickListener quantityListener = new OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.edit_plus_button:

                        productQuantity = ++productQuantity;
                        entryHasChanged = true;
                        break;

                    case R.id.edit_minus_button:

                        if (productQuantity > 0) {
                            productQuantity = --productQuantity;
                            entryHasChanged = true;

                        } else {
                            zeroQuantityToast.show();
                        }
                        break;
                }
                quantityEditText.setText(String.valueOf(productQuantity));
            }
        };
        plusButton.setOnClickListener(quantityListener);
        minusButton.setOnClickListener(quantityListener);
    }

    private void saveCustardEntry() {

        String priceString = priceEditText.getText().toString().trim();
        productPrice = Double.parseDouble(priceString);

        String quantityString = quantityEditText.getText().toString().trim();
        productQuantity = Integer.parseInt(quantityString);

        // Create new map of values, where column names are the keys.
        ContentValues values = new ContentValues();
        values.put(CustardEntry.COLUMN_CUSTARD_NAME, productName);
        values.put(CustardEntry.COLUMN_CUSTARD_SIZE, productSize);
        values.put(CustardEntry.COLUMN_CUSTARD_PRICE, productPrice);
        values.put(CustardEntry.COLUMN_CUSTARD_QUANTITY, productQuantity);
        values.put(CustardEntry.COLUMN_CUSTARD_INVENTORY_DATE, inventoryDate);
        values.put(CustardEntry.COLUMN_CUSTARD_SUPPLIER_NAME, supplierName);
        values.put(CustardEntry.COLUMN_CUSTARD_SUPPLIER_PHONE, supplierPhone);

        if (currentCustardUri == null) {

            // Insert new row, returning the new URI value of the new row.
            Uri newUri = getContentResolver().insert(CustardEntry.CONTENT_URI, values);

            // Show toast message depending on whether or not the entry save was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error saving entry.
                Toast.makeText(this, getString(R.string.edit_insert_entry_failed),
                        Toast.LENGTH_SHORT).show();

            } else {
                // else the entry save was successful and show successful toast.
                Toast.makeText(this, getString(R.string.edit_insert_entry_successful),
                        Toast.LENGTH_SHORT).show();
            }

            // For confirmation.
            Log.v(getString(R.string.edit_activity), getString(R.string.new_entry));

        } else {
            // Update row, returning the primary key value of the row.
            int rowsAffected = getContentResolver().update(currentCustardUri, values, null, null);

            // Show toast message depending on whether or not the entry insertion was successful.
            if (rowsAffected == 0) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.edit_update_entry_failed),
                        Toast.LENGTH_SHORT).show();

            } else {
                // else the entry insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.edit_update_entry_successful),
                        Toast.LENGTH_SHORT).show();
            }
            // For confirmation.
            Log.v(getString(R.string.edit_activity), getString(R.string.updated_entry) + rowsAffected);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu options from the res/menu/menu_edit.xml file.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // Respond to a click on the "Save" menu option.
            case R.id.action_save:

                // Extract the contents of all input fields.
                productName = nameEditText.getText().toString().trim();
                String priceString = priceEditText.getText().toString().trim();
                String quantityString = quantityEditText.getText().toString().trim();
                supplierName = supplierNameEditText.getText().toString().trim();
                supplierPhone = supplierPhoneEditText.getText().toString().trim();

                // Display error on any empty input field.
                if (TextUtils.isEmpty(productName)) {
                    nameEditText.setError(getString(R.string.required_field));
                }
                if (TextUtils.isEmpty(priceString)) {
                    priceEditText.setError(getString(R.string.required_field));
                }
                if (TextUtils.isEmpty(quantityString)) {
                    quantityEditText.setError(getString(R.string.required_field));
                }
                if (TextUtils.isEmpty(supplierName)) {
                    supplierNameEditText.setError(getString(R.string.required_field));
                }
                if (TextUtils.isEmpty(supplierPhone)) {
                    supplierPhoneEditText.setError(getString(R.string.required_field));
                }

                // If all input fields are not empty...
                if (!TextUtils.isEmpty(productName) &&
                        !TextUtils.isEmpty(supplierName) &&
                        !TextUtils.isEmpty(supplierPhone) &&
                        !TextUtils.isEmpty(priceString) &&
                        !TextUtils.isEmpty(quantityString)) {

                    // Save entry to the custard inventory table.
                    saveCustardEntry();

                    // Exit activity.
                    finish();

                } else {
                    Toast.makeText(this, getString(R.string.edit_all_input_fields_required),
                            Toast.LENGTH_SHORT).show();
                }

                return true;

            // Respond to a click on the "Close" menu option
            case R.id.action_delete:

                // Pop up confirmation dialog for deletion.
                showDeleteConfirmationDialog();

                // Exit activity.
                return true;

            // Respond to a click on the "Up" arrow button in the app bar.
            case android.R.id.home:

                // If the entry hasn't changed, continue with navigating up to parent activity
                // which is the {@link MainActivity}.
                if (!entryHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);

        // If this is a new custard entry, hide the "Delete" menu item.
        if (currentCustardUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
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
                currentCustardUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // If no rows returned in the cursor, go no further.
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            // Extract out the value from the Cursor for the given column index.
            productName = cursor.getString(cursor.getColumnIndex(CustardEntry.COLUMN_CUSTARD_NAME));
            productSize = cursor.getInt(cursor.getColumnIndex(CustardEntry.COLUMN_CUSTARD_SIZE));
            productPrice = cursor.getDouble(cursor.getColumnIndex(CustardEntry.COLUMN_CUSTARD_PRICE));
            productQuantity = cursor.getInt(cursor.getColumnIndex(CustardEntry.COLUMN_CUSTARD_QUANTITY));
            inventoryDate = cursor.getString(cursor.getColumnIndex(CustardEntry.COLUMN_CUSTARD_INVENTORY_DATE));
            supplierName = cursor.getString(cursor.getColumnIndex(CustardEntry.COLUMN_CUSTARD_SUPPLIER_NAME));
            supplierPhone = cursor.getString(cursor.getColumnIndex(CustardEntry.COLUMN_CUSTARD_SUPPLIER_PHONE));

            // Set decimal format to display US dollars.
            DecimalFormat dollars = new DecimalFormat(getString(R.string.dollar_pattern));

            // Update the Views with current values.
            nameEditText.setText(productName);
            priceEditText.setText(dollars.format(productPrice));
            quantityEditText.setText(String.valueOf(productQuantity));
            inventoryDateTextView.setText(inventoryDate);
            supplierNameEditText.setText(supplierName);
            supplierPhoneEditText.setText(supplierPhone);

            switch (productSize) {
                case CustardEntry.CUSTARD_SIZE_PINT:
                    sizeSpinner.setSelection(0);
                    break;

                case CustardEntry.CUSTARD_SIZE_QUART:
                    sizeSpinner.setSelection(1);
                    break;

                case CustardEntry.CUSTARD_SIZE_HALF_GALLON:
                    sizeSpinner.setSelection(2);
                    break;

                case CustardEntry.CUSTARD_SIZE_GALLON:
                    sizeSpinner.setSelection(3);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // If the loader is invalidated, reset and clear out data from the fields.
        nameEditText.setText(R.string.empty_string);
        sizeSpinner.setSelection(0);
        priceEditText.setText(R.string.empty_string);
        quantityEditText.setText(R.string.empty_string);
        inventoryDateTextView.setText(R.string.empty_string);
        supplierNameEditText.setText(R.string.empty_string);
        supplierPhoneEditText.setText(R.string.empty_string);
    }

    // OnTouchListener that listens for any user Spinner touches, implying that they are modifying
    // the value, and we change the entryHasChanged boolean to true.
    private final OnTouchListener spinnerListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {

            if (v.performClick()) {
                entryHasChanged = true;
            }
            return false;
        }
    };

    // OnClickListener that listens for any user EditText touches, implying that they are modifying
    // the value, and we change the entryHasChanged boolean to true.
    private final OnClickListener editTextListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            entryHasChanged = true;
        }
    };

    private void showUnsavedChangesDialog(

            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the entry.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {

        // If the custard entry hasn't changed, continue with handling back button press.
        if (!entryHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDeleteConfirmationDialog() {

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the entry.
                deleteEntry();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the entry.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Perform the deletion of custard entry in the database.
    private void deleteEntry() {

        // Only perform the delete if this is an existing entry.
        if (currentCustardUri != null) {

            // Call the ContentResolver to delete the entry at the given content URI.
            // Pass in null for the selection and selection args because the currentCustardUri
            // content URI already identifies the entry that we want.
            int rowsDeleted = getContentResolver().delete(currentCustardUri, null, null);

            // Show toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.edit_delete_entry_failed),
                        Toast.LENGTH_SHORT).show();

            } else {
                // Otherwise, the delete was successful and display a toast.
                Toast.makeText(this, getString(R.string.edit_delete_entry_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}