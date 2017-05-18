package com.example.android.inventoryapp;

import android.provider.BaseColumns;

/**
 * The InventoryContract class stores the constants used in database operations
 */
public class InventoryContract {

    // Empty constructor in case someone accidentally attempts to instantiate a InventoryContract object
    public InventoryContract() {
    }

    public static final class InventoryEntity implements BaseColumns {
        public static final String IN_STOCK_TABLE_NAME = "inStock";
        public static final String OUT_STOCK_TABLE_NAME = "outStock";
        public static final String SHOP_STOCK_TABLE_NAME = "shopStock";

        // Columns containing Strings in the database
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_UNITS = "unit";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_RESTOCK = "restock";
        public static final String COLUMN_UPC = "barcode";
        public static final String COLUMN_APIID = "appid";


    }
}
