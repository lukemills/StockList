package com.example.android.inventoryapp;

import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by lukem on 4/21/2017.
 */

public class Utils {

    public void putListExtrasInIntentBundle(Intent intent, ArrayList<String> listNames){
        if(listNames.get(0).isEmpty()){ listNames.set(0, "l1"); }
        if(listNames.get(1).isEmpty()){ listNames.set(1, "l2"); }
        if(listNames.get(2).isEmpty()){ listNames.set(2, "l3"); }
        if(listNames.get(3).isEmpty()){ listNames.set(3, "l4"); }
        intent.putExtra("list1", listNames.get(0));
        intent.putExtra("list2", listNames.get(1));
        intent.putExtra("list3", listNames.get(2));
        intent.putExtra("list4", listNames.get(3));
    }


    public URL makeUrl(String urlString) {
        URL res_url;
        try {
            res_url = new URL(urlString);
        } catch (MalformedURLException exc) {
            Log.e("makeUrl", "Couldn't make url from string", exc);
            return null;
        }
        return res_url;
    }

    /**
     * Convert InputStream into a String with JSON response from HTTP Request.
     */
    public String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
