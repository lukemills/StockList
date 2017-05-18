package com.example.android.inventoryapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lukem on 4/27/2017.
 */

public class updateAsyncTask extends AsyncTask<URL, Void, String> {

    String baseUrl = "http://hlr710vm.homelinux.com:5000/api";

    StockFragment.updateAction action;
    String updateJSON;
    String id;
    String toListId;
    String fromListId;
    int key;
    List thisList;
    String listName;
    Activity thisActivity;
    char thisTable;

    // (updateAction.move, p.getmApiId(), updateJSON, childId).
    public updateAsyncTask(StockFragment.updateAction doAction, String itemId, int itemLocalKey, String doUpdateJSON,
                           String toChildId, String fromChildId, Activity activity, List list, char table) {
        if((itemId == null || itemId.isEmpty()) && doAction != StockFragment.updateAction.newItem){
            Log.v("updateAsync", "itemId is empty");
            if(doAction == StockFragment.updateAction.delete){ return; }
            else { action = StockFragment.updateAction.newItem; }
        } else {
            action = doAction;
        }
        updateJSON = doUpdateJSON;
        id = itemId;
        toListId = toChildId;
        fromListId = fromChildId;
        key = itemLocalKey;
        thisList = list;
        listName = thisList.getmListName();
        thisActivity = activity;
        thisTable = table;
    }

    @Override
    protected String doInBackground(URL... urls) {
        if (action == StockFragment.updateAction.delete) {
            postDelete(id);
        } else if (action == StockFragment.updateAction.update) {
            postUpdate(id, updateJSON);
        } else if (action == StockFragment.updateAction.newItem) {
            postNewItem(toListId, updateJSON, key);
        } else if (action == StockFragment.updateAction.move){
            postMoveItem(toListId, fromListId, id);
        }
        return "";
    }

    public void postDelete(String id) {
        String postResponse = "";
        HttpURLConnection urlConnection = null;
        URL url = null;
        String respMess = "";
        try {
            Log.v("DELETING TO", baseUrl + "/item/" + id);
            url = new Utils().makeUrl(baseUrl + "/item/" + id);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.connect();
            Log.v("Done", " connecting");
            urlConnection.getResponseCode();
            if (urlConnection.getResponseCode() != 200) {
                Log.v("RESPNSE", Integer.toString(urlConnection.getResponseCode()));
                // Server returned HTTP error code.
                Log.v("postDelete", "failure - " + urlConnection.getResponseMessage());
                BufferedInputStream errorStream = new BufferedInputStream(urlConnection.getErrorStream());
                postResponse = new Utils().readFromStream(errorStream);
                Log.v("ErrorStream", postResponse);
            } else {
                Log.v("postDelete", "success");
            }
        } catch (IOException e) {
            Log.v("StockFragment", "Exception in postDelete" + respMess, e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public void postUpdate(String id, String updateJSON) {
        HttpURLConnection urlConnection = null;
        URL url = null;
        try {
            url = new URL(baseUrl + "/item/" + id);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "text/plain");
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(15000);

            Log.v("UpdateAsync", "Posting " + updateJSON + " to " + baseUrl + "/item/" + id);

            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(urlConnection.getOutputStream())
            );
            out.write(updateJSON);
            out.flush();
            Log.v("postUpdate", "Closing connection");
            int stat = urlConnection.getResponseCode();
            if (stat == HttpURLConnection.HTTP_OK) {
                Log.v("postUpdate", "sucess");

            } else {
                // Server returned HTTP error code.
                Log.v("postUpdate", "failure - " + urlConnection.getResponseMessage());
            }
        } catch (Exception e) {
            Log.v("StockFragment", "Error in postUpdate", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public void postNewItem(String childId, String updateJSON, int key) {
        HttpURLConnection urlConnection = null;
        URL url = null;
        String postResponse = "";
        int reqStatus = -1;
        try {
            url = new URL(baseUrl + "/item");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "text/plain");
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(15000);

            Log.v("postNew", "Posting " + updateJSON + " to " + baseUrl + "/item");

            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(urlConnection.getOutputStream())
            );
            out.write(updateJSON);
            out.flush();
            Log.v("postNew", "Closing connection");
            reqStatus = urlConnection.getResponseCode();
            if (reqStatus == HttpURLConnection.HTTP_OK) {
                Log.v("postNew", "success");

                BufferedInputStream inStream = new BufferedInputStream(urlConnection.getInputStream());
                postResponse = new Utils().readFromStream(inStream);
                Log.v("postNew", "Response - " + postResponse);

                /* Update the local database with the api id */
                InventoryAppDBHelper dbHandler;
                dbHandler = new InventoryAppDBHelper(thisActivity, thisList.getmListName(), null, 1);
                String apiId = new JSONObject(postResponse).getString("id");
                Log.v("postNew", "new id is " + apiId);
                dbHandler.updateApiId(key, apiId, thisTable);
                dbHandler.close();

            } else {
                // Server returned HTTP error code.
                Log.v("postNew", "failure - " + urlConnection.getResponseMessage());
                BufferedInputStream errorStream = new BufferedInputStream(urlConnection.getErrorStream());
                postResponse = new Utils().readFromStream(errorStream);
                Log.v("postNew", "Response " + postResponse);

            }
        } catch (Exception e) {
            Log.v("StockFragment", "Error in postUpdate P1", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public void postMoveItem(String childToId, String childFromId, String itemId){
        String removeJSON = createRemoveOpJSON(itemId);
        String addJSON = createAddOpJSON(itemId);
        HttpURLConnection urlConnection = null;
        URL url = null;
        String postResponse = "";
        int reqStatus = -1;
        try {
            url = new URL(baseUrl + "/child/" + childFromId);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "text/plain");
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(15000);

            Log.v("UpdateAsync", "Posting remove " + removeJSON + " to " + baseUrl + "/child/" + childFromId);

            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(urlConnection.getOutputStream())
            );
            out.write(removeJSON);
            out.flush();
            Log.v("postMove", "Closing connection");
            reqStatus = urlConnection.getResponseCode();
            if (reqStatus == HttpURLConnection.HTTP_OK) {
                Log.v("postMove", "success");

                BufferedInputStream inStream = new BufferedInputStream(urlConnection.getInputStream());
                postResponse = new Utils().readFromStream(inStream);
                Log.v("Response", postResponse);


            } else {
                // Server returned HTTP error code.
                Log.v("postUpdate", "failure - " + urlConnection.getResponseMessage());
                BufferedInputStream errorStream = new BufferedInputStream(urlConnection.getErrorStream());
                postResponse = new Utils().readFromStream(errorStream);
                Log.v("Response", postResponse);
            }
        } catch (Exception e) {
            Log.v("StockFragment", "Error in postUpdate P1", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        try {
            url = new URL(baseUrl + "/child/" + childToId);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "text/plain");
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(15000);

            Log.v("UpdateAsync", "Posting add " + addJSON + " to " + baseUrl + "/child/" + childToId);

            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(urlConnection.getOutputStream())
            );
            out.write(addJSON);
            out.flush();
            Log.v("postUpdate", "Closing connection");
            reqStatus = urlConnection.getResponseCode();
            if (reqStatus == HttpURLConnection.HTTP_OK) {
                Log.v("postUpdate", "success");

                BufferedInputStream inStream = new BufferedInputStream(urlConnection.getInputStream());
                postResponse = new Utils().readFromStream(inStream);
                Log.v("Response", postResponse);


            } else {
                // Server returned HTTP error code.
                Log.v("postUpdate", "failure - " + urlConnection.getResponseMessage());
                BufferedInputStream errorStream = new BufferedInputStream(urlConnection.getErrorStream());
                postResponse = new Utils().readFromStream(errorStream);
                Log.v("Response", postResponse);

            }
        } catch (Exception e) {
            Log.v("StockFragment", "Error in postUpdate P1", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }



    private String createNewOpJSON(String response) {
        //{ "op" : "add", "id" : "<item_id>" }
        JSONArray opDict = new JSONArray();
        JSONObject op = null;
        try {
            JSONObject responseJSON = new JSONObject(response);
            String id = responseJSON.getString("id");
            op = new JSONObject();
            op.put("op", "add");
            op.put("id", id);
        } catch (JSONException e) {
            Log.v("createNewOpJSON", "Malformed JSON', e");
        }
        if (op != null) {
            Log.v("New item string", op.toString());
            opDict.put(op);
            return opDict.toString();
        } else {
            return "";
        }
    }

    private String createAddOpJSON(String itemId){
        JSONArray opDict = new JSONArray();
        JSONObject opDelete = new JSONObject();
        JSONObject opAdd = new JSONObject();
        try {
            opAdd.put("op", "add");
            opAdd.put("id", itemId);
            opDict.put(opAdd);
        } catch (JSONException e) {
            Log.v("createMoveOpJSON", "Malformed JSON', e");
        }
        return opDict.toString();
    }

    private String createRemoveOpJSON(String itemId){
        JSONArray opDict = new JSONArray();
        JSONObject opDelete = new JSONObject();
        try {
            opDelete.put("op", "remove");
            opDelete.put("id", itemId);
            Log.v("opDelete is", opDelete.toString());
            Log.v("item id is", itemId);
            opDict.put(opDelete);
        } catch (JSONException e) {
            Log.v("createMoveOpJSON", "Malformed JSON', e");
        }
        return opDict.toString();
    }
}