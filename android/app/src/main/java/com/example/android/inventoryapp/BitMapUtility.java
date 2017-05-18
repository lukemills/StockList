package com.example.android.inventoryapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Helper function to convert byte arrays to Bitmap for getting and setting images from database
 */
public class BitMapUtility {

    // Convert a bitmap to a byte array
    public static byte[] getBytes(Bitmap bitmap) {
        if(bitmap == null){
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // Convert a byte array to a bitmap
    public static Bitmap getImage(byte[] image) {
        if(image == null){
            return null;
        }
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}