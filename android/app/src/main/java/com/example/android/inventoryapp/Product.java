package com.example.android.inventoryapp;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Product class stores state information for each product
 */
public class Product {

    private int mKey;

    // The product of the Product
    private String mName;

    // The unit of the product
    private String mUnits;

    // The quantity at which a restock reminder will be sent
    private int mRestock;

    // Image resource URL for the Product
    private Bitmap mImage;

    // The barcode of the product
    private String mUPC;

    // The current quantity of the Product
    private int mQuantity;

    private String mDescription;

    private String mApiId;

    // Constant value that represents no image was provided for this word
    private static final String NO_IMAGE_PROVIDED = "";

    /**
     * Constructor to create a new instance of Product object with associated image
     *
     * @param productName - the product of the Product
     * @param upc         - the upc of the product
     * @param restock     - the quantity at which a restock reminder will be sent
     * @param quantity    - the quantity of the Product
     * @param image       is the bitmap for the image associated with the Product
     */
    public Product(String productName, String units, String upc, int restock,
                   int quantity, Bitmap image, String desc, int key, String apiId) {
        mName = productName;
        mUnits = units;
        mUPC = upc;
        mRestock = restock;
        mImage = image;
        mQuantity = quantity;
        mDescription = desc;
        mKey = key;
        mApiId = apiId;
    }


    // Getter method for the name of the product
    public String getmName() {
        return mName;
    }

    // Getter method for the restock quantity
    public int getmRestock() {
        return mRestock;
    }

    // Getter method for the barcode
    public String getmUPC() {
        return mUPC;
    }

    // Getter method for the iamgeURL
    public Bitmap getmImage() {
        return mImage;
    }

    // Getter method for the iamgeURL
    public String getmUnits() {
        return mUnits;
    }

    // Getter method for the quantity
    public int getmQuantity() {
        return mQuantity;
    }

    public int getmKey() {
        return mKey;
    }

    // The decrementQuantityByOne function decrements mQuantity by one and returns the new quantity
    public int decrementQuantityByOne() {
        if (mQuantity > 0) {
            return --mQuantity;
        } else return mQuantity;
    }

    // Getter method for the String of quantity
    public String getStringmQuantity() {
        return Integer.toString(mQuantity);
    }

    // Setter method for the quantity
    public void setmQuantity(int mQuantity) {
        this.mQuantity = mQuantity;
    }


    public void incrementQuantity(int incrementBy) {
        mQuantity += incrementBy;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmApiId() {
        return mApiId;
    }

    public String buildSetQuantityJSON() {
        JSONArray opJSON = new JSONArray();
        JSONObject productJSON = new JSONObject();
        try {
            productJSON.put("op", "set");
            productJSON.put("quantity", Integer.toString(getmQuantity()));
            productJSON.put("name", getmName());
            productJSON.put("unit_of_measure", getmUnits());
            productJSON.put("upc", getmUPC());
            productJSON.put("description", getmDescription());
            opJSON.put(productJSON);
        } catch (JSONException e) {
            Log.v("buildProductJSON()", "Could not build product JSON", e);
            return null;
        }
        Log.v("buildSetQuantityJSON", "Final string " + opJSON.toString());
        return opJSON.toString();
    }

    public String buildSetAllJSON(String childId, StockFragment.updateAction action) {
        JSONArray opJSON = new JSONArray();
        JSONObject productJSON = new JSONObject();
        try {
            if(action == StockFragment.updateAction.update){
                    productJSON.put("op", "set");
            }
            productJSON.put("name", getmName());
            productJSON.put("unit_of_measure", getmUnits());
            if (getmUPC() == null || getmUPC().equals("null") || getmUPC().isEmpty()) {
                productJSON.put("upc", "null");
            } else {
                productJSON.put("upc", getmUPC());
            }
            if (getmDescription() == null || getmDescription().equals("null") || getmDescription().isEmpty()) {
                productJSON.put("description", "null");
            } else {
                productJSON.put("description", getmDescription());
            }
            if (!childId.isEmpty()) {
                productJSON.put("child_id", childId);
            }
            productJSON.put("quantity", Integer.toString(getmQuantity()));
            productJSON.put("image", "none.jpeg");
            productJSON.put("brand", "generic");
            opJSON.put(productJSON);
        } catch (JSONException e) {
            Log.v("buildProductJSON()", "Could not build product JSON", e);
            return null;
        }
        return opJSON.toString();
    }

    public void setmApiId(String mApiId) {
        this.mApiId = mApiId;
    }
}
