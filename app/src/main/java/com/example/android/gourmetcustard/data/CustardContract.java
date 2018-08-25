package com.example.android.gourmetcustard.data;

/*
 * Grow With Google Challenge Scholarship: Android Basics
 * Project: 9
 * Version: 3.0
 * App Name: Gourmet Custard
 * Author: Joseph McDonald
 */

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class CustardContract {

    // To prevent accidental instantiation of the contract class, make the constructor private.
    private CustardContract() {}

    // CONTENT AUTHORITY CONSTANT.
    public static final String CONTENT_AUTHORITY = "com.example.android.gourmetcustard";

    // BASE CONTENT URI CONSTANT.
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // PATH Table Name CONSTANT.
    public static final String PATH_CUSTARD = "custard_inventory";

    public static final class CustardEntry implements BaseColumns {

        // COMPLETE CONTENT URI CONSTANT.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CUSTARD);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of custard.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CUSTARD;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single custard.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CUSTARD;

        // Custard inventory table name.
        public static final String TABLE_NAME = "custard_inventory";

        // Custard inventory table columns.
        //public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_CUSTARD_NAME = "product_name";
        public static final String COLUMN_CUSTARD_SIZE = "product_size";
        public static final String COLUMN_CUSTARD_PRICE = "product_price";
        public static final String COLUMN_CUSTARD_QUANTITY = "product_quantity";
        public static final String COLUMN_CUSTARD_INVENTORY_DATE = "inventory_date";
        public static final String COLUMN_CUSTARD_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_CUSTARD_SUPPLIER_PHONE = "supplier_phone";

        // Supplier KOPP'S product name CONSTANTS.
        public static final String CUSTARD_MINT_CHIP = "Mint Chip";
        public static final String CUSTARD_TIRAMISU = "Tiramisu";
        public static final String CUSTARD_HOG_HEAVEN = "Hog Heaven";
        public static final String CUSTARD_CHERRY_AMARETTO_CHEESECAKE = "Cherry Amaretto Cheesecake";

        // Supplier OSCAR'S product name CONSTANTS.
        public static final String CUSTARD_CARAMEL_CASHEW = "Caramel Cashew";
        public static final String CUSTARD_BLUE_MOON = "Blue Moon";
        public static final String CUSTARD_BADGER_CLAW = "Badger Claw";
        public static final String CUSTARD_PISTACHIO = "Pistachio";

        // Supplier GILLES product name CONSTANTS.
        public static final String CUSTARD_SMORES = "S'mores";
        public static final String CUSTARD_ROOT_BEER_FLOAT = "Root Beer Float";
        public static final String CUSTARD_CHOCOLATE_ALMOND = "Chocolate Almond";
        public static final String CUSTARD_BANANA_PARFAIT = "Banana Parfait";

        // Custard product size CONSTANTS.
        public static final int CUSTARD_SIZE_PINT = 0;
        public static final int CUSTARD_SIZE_QUART = 1;
        public static final int CUSTARD_SIZE_HALF_GALLON = 2;
        public static final int CUSTARD_SIZE_GALLON = 3;

        // Custard product price CONSTANTS.
        public static final double CUSTARD_PRICE_PINT = 5.00;
        public static final double CUSTARD_PRICE_QUART = 9.00;
        public static final double CUSTARD_PRICE_HALF_GALLON = 16.00;
        public static final double CUSTARD_PRICE_GALLON = 30.00;

        // Supplier name CONSTANTS.
        public static final String SUPPLIER_NAME_KOPPS = "Kopp's Frozen Custard";
        public static final String SUPPLIER_NAME_OSCARS = "Oscar's Frozen Custard";
        public static final String SUPPLIER_NAME_GILLES = "Gilles Frozen Custard";

        // Supplier phone number CONSTANTS.
        public static final String SUPPLIER_PHONE_KOPPS = "262-789-9490";
        public static final String SUPPLIER_PHONE_OSCARS = "262-798-9707";
        public static final String SUPPLIER_PHONE_GILLES = "414-453-4875";
    }
}