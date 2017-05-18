package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lukem on 4/23/2017.
 */

public class ListDBHelper extends SQLiteOpenHelper {

    /**
     * Database helper for a database storing list information
     */

    // Database information
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "list.db";

    // Context of the database
    private Context mContext;

    // Constructor of the database; stores context for use in deletion of entire database
    public ListDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, DATABASE_VERSION);
        mContext = context;
    }

    // Create the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_LIST);
    }

    // Drop the old table when database is upgraded, then re-instantiate a new database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_LIST_TABLE);

        onCreate(db);
    }

    // SQL used to create the database
    private static final String SQL_CREATE_ENTRIES_LIST = "CREATE TABLE " +
            ListContract.ListInventoryEntity.LIST_TABLE_NAME + "(" +
            ListContract.ListInventoryEntity._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ListContract.ListInventoryEntity.COLUMN_PARENTID + " TEXT, " +
            ListContract.ListInventoryEntity.COLUMN_INSTOCKID + " TEXT, " +
            ListContract.ListInventoryEntity.COLUMN_SHOPSTOCKID + " TEXT, " +
            ListContract.ListInventoryEntity.COLUMN_OUTSTOCKID + " TEXT, " +
            ListContract.ListInventoryEntity.COLUMN_LISTINDEX + " INTEGER, " +
            ListContract.ListInventoryEntity.COLUMN_LISTNAME + " TEXT " +
            ");";

    // SQL used to drop the table
    private static final String SQL_DELETE_ENTRIES_LIST_TABLE =
            "DROP TABLE IF EXISTS " + ListContract.ListInventoryEntity.LIST_TABLE_NAME;

    // readProduct takes a String productName that is the productName and obtains a cursor to the row for
    // which the NAME column equals productName. The cursor reference to all column elements
    public Cursor readList(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String query;
        Log.v("readlist", "reading list");

        query = "SELECT * FROM " + ListContract.ListInventoryEntity.LIST_TABLE_NAME + " WHERE "
                + ListContract.ListInventoryEntity.COLUMN_LISTNAME + "=\"" + name + "\";";

        Log.v("readlist", "done");
        return db.rawQuery(query, null);
    }

    // deleteDatabase deletes the database in its entirety and returns a boolean indicating its
    // success
    public boolean deleteListDatabase() {
        return mContext.deleteDatabase(DATABASE_NAME);
    }

    // addProduct adds a new row to database for the given product.
    public void addList(List list) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ListContract.ListInventoryEntity.COLUMN_INSTOCKID, list.getmInStockId());
        values.put(ListContract.ListInventoryEntity.COLUMN_OUTSTOCKID, list.getmOutStockId());
        values.put(ListContract.ListInventoryEntity.COLUMN_SHOPSTOCKID, list.getmShopStockId());
        values.put(ListContract.ListInventoryEntity.COLUMN_PARENTID, list.getmParentId());
        values.put(ListContract.ListInventoryEntity.COLUMN_LISTINDEX, list.getmIndex());
        values.put(ListContract.ListInventoryEntity.COLUMN_LISTNAME, list.getmListName());

        db.insert(ListContract.ListInventoryEntity.LIST_TABLE_NAME, null, values);
        db.close();
    }

    // addProduct adds a new row to database for the given product.
    public void addList(String parentId, String instockId, String outstockId, String shopstockId,
                        int index, String name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ListContract.ListInventoryEntity.COLUMN_INSTOCKID, instockId);
        values.put(ListContract.ListInventoryEntity.COLUMN_OUTSTOCKID, outstockId);
        values.put(ListContract.ListInventoryEntity.COLUMN_SHOPSTOCKID, shopstockId);
        values.put(ListContract.ListInventoryEntity.COLUMN_PARENTID, parentId);
        values.put(ListContract.ListInventoryEntity.COLUMN_LISTINDEX, index);
        values.put(ListContract.ListInventoryEntity.COLUMN_LISTNAME, name);

        db.insert(ListContract.ListInventoryEntity.LIST_TABLE_NAME, null, values);
        db.close();
    }

    // deleteProduct deletes a given product (represented by its argument productName) from the database
    public void deleteList(int key) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + ListContract.ListInventoryEntity.LIST_TABLE_NAME + " WHERE " +
                ListContract.ListInventoryEntity._ID + "=\"" + key + "\";");
    }

    // Display data displays the entire database in a textView
    public Cursor getCursorToDb(char table) {
        String query;
        SQLiteDatabase db = getReadableDatabase();
        query = "SELECT * FROM " + ListContract.ListInventoryEntity.LIST_TABLE_NAME + " WHERE 1";
        return db.rawQuery(query, null);
    }

    public List getList(String name) {
        List list = null;
        Cursor iter = this.readList(name);
        try {
            iter.moveToFirst();
            int pIdCol = iter.getColumnIndex(ListContract.ListInventoryEntity.COLUMN_PARENTID);
            int iIdCol = iter.getColumnIndex(ListContract.ListInventoryEntity.COLUMN_INSTOCKID);
            int oIdCol = iter.getColumnIndex(ListContract.ListInventoryEntity.COLUMN_OUTSTOCKID);
            int sIdCol = iter.getColumnIndex(ListContract.ListInventoryEntity.COLUMN_SHOPSTOCKID);
            int indexCol = iter.getColumnIndex(ListContract.ListInventoryEntity.COLUMN_LISTINDEX);
            int nameCol = iter.getColumnIndex(ListContract.ListInventoryEntity.COLUMN_LISTNAME);

            // String mParentId, String mInStockId, String mOutStockId, String mShopStockId, String mListName) {
            list = new List(
                    iter.getString(pIdCol),
                    iter.getString(iIdCol),
                    iter.getString(oIdCol),
                    iter.getString(sIdCol),
                    iter.getString(nameCol),
                    iter.getInt(indexCol)
            );
        } finally {
            iter.close();
        }
        Log.v("getList", "reutrning list");
        return list;
    }
}
