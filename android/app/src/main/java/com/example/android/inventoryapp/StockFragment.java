package com.example.android.inventoryapp;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.MalformedJsonException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.android.inventoryapp.MainActivity.LISTDBNAME;

/**
 * A simple {@link Fragment} subclass.
 */
public class StockFragment extends Fragment {

    // Constants for indicating returning calls from intents
    static final int ADD_PRODUCT = 1;
    static final int DETAIL_UPDATE = 2;
    static final int DETAIL_RESULT_INC_OR_DECREMENT = 3;
    static final int DETAIL_RESULT_DELETE = 4;
    static final int DETAIL_RESULT_MOVE = 5;

    public enum updateAction {
        delete,
        update,
        newItem,
        move
    }

    View root;

    String TAG = "StockFragment";


    // Adapter for list
    ProductAdapter adapter;
    // Ref to new product button
    FloatingActionButton addProductButton;

    // products holds the resulting products obtained from the query
    private ArrayList<Product> products;

    int tableIndex;
    String tableName;
    List thisList;

    char table;

    // The listView that will be populated with products
    ListView productListView;

    populateDb dbPop;

    public StockFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        InventoryAppDBHelper dbHandler;
        ListDBHelper listHelper;
        listHelper = new ListDBHelper(getActivity(), LISTDBNAME, null, 1);

        tableIndex = getArguments().getInt("index");
        tableName = getArguments().getString("parentList");
        table = getArguments().getChar("table");
        Log.v(TAG, tableName + "; table: " + table);

        root = inflater.inflate(R.layout.fragment_in_stock, container, false);

        // Obtain ref to new product button
        addProductButton = (FloatingActionButton) root.findViewById(R.id.new_product_fromlist);

        thisList = listHelper.getList(tableName);
        Log.v("Fragment", "DB Returned table is " + thisList.getmListName());

        // Instantiate a new DBHandler
        Log.v("Fragment:", "Table name is " + tableName);
        dbHandler = new InventoryAppDBHelper(getActivity(), tableName, null, 1);

        dbPop = new populateDb();

        // Load products from database
        products = dbPop.populateListFromDb(dbHandler, table);
        dbHandler.close();

        // Obtain ref to list
        productListView = (ListView) root.findViewById(R.id.list);

        // Set the array adapter
        adapter = new ProductAdapter(getActivity(), products,
                table, tableName, thisList, getActivity());
        productListView.setAdapter(adapter);

        if (adapter.isEmpty()) {
            TextView error = (TextView) root.findViewById(R.id.database_empty_text_view);
            error.setVisibility(View.VISIBLE);
        } else {
            TextView error = (TextView) root.findViewById(R.id.database_empty_text_view);
            error.setVisibility(View.GONE);
        }

        // Set a click listener to got to the detail page when the list item is clicked on
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Get the feedItem object at the given position the user clicked on
                Product product = products.get(position);
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                detailIntent.putExtra("key", product.getmKey());
                detailIntent.putExtra("tableName", tableName);
                detailIntent.putExtra("position", position);
                detailIntent.putExtra("table", table);
                // Start the new activity
                startActivityForResult(detailIntent, DETAIL_UPDATE);

            }
        });

        // Set a click listener for when new product button is clicked
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newProductIntent = new Intent(getActivity(), ProductEditActivity.class);
                newProductIntent.putExtra("table", table);
                newProductIntent.putExtra("tableName", tableName);
                newProductIntent.putExtra("context", "newProduct");
                startActivityForResult(newProductIntent, ADD_PRODUCT);
            }

        });

        // Inflate the layout for this fragment
        return root;
    }

    // Perform list refresh on callback from new product intent
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String updateJSON;
        InventoryAppDBHelper dbHandler;
        dbHandler = new InventoryAppDBHelper(getActivity(), tableName, null, 1);
        if (requestCode == ADD_PRODUCT) {
            Log.v("Returned from new ", "product");
            if (resultCode == Activity.RESULT_OK) {
                refresh(dbHandler);
                Product p = products.get(products.size() - 1);
                doNewItem(table, p);
                products.clear();
                products = dbPop.populateListFromDb(dbHandler, table);
                p = products.get(products.size() - 1);
                Log.v("Product id is", p.getmApiId());
                refresh(dbHandler);
            }
        } else if (requestCode == DETAIL_UPDATE) {
            if (resultCode == DETAIL_RESULT_DELETE) {
                // Refresh the list if item was deleted
                int position = data.getIntExtra("position", -1);
                if (position != -1 && position < products.size()) {
                    Product p = products.get(position);
                    doDelete(p);
                }
                refresh(dbHandler);
            } else if (resultCode == DETAIL_RESULT_INC_OR_DECREMENT) {
                // Update quantity if increment/decrement performed.
                int index = data.getIntExtra("position", -1);
                int newQuantity = data.getIntExtra("newQuantity", -1);
                if ((index != (-1))) {
                    products.clear();
                    products = dbPop.populateListFromDb(dbHandler, table);
                    Product p = products.get(index);
                    if (newQuantity != -1) {
                        p.setmQuantity(newQuantity);
                    }
                    adapter.notifyDataSetChanged();
                    updateJSON = p.buildSetAllJSON("", updateAction.update);
                    if (!thisList.getmParentId().equals("-1") && !updateJSON.isEmpty()) {
                        Log.v("INCDEC", "POSTING");
                        new updateAsyncTask(updateAction.update, p.getmApiId(), p.getmKey(),
                                updateJSON, "", "", getActivity(), thisList, table).execute();
                    }
                }
                refresh(dbHandler);
            } else if (resultCode == DETAIL_RESULT_MOVE) {
                int position = data.getIntExtra("position", -1);
                char moveTo = data.getCharExtra("moveTo", 'z');
                Log.v("Position is ", Integer.toString(position));
                if (position != -1 && position < products.size() && moveTo != 'z') {
                    Product p = products.get(position);
                    String toChildId = "";
                    String fromChildId = "";
                    switch (moveTo) {
                        case 'i':
                            Log.v("Post new to table", Character.toString(moveTo));
                            toChildId = thisList.getmInStockId();
                            break;
                        case 'o':
                            toChildId = thisList.getmOutStockId();
                            break;
                        case 's':
                            toChildId = thisList.getmShopStockId();
                            break;
                    }
                    switch (table){
                        case 'i':
                            fromChildId = thisList.getmInStockId();
                            break;
                        case 'o':
                            fromChildId  = thisList.getmOutStockId();
                            break;
                        case 's':
                            fromChildId  = thisList.getmShopStockId();
                            break;
                    }
                    Log.v("Fragment", "Child id is " + toChildId);
                    Log.v("Fragment", "Product to move" + p.getmName());
                    updateJSON = p.buildSetAllJSON("", updateAction.move);
                    if (!toChildId.isEmpty() && !toChildId.equals("-1") && !updateJSON.isEmpty()
                            && !fromChildId.isEmpty() && !fromChildId.equals("-1")) {
                        new updateAsyncTask(updateAction.move, p.getmApiId(), p.getmKey(),
                                updateJSON, toChildId, fromChildId, getActivity(), thisList, table).execute();
                    }
                } else {
                    Log.v("DETAIL_RESULT_MOVE", "Error in item move parameters");
                }
                refresh(dbHandler);
            }
        }
        dbHandler.close();
    }

    @Override
    public void onStart() {
        InventoryAppDBHelper dbHandler;
        dbHandler = new InventoryAppDBHelper(getActivity(), tableName, null, 1);
        super.onStart();
        refresh(dbHandler);
        dbHandler.close();
    }

    private void refresh(InventoryAppDBHelper dbHandler) {
        adapter.clear();
        products.clear();
        products = dbPop.populateListFromDb(dbHandler, table);
        adapter = new ProductAdapter(getActivity(), products, table, tableName, thisList, getActivity());
        productListView.setAdapter(adapter);
        if (adapter.isEmpty()) {
            TextView error = (TextView) root.findViewById(R.id.database_empty_text_view);
            error.setVisibility(View.VISIBLE);
        } else {
            TextView error = (TextView) root.findViewById(R.id.database_empty_text_view);
            error.setVisibility(View.GONE);
        }
    }

    private void doDelete(Product p) {
        Log.v("Delete item", "Product " + p.getmName() + "'s id is " + p.getmApiId());
        if (!thisList.getmParentId().equals("-1")) {
            new updateAsyncTask(StockFragment.updateAction.delete, p.getmApiId(), p.getmKey(),
                    "", "", "", getActivity(), thisList, table).execute();
        }
    }

    private void doNewItem(char toTable, Product p) {
        String updateJSON;
        String childId = "";
        Log.v("NewProduct", p.getmName());
        switch (toTable) {
            case 'i':
                Log.v("Post new to table", Character.toString(toTable));
                childId = thisList.getmInStockId();
                break;
            case 'o':
                childId = thisList.getmOutStockId();
                break;
            case 's':
                childId = thisList.getmShopStockId();
                break;
        }
        Log.v("Fragment", "Child id is " + childId);
        updateJSON = p.buildSetAllJSON(childId, StockFragment.updateAction.newItem);
        Log.v("doNewItem Posting", updateJSON);
        if (!childId.isEmpty() && !childId.equals("-1") && !updateJSON.isEmpty()) {
            updateAsyncTask task = new updateAsyncTask(StockFragment.updateAction.newItem, "", p.getmKey(),
                    updateJSON, childId, "", getActivity(), thisList, table);
            task.execute();
            try{
                task.get();
            } catch (Exception e){
                Log.v("doNewItem", "Exception", e);
            }
            if(p.getmApiId() == null){
                p.setmApiId("-1");
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
