package com.example.android.inventoryapp;

/**
 * Created by lukem on 4/23/2017.
 */

public class List {
    private String mParentId;
    private String mInStockId;
    private String mOutStockId;
    private String mShopStockId;
    private String mListName;
    private int mIndex;

    public List(String mParentId, String mInStockId, String mOutStockId, String mShopStockId, String mListName, int index) {
        this.mParentId = mParentId;
        this.mInStockId = mInStockId;
        this.mOutStockId = mOutStockId;
        this.mShopStockId = mShopStockId;
        this.mListName = mListName;
        this.mIndex = index;
    }

    public int getmIndex() {
        return mIndex;
    }

    public String getmListName() {
        return mListName;
    }

    public String getmShopStockId() {
        return mShopStockId;
    }

    public String getmParentId() {
        return mParentId;
    }

    public String getmInStockId() {
        return mInStockId;
    }

    public String getmOutStockId() {
        return mOutStockId;
    }
}
