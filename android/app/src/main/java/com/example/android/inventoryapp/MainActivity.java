package com.example.android.inventoryapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String LISTDBNAME = "listDatabase";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    String REQ_URL = "http://hlr710vm.homelinux.com:5000/api/user/5900f89820dcb5429ba43f36";

    ArrayList<String> listNames;

    private TextView list1TextView;
    private TextView list2TextView;
    private TextView list3TextView;
    private TextView list4TextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        URL req_url;
        ListAsyncTask task;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        list1TextView = (TextView) findViewById(R.id.list1);
        list2TextView = (TextView) findViewById(R.id.list2);
        list3TextView = (TextView) findViewById(R.id.list3);
        list4TextView = (TextView) findViewById(R.id.list4);

        list1TextView.setText(R.string.list1);
        list2TextView.setText(R.string.list2);
        list3TextView.setText(R.string.list3);
        list4TextView.setText(R.string.list4);

        listNames = new ArrayList<>();


        // Update the products database
        task = new ListAsyncTask(MainActivity.this);
        req_url = new Utils().makeUrl(REQ_URL);
        task.execute(req_url);
        try {
            task.get();
        } catch (Exception e){
            Log.v("MainActivity", "Failed to wait on asynctask");
        }
        Log.v("MainActivity", "Done with asynctask");
        if(listNames.size() == 0){
            listNames.add(0, "l1");
            listNames.add(1, "l2");
            listNames.add(2, "l3");
            listNames.add(3, "l4");
            Log.v("MainActivity", "ListNames was empty");
            generateLocalLists(listNames);
        }

        list1TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list1Intent = new Intent(MainActivity.this, Main2Activity.class);
                new Utils().putListExtrasInIntentBundle(list1Intent, listNames);
                list1Intent.putExtra("parentList", listNames.get(0));
                list1Intent.putExtra("index", 0);
                startActivity(list1Intent);
            }
        });
        list2TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list2Intent = new Intent(MainActivity.this, Main2Activity.class);
                new Utils().putListExtrasInIntentBundle(list2Intent, listNames);
                list2Intent.putExtra("parentList", listNames.get(1));
                list2Intent.putExtra("index", 1);
                startActivity(list2Intent);
            }
        });
        list3TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list3Intent = new Intent(MainActivity.this, Main2Activity.class);
                new Utils().putListExtrasInIntentBundle(list3Intent, listNames);
                list3Intent.putExtra("parentList", listNames.get(2));
                list3Intent.putExtra("index", 2);
                startActivity(list3Intent);
            }
        });
        list4TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list4Intent = new Intent(MainActivity.this, Main2Activity.class);
                new Utils().putListExtrasInIntentBundle(list4Intent, listNames);
                list4Intent.putExtra("parentList", listNames.get(3));
                list4Intent.putExtra("index", 3);
                startActivity(list4Intent);
            }
        });

    }

    private class ListAsyncTask extends AsyncTask<URL, Void, String> {

        String LOGTAG = "LISTASYNCTASK";

        String mParentList;

        private ProgressDialog dialog;
        private MainActivity mActivity;

        public ListAsyncTask(MainActivity activity) {
            this.mActivity = activity;
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait while databae is synced");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(URL... urls) {
            String response = "";
            if (urls.length < 1) {
                Log.e(LOGTAG, "Passed zero urls to asynctask");
                return "";
            }
            try {
                response = makeHttpReq(urls[0]);
            } catch (IOException ioecx) {
                Log.e(LOGTAG, "Couldn't make request", ioecx);
                return "";
            }

            return response;
        }

        /**
         * Update the screen with the resulting data
         */
        @Override
        protected void onPostExecute(String response) {
            int position;
            if (response.isEmpty()) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                return;
            }

            for (position = 0; position < 4; position++) {
                Log.v("Passed position:", Integer.toString(position));
                mParentList = extractListNameN(position + 1, response);
                Log.v("mParentList", mParentList);
                switch (position) {
                    case 0:
                        if (mParentList.isEmpty()) {
                            listNames.add(0, "l1");
                            list4TextView.setText(R.string.list1);
                            continue;
                        } else {
                            listNames.add(0, mParentList);
                            list1TextView.setText(listNames.get(0));
                        }
                        break;
                    case 1:
                        if (mParentList.isEmpty()) {
                            listNames.add(1, "l2");
                            list4TextView.setText(R.string.list2);
                            continue;
                        } else {
                            listNames.add(1, mParentList);
                            list2TextView.setText(listNames.get(1));
                        }
                        break;
                    case 2:
                        if (mParentList.isEmpty()) {
                            listNames.add(2, "l3");
                            list4TextView.setText(R.string.list3);
                            continue;
                        } else {
                            listNames.add(2, mParentList);
                            list3TextView.setText(listNames.get(2));
                        }
                        break;
                    case 3:
                        if (mParentList.isEmpty()) {
                            listNames.add(3, "l4");
                            list4TextView.setText(R.string.list4);
                            continue;
                        } else {
                            listNames.add(3, mParentList);
                            list4TextView.setText(listNames.get(3));
                        }
                        break;
                }


                ArrayList<Product> inStockProds = extractJSON(response, 'i', position);
                ArrayList<Product> outStockProds = extractJSON(response, 'o', position);
                ArrayList<Product> shopStockProds = extractJSON(response, 's', position);


                if (inStockProds != null && outStockProds != null && shopStockProds != null) {
                    updateDatabase(inStockProds, 'i');
                    updateDatabase(outStockProds, 'o');
                    updateDatabase(shopStockProds, 's');
                }
            }

            ArrayList<List> lists = extractLists(response);
            if (lists != null) {
                updateListItems(lists);
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        private String makeHttpReq(URL url) throws IOException {
            String response = "";
            HttpURLConnection urlCon = null;
            InputStream inStr = null;
            try {
                urlCon = (HttpURLConnection) url.openConnection();
                urlCon.setRequestMethod("GET");
                urlCon.setReadTimeout(10000 /* milliseconds */);
                urlCon.setConnectTimeout(15000 /* milliseconds */);
                urlCon.connect();
                inStr = urlCon.getInputStream();
                if (urlCon.getResponseCode() == 200) {
                    inStr = urlCon.getInputStream();
                    response = new Utils().readFromStream(inStr);
                }
            } catch (IOException e) {
                Log.e(LOGTAG, "Error getting response from web server.", e);
                Log.e(LOGTAG, "Returning");
                return "";
            } finally {
                if (urlCon != null) {
                    urlCon.disconnect();
                }
                if (inStr != null) {
                    // function must handle java.io.IOException here
                    inStr.close();
                }
            }
            Log.v("JSON Response:", response);
            return response;
        }

        private ArrayList<Product> extractJSON(String JSON_Str, char list, int position) {
            ArrayList<Product> prods = new ArrayList<>();
            if (TextUtils.isEmpty(JSON_Str)) {
                Log.v(LOGTAG, "Warning: JSON_str is empty!");
                return null;
            }

            try {
                JSONObject response = new JSONObject(JSON_Str);
                String thisList = "";

                switch (list) {
                    case 'i':
                        thisList = "instock";
                        break;
                    case 'o':
                        thisList = "outstock";
                        break;
                    case 's':
                        thisList = "shopstock";
                        break;
                    default:
                        Log.v(LOGTAG, "Bad list type Specified");
                        break;
                }

            /* Make sure there is indeed an entry in this array */
                if (response.length() > 0) {
                    JSONArray clientLists = response.getJSONArray("lists");
                    Log.v("ASYNCTASK", "ClientLists length was " + Integer.toString(clientLists.length()) + "; position is " + Integer.toString(position));
                    if (clientLists.length() <= position) {
                        // There was not a list number `position`
                        return null;
                    }
                    JSONObject parentList = clientLists.getJSONObject(position);
                    Log.v("Iterating", "Parsing objects");

                /* Parse the appropriate child list */
                    JSONArray stockItems = parentList.getJSONObject(thisList).getJSONArray("items");
                    Product p;
                    for (int i = 0; i < stockItems.length(); i++) {
                        p = makeProductFromJSONObject(stockItems.getJSONObject(i));
                        Log.v(LOGTAG, "returned");
                        if (p != null) {
                            Log.v(LOGTAG, "Adding item");
                            prods.add(p);
                        }
                        //Log.v("TEMP", "ADDED: " + prods.get(i).toString());
                    }

                }

            } catch (JSONException e) {
                Log.e(LOGTAG, "Could not parse JSON from request to server to update inventories");
                return null;
            }
            return prods;
        }

        private ArrayList<List> extractLists(String JSONStr) {
            ArrayList<List> lists = new ArrayList<>();
            if (TextUtils.isEmpty(JSONStr)) {
                Log.v(LOGTAG, "Warning: JSON_str is empty!");
                return null;
            }
            try {
                JSONObject response = new JSONObject(JSONStr);
                JSONArray JSONLists = response.getJSONArray("lists");
                JSONObject listObj, listInStock, listOutStock, listShopStock;
                for (int i = 0; i < JSONLists.length(); i++) {
                    listObj = JSONLists.getJSONObject(i);
                    listInStock = listObj.getJSONObject("instock");
                    listOutStock = listObj.getJSONObject("outstock");
                    listShopStock = listObj.getJSONObject("shopstock");
                    Log.v("InStock", listInStock.getString("_id"));
                    Log.v("OutStock", listOutStock.getString("_id"));
                    Log.v("ShopStock", listShopStock.getString("_id"));


                    // String mParentId, String mInStockId, String mOutStockId, String mShopStockId, String mListName, int index
                    List l = new List(
                            listObj.getString("_id"),
                            listInStock.getString("_id"),
                            listOutStock.getString("_id"),
                            listShopStock.getString("_id"),
                            listObj.getString("list_name"),
                            i
                    );
                    Log.v("extractList", "inStock: " + l.getmInStockId());
                    Log.v("extractList", "outStock: " + l.getmOutStockId());
                    Log.v("extractList", "shopStock: " + l.getmShopStockId());

                    lists.add(l);
                }
            } catch (JSONException e) {
                Log.e(LOGTAG, "Could not parse JSON from request to server to update inventories");
                return null;
            }
            return lists;
        }

        private Product makeProductFromJSONObject(JSONObject jsonObject) {
            Product p = null;
            try {
                String notFound = jsonObject.getString("Not Found");
                Log.v("AsyncTask", " Product not found:");
                return null;
            } catch (JSONException ex12) {
                Log.v(LOGTAG, "Not found field not encountered; Product is valid");
                try {
                    p = new Product(
                            jsonObject.getString("name"),
                            jsonObject.getString("unit_of_measure"),
                            jsonObject.getString("upc"),
                            0,
                            jsonObject.getInt("quantity"),
                            null,
                            jsonObject.getString("description"),
                            -1,
                            jsonObject.getString("_id"));
                } catch (JSONException exc2) {
                    Log.v(LOGTAG, "Could not parse JSON Object", exc2);
                }
                //Log.v("MakeProd", "ID is " + p.getmApiId());
                return p;
            }
        }

        private void updateDatabase(ArrayList<Product> childList, char table) {
            Product tmp;
            InventoryAppDBHelper dbHandler;
            dbHandler = new InventoryAppDBHelper(MainActivity.this, mParentList, null, 1);
            //dbHandler.deleteInventoryTable(table);

            Log.v("Update", "Updating database table " + table);
            for (int i = 0; i < childList.size(); i++) {
                dbHandler.updateCreateItem(childList.get(i), table);
            }

            dbHandler.close();
        }

        private String extractListNameN(int n, String response) {
            try {
                JSONObject clientData = new JSONObject(response);
                JSONArray clientLists = clientData.getJSONArray("lists");
                if (clientLists.length() < n) {
                    return "";
                } else {
                    String listName = clientLists.getJSONObject(n - 1).getString("list_name");
                    return listName;
                }
            } catch (JSONException e) {
                Log.v("extractListNameN", "JSON exception");
            }
            return "";
        }

        private void updateListItems(ArrayList<List> lists) {
            int i;
            ListDBHelper listDBHelper;
            List list;
            listDBHelper = new ListDBHelper(MainActivity.this, LISTDBNAME, null, 1);

            Log.v("updateListItems", "Updating lists");
            for (i = 0; i < lists.size(); i++) {
                list = lists.get(i);
                Log.v("Inserting List", "Parent: " + list.getmParentId() + "; name: " + list.getmListName()
                        + " inStockId: " + list.getmInStockId() + " outStockId: " + list.getmOutStockId()
                        + " shopStockId: " + list.getmShopStockId() + " index: " + list.getmIndex());
                listDBHelper.addList(lists.get(i));
                List thisList = listDBHelper.getList(list.getmListName());
                Log.v("Actually inserted", "Parent: " + list.getmParentId() + "; name: " + list.getmListName()
                        + " inStockId: " + list.getmInStockId() + " outStockId: " + list.getmOutStockId()
                        + " shopStockId: " + list.getmShopStockId() + " index: " + list.getmIndex());
            }
            if(lists.size() < 4){
                for( ; i < 4; i++){
                    Log.v("i", " is " + Integer.toString(i));
                    listDBHelper.addList("-1", "-1", "-1", "-1", i, listNames.get(i));
                }
            }
            listDBHelper.close();
        }

    }

    private void generateLocalLists(ArrayList<String> names) {
        ListDBHelper listDBHelper;
        listDBHelper = new ListDBHelper(MainActivity.this, LISTDBNAME, null, 1);
        Log.v("generateLocalLists", "Generating local lists");
        for(int i = 0; i < 4; i++){
            listDBHelper.addList("-1", "-1", "-1", "-1", i, names.get(i));
        }
        listDBHelper.close();
    }

}