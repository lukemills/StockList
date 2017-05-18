package com.example.android.inventoryapp;

import android.provider.BaseColumns;

/**
 * Created by lukem on 4/23/2017.
 */

public class ListContract {

    // Empty constructor in case someone accidentally attempts to instantiate a ListContract object
    public ListContract() {
    }

    public static final class ListInventoryEntity implements BaseColumns {
        public static final String LIST_TABLE_NAME = "lists";

        // Columns containing Strings in the database
        public static final String COLUMN_PARENTID = "parentid";
        public static final String COLUMN_INSTOCKID = "instockid";
        public static final String COLUMN_OUTSTOCKID = "outstockid";
        public static final String COLUMN_SHOPSTOCKID = "shopstockid";
        public static final String COLUMN_LISTNAME = "listname";
        public static final String COLUMN_LISTINDEX = "listindex";
    }
}
