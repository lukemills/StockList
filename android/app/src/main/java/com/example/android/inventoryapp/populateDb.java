package com.example.android.inventoryapp;

import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by lukem on 3/2/2017.
 */

public class populateDb {
    public ArrayList<Product> populateListFromDb(InventoryAppDBHelper dbHelper, char table) {
        Cursor iter = dbHelper.getCursorToDb(table);
        ArrayList<Product> productList = new ArrayList<>();
        try {
            iter.moveToFirst();
            while (!iter.isAfterLast()) {
                int nameCol = iter.getColumnIndex(InventoryContract.InventoryEntity.COLUMN_NAME);
                int unitCol = iter.getColumnIndex(InventoryContract.InventoryEntity.COLUMN_UNITS);
                int restockCol = iter.getColumnIndex(InventoryContract.InventoryEntity.COLUMN_RESTOCK);
                int barcodeCol = iter.getColumnIndex(InventoryContract.InventoryEntity.COLUMN_UPC);
                int imageCol = iter.getColumnIndex(InventoryContract.InventoryEntity.COLUMN_IMAGE);
                int quantityCol = iter.getColumnIndex(InventoryContract.InventoryEntity.COLUMN_QUANTITY);
                int descCol = iter.getColumnIndex(InventoryContract.InventoryEntity.COLUMN_DESCRIPTION);
                int key = iter.getColumnIndex(InventoryContract.InventoryEntity._ID);
                int apiIdCol = iter.getColumnIndex(InventoryContract.InventoryEntity.COLUMN_APIID);
                productList.add(new Product(
                                iter.getString(nameCol),
                                iter.getString(unitCol),
                                iter.getString(barcodeCol),
                                iter.getInt(restockCol),
                                iter.getInt(quantityCol),
                                BitMapUtility.getImage(iter.getBlob(imageCol)),
                                iter.getString(descCol),
                                iter.getInt(key),
                                iter.getString(apiIdCol)
                        )
                );
                iter.moveToNext();
            }
        } finally {
            iter.close();
            dbHelper.close();
        }
        return productList;
    }
}
