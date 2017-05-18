package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.inventoryapp.InventoryContract.InventoryEntity;

/**
 * Database helper for a database storing inventory information
 */
public class InventoryAppDBHelper extends SQLiteOpenHelper {
    // Database information
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "inventory.db";

    // Context of the database
    private Context mContext;

    // Constructor of the database; stores context for use in deletion of entire database
    public InventoryAppDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, DATABASE_VERSION);
        mContext = context;
    }

    // Create the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_INSTOCK);
        db.execSQL(SQL_CREATE_ENTRIES_OUTSTOCK);
        db.execSQL(SQL_CREATE_ENTRIES_SHOPSTOCK);
    }

    // Drop the old table when database is upgraded, then re-instantiate a new database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_INSTOCK);
        db.execSQL(SQL_DELETE_ENTRIES_OUTSTOCK);
        db.execSQL(SQL_DELETE_ENTRIES_SHOPSTOCK);

        onCreate(db);
    }

    // SQL used to create the database
    private static final String SQL_CREATE_ENTRIES_INSTOCK = "CREATE TABLE " +
            InventoryEntity.IN_STOCK_TABLE_NAME + "(" +
            InventoryEntity._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            InventoryEntity.COLUMN_NAME + " TEXT, " +
            InventoryEntity.COLUMN_UPC + " TEXT, " +
            InventoryEntity.COLUMN_IMAGE + " BLOB, " +
            InventoryEntity.COLUMN_DESCRIPTION + " STRING, " +
            InventoryEntity.COLUMN_RESTOCK + " INTEGER, " +
            InventoryEntity.COLUMN_QUANTITY + " INTEGER, " +
            InventoryEntity.COLUMN_UNITS + " TEXT, " +
            InventoryEntity.COLUMN_APIID + " TEXT " +
            ");";

        private static final String SQL_CREATE_ENTRIES_OUTSTOCK = "CREATE TABLE " +
                InventoryEntity.OUT_STOCK_TABLE_NAME + "(" +
                InventoryEntity._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                InventoryEntity.COLUMN_NAME + " TEXT, " +
                InventoryEntity.COLUMN_UPC + " TEXT, " +
                InventoryEntity.COLUMN_IMAGE + " BLOB, " +
                InventoryEntity.COLUMN_DESCRIPTION + " STRING, " +
                InventoryEntity.COLUMN_RESTOCK + " INTEGER, " +
                InventoryEntity.COLUMN_QUANTITY + " INTEGER, " +
                InventoryEntity.COLUMN_UNITS + " TEXT, " +
                InventoryEntity.COLUMN_APIID + " TEXT " +
                ");";

        private static final String SQL_CREATE_ENTRIES_SHOPSTOCK= "CREATE TABLE " +
                InventoryEntity.SHOP_STOCK_TABLE_NAME + "(" +
                InventoryEntity._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                InventoryEntity.COLUMN_NAME + " TEXT, " +
                InventoryEntity.COLUMN_UPC + " TEXT, " +
                InventoryEntity.COLUMN_IMAGE + " BLOB, " +
                InventoryEntity.COLUMN_DESCRIPTION + " STRING, " +
                InventoryEntity.COLUMN_RESTOCK + " INTEGER, " +
                InventoryEntity.COLUMN_QUANTITY + " INTEGER, " +
                InventoryEntity.COLUMN_UNITS + " TEXT, " +
                InventoryEntity.COLUMN_APIID + " TEXT " +
                ");";

    // SQL used to drop the table
    private static final String SQL_DELETE_ENTRIES_INSTOCK =
            "DROP TABLE IF EXISTS " + InventoryEntity.IN_STOCK_TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES_OUTSTOCK =
            "DROP TABLE IF EXISTS " + InventoryEntity.OUT_STOCK_TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES_SHOPSTOCK =
            "DROP TABLE IF EXISTS " + InventoryEntity.SHOP_STOCK_TABLE_NAME;


    // readProduct takes a String productName that is the productName and obtains a cursor to the row for
    // which the NAME column equals productName. The cursor reference to all column elements
    public Cursor readProduct(int key, char table) {
        SQLiteDatabase db = getReadableDatabase();
        String query;

        switch (table){
            case 'i':
                query = "SELECT * FROM " + InventoryEntity.IN_STOCK_TABLE_NAME + " WHERE "
                        + InventoryEntity._ID + "=\"" + key + "\";";
                break;
            case 'o':
                query = "SELECT * FROM " + InventoryEntity.OUT_STOCK_TABLE_NAME + " WHERE "
                        + InventoryEntity._ID + "=\"" + key + "\";";
                break;
            case 's':
                query = "SELECT * FROM " + InventoryEntity.SHOP_STOCK_TABLE_NAME + " WHERE "
                    + InventoryEntity._ID + "=\"" + key + "\";";
                break;
            default:
                query = "";
                break;
        }

        return db.rawQuery(query, null);
    }

    // readProduct takes a String productName that is the productName and obtains a cursor to the row for
    // which the NAME column equals productName. The cursor reference to all column elements
    public Cursor readProductFromAPIKey(String apiKey, char table) {
        SQLiteDatabase db = getReadableDatabase();
        String query;

        switch (table){
            case 'i':
                query = "SELECT * FROM " + InventoryEntity.IN_STOCK_TABLE_NAME + " WHERE "
                        + InventoryEntity.COLUMN_APIID + "=\"" + apiKey + "\";";
                break;
            case 'o':
                query = "SELECT * FROM " + InventoryEntity.OUT_STOCK_TABLE_NAME + " WHERE "
                        + InventoryEntity.COLUMN_APIID + "=\"" + apiKey + "\";";
                break;
            case 's':
                query = "SELECT * FROM " + InventoryEntity.SHOP_STOCK_TABLE_NAME + " WHERE "
                        + InventoryEntity.COLUMN_APIID + "=\"" + apiKey + "\";";
                break;
            default:
                query = "";
                break;
        }

        return db.rawQuery(query, null);
    }
    // getQuantity gets the quantity stored of the product given by productName
    public int getQuantity(int key, char table) {
        SQLiteDatabase db = getReadableDatabase();

        String query;

        switch (table){
            case 'i':
                query = "SELECT " + InventoryEntity.COLUMN_QUANTITY + " FROM "
                        + InventoryEntity.IN_STOCK_TABLE_NAME + " WHERE " + InventoryEntity._ID
                        + "=\"" + key + "\";";
                break;
            case 'o':
                query = "SELECT " + InventoryEntity.COLUMN_QUANTITY + " FROM "
                        + InventoryEntity.OUT_STOCK_TABLE_NAME + " WHERE " + InventoryEntity._ID
                        + "=\"" + key + "\";";
                break;
            case 's':
                query = "SELECT " + InventoryEntity.COLUMN_QUANTITY + " FROM "
                    + InventoryEntity.SHOP_STOCK_TABLE_NAME + " WHERE " + InventoryEntity._ID
                    + "=\"" + key + "\";";
                break;
            default:
                query = "";
                break;
        }
        Cursor iter = db.rawQuery(query, null);
        iter.moveToFirst();
        if(!iter.isAfterLast()) {
            int quantId = iter.getColumnIndex(InventoryEntity.COLUMN_QUANTITY);
            return iter.getInt(quantId);
        } else {
            return -1;
        }
    }

    // deleteDatabase deletes the database in its entirety and returns a boolean indicating its
    // success
    public boolean deleteInventoryDatabase() {
        return mContext.deleteDatabase(DATABASE_NAME);
    }

    // addProduct adds a new row to database for the given product.
    public void addProduct(Product product, byte[] image, char table) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InventoryEntity.COLUMN_NAME, product.getmName());
        values.put(InventoryEntity.COLUMN_UPC, product.getmUPC());
        values.put(InventoryEntity.COLUMN_DESCRIPTION, product.getmDescription());
        values.put(InventoryEntity.COLUMN_RESTOCK, product.getmUPC());
        values.put(InventoryEntity.COLUMN_IMAGE, image);
        values.put(InventoryEntity.COLUMN_QUANTITY, product.getmQuantity());
        values.put(InventoryEntity.COLUMN_UNITS, product.getmUnits());
        values.put(InventoryEntity.COLUMN_APIID, product.getmApiId());

        switch (table){
            case 'i':
                db.insert(InventoryEntity.IN_STOCK_TABLE_NAME, null, values);
                break;
            case 'o':
                db.insert(InventoryEntity.OUT_STOCK_TABLE_NAME, null, values);
                break;
            case 's':
                db.insert(InventoryEntity.SHOP_STOCK_TABLE_NAME, null, values);
                break;
            default:
                break;
        }
        db.close();
    }

    // addProduct adds a new row to database for the given product.
    public void addProduct(String product, String barcode, byte[] image, int reorder, int quantity,
                           String unit, String description, char table, String apiId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InventoryEntity.COLUMN_NAME, product);
        values.put(InventoryEntity.COLUMN_UPC, barcode);
        values.put(InventoryEntity.COLUMN_RESTOCK, reorder);
        values.put(InventoryEntity.COLUMN_DESCRIPTION, description);
        values.put(InventoryEntity.COLUMN_IMAGE, image);
        values.put(InventoryEntity.COLUMN_QUANTITY, quantity);
        values.put(InventoryEntity.COLUMN_UNITS, unit);
        values.put(InventoryEntity.COLUMN_APIID, apiId);

        switch (table){
            case 'i':
                db.insert(InventoryEntity.IN_STOCK_TABLE_NAME, null, values);
                break;
            case 'o':
                db.insert(InventoryEntity.OUT_STOCK_TABLE_NAME, null, values);
                break;
            case 's':
                db.insert(InventoryEntity.SHOP_STOCK_TABLE_NAME, null, values);
                break;
            default:
                break;
        }
        db.close();
    }

    // deleteProduct deletes a given product (represented by its argument productName) from the database
    public void deleteProduct(int key, char table) {
        SQLiteDatabase db = getWritableDatabase();
        switch (table){
            case 'i':
                db.execSQL("DELETE FROM " + InventoryEntity.IN_STOCK_TABLE_NAME + " WHERE " +
                        InventoryEntity._ID + "=\"" + key + "\";");
                break;
            case 'o':
                db.execSQL("DELETE FROM " + InventoryEntity.OUT_STOCK_TABLE_NAME + " WHERE " +
                        InventoryEntity._ID + "=\"" + key + "\";");
                break;
            case 's':
                db.execSQL("DELETE FROM " + InventoryEntity.SHOP_STOCK_TABLE_NAME + " WHERE " +
                        InventoryEntity._ID + "=\"" + key + "\";");
                break;
        }
    }

    public void deleteInventoryTable(char table){
        SQLiteDatabase db = getWritableDatabase();
        switch (table){
            case 'i':
                db.execSQL("DELETE FROM " + InventoryEntity.IN_STOCK_TABLE_NAME);
                break;
            case 'o':
                db.execSQL("DELETE FROM " + InventoryEntity.OUT_STOCK_TABLE_NAME);
                break;
            case 's':
                db.execSQL("DELETE FROM " + InventoryEntity.SHOP_STOCK_TABLE_NAME);
                break;
        }
        Log.v("DELETE", "entries from database");
    }

    public void deleteNullImages(char table){
        SQLiteDatabase db = getWritableDatabase();
        switch (table){
            case 'i':
                db.execSQL("DELETE FROM " + InventoryEntity.IN_STOCK_TABLE_NAME + " WHERE " +
                        InventoryEntity.COLUMN_IMAGE + " IS \"" + null + "\";");
                break;
            case 'o':
                db.execSQL("DELETE FROM " + InventoryEntity.OUT_STOCK_TABLE_NAME + " WHERE " +
                        InventoryEntity.COLUMN_IMAGE + " IS \"" + null + "\";");
                break;
            case 's':
                db.execSQL("DELETE FROM " + InventoryEntity.SHOP_STOCK_TABLE_NAME + " WHERE " +
                        InventoryEntity.COLUMN_IMAGE + " IS \"" + null + "\";");
                break;
        }
        Log.v("DELETE", "entries from database");
    }

    public void updateProduct(String apiId, Product product, char table){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        if(product.getmImage() != null) {
            values.put(InventoryEntity.COLUMN_IMAGE, BitMapUtility.getBytes(product.getmImage()));
        }
        values.put(InventoryEntity.COLUMN_NAME, product.getmName());
        values.put(InventoryEntity.COLUMN_QUANTITY, product.getmQuantity());
        values.put(InventoryEntity.COLUMN_DESCRIPTION, product.getmDescription());
        values.put(InventoryEntity.COLUMN_UNITS, product.getmUnits());
        values.put(InventoryEntity.COLUMN_UPC, product.getmUPC());

        String whereClause;
        whereClause = InventoryEntity.COLUMN_APIID + "=\"" + apiId + "\";";

        switch (table) {
            case 'i':
                db.update(InventoryEntity.IN_STOCK_TABLE_NAME,
                        values,
                        whereClause,
                        null);
                break;
            case 'o':
                db.update(InventoryEntity.OUT_STOCK_TABLE_NAME,
                        values,
                        whereClause,
                        null);
                break;
            case 's':
                db.update(InventoryEntity.SHOP_STOCK_TABLE_NAME,
                        values,
                        whereClause,
                        null);
                break;
        }
    }

    // updateProductQuantity takes a String representing a product and an integer representing the new number of
    //repetitions for the product. updateProductQuantity updates the repetitions column of the row for productName
    public void updateProductQuantity(int key, int newQuantity, char table) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InventoryEntity.COLUMN_QUANTITY, newQuantity);
        String whereClause = InventoryEntity._ID + "=\"" + key + "\";";

        switch (table){
            case 'i':
                db.update(InventoryEntity.IN_STOCK_TABLE_NAME,
                        values,
                        whereClause,
                        null);
                break;
            case 'o':
                db.update(InventoryEntity.OUT_STOCK_TABLE_NAME,
                        values,
                        whereClause,
                        null);
                break;
            case 's':
                db.update(InventoryEntity.SHOP_STOCK_TABLE_NAME,
                        values,
                        whereClause,
                        null);
                break;
            default:
                return;
        }
    }

    public void updateApiId(int key, String apiId, char table){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InventoryEntity.COLUMN_APIID, apiId);

        String whereClause;
        whereClause = InventoryEntity._ID + "=\"" + key + "\";";

        switch (table) {
            case 'i':
                db.update(InventoryEntity.IN_STOCK_TABLE_NAME,
                        values,
                        whereClause,
                        null);
                break;
            case 'o':
                db.update(InventoryEntity.OUT_STOCK_TABLE_NAME,
                        values,
                        whereClause,
                        null);
                break;
            case 's':
                db.update(InventoryEntity.SHOP_STOCK_TABLE_NAME,
                        values,
                        whereClause,
                        null);
                break;
        }
    }

    public void updateProductValues(String productName, String upc, byte[] image, int restock,
                                    int quantity, String units, String description, char table,
                                    int key) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        Log.v("HELPER:", "Updating product");

        if(image != null) {
            values.put(InventoryEntity.COLUMN_IMAGE, image);
        }
        values.put(InventoryEntity.COLUMN_NAME, productName);
        values.put(InventoryEntity.COLUMN_QUANTITY, quantity);
        values.put(InventoryEntity.COLUMN_DESCRIPTION, description);
        values.put(InventoryEntity.COLUMN_UNITS, units);
        values.put(InventoryEntity.COLUMN_UPC, upc);

        String whereClause;
        whereClause = InventoryEntity._ID + "=\"" + key + "\";";

        switch (table) {
            case 'i':
                db.update(InventoryEntity.IN_STOCK_TABLE_NAME,
                        values,
                        whereClause,
                        null);
                break;
            case 'o':
                db.update(InventoryEntity.OUT_STOCK_TABLE_NAME,
                        values,
                        whereClause,
                        null);
                break;
            case 's':
                db.update(InventoryEntity.SHOP_STOCK_TABLE_NAME,
                        values,
                        whereClause,
                        null);
                break;
        }
    }

    public void updateCreateItem(Product product, char table){
        Cursor cursor = readProductFromAPIKey(product.getmApiId(), table);
        if(!cursor.moveToFirst() || cursor.getCount() == 0){
                addProduct(product, null, table);

        } else {
            /*
            String apiIdkey = product.getmApiId();
            int apiCol = cursor.getColumnIndex(InventoryContract.InventoryEntity.COLUMN_APIID);
            if(apiIdkey.equals(cursor.getString(apiCol))){
            */
                updateProduct(product.getmApiId(), product, table);
            /*
            } else {
                addProduct(product.getmApiId(), product, table);
            }
            */
        }
    }

    // Display data displays the entire database in a textView
    public Cursor getCursorToDb(char table) {
        String query;
        SQLiteDatabase db = getReadableDatabase();
        switch (table){
            case 'i':
                query = "SELECT * FROM " + InventoryEntity.IN_STOCK_TABLE_NAME + " WHERE 1";
                break;
            case 'o':
                query = "SELECT * FROM " + InventoryEntity.OUT_STOCK_TABLE_NAME + " WHERE 1";
                break;
            case 's':
                query = "SELECT * FROM " + InventoryEntity.SHOP_STOCK_TABLE_NAME + " WHERE 1";
                break;
            default:
                query = "";
                break;
        }
        return db.rawQuery(query, null);
    }

    public Product getProduct(int key, char table){
        Product product = null;
        Cursor iter = this.readProduct(key, table);
        try {
            iter.moveToFirst();
            int nameCol = iter.getColumnIndex(InventoryContract.InventoryEntity.COLUMN_NAME);
            int imageCol = iter.getColumnIndex(InventoryContract.InventoryEntity.COLUMN_IMAGE);
            int quantityCol = iter.getColumnIndex(InventoryContract.InventoryEntity.COLUMN_QUANTITY);
            int unitCol = iter.getColumnIndex(InventoryContract.InventoryEntity.COLUMN_UNITS);
            int upcCol = iter.getColumnIndex(InventoryContract.InventoryEntity.COLUMN_UPC);
            int restockCol = iter.getColumnIndex(InventoryContract.InventoryEntity.COLUMN_RESTOCK);
            int descCol = iter.getColumnIndex(InventoryContract.InventoryEntity.COLUMN_DESCRIPTION);
            int apiCol = iter.getColumnIndex(InventoryContract.InventoryEntity.COLUMN_APIID);

            product = new Product(
                    iter.getString(nameCol),
                    iter.getString(unitCol),
                    iter.getString(upcCol),
                    iter.getInt(restockCol),
                    iter.getInt(quantityCol),
                    BitMapUtility.getImage(iter.getBlob(imageCol)),
                    iter.getString(descCol),
                    key,
                    iter.getString(apiCol)
            );
        } finally {
            iter.close();
        }
        return product;
    }
}
