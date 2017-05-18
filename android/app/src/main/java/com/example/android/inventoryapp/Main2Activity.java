package com.example.android.inventoryapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

public class Main2Activity extends AppCompatActivity {

    String parentList = "l1";
    ArrayList<String> listNames;
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    */

    // ListView to be used with the Navigation Drawer
    private ListView mDrawerList;
    // ArrayAdapter to be used with the Navigation Drawer
    private ArrayAdapter<String> mAdapter;


    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listNames = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        parentList = bundle.getString("parentList");
        listNames.add(0, bundle.getString("list1"));
        listNames.add(1, bundle.getString("list2"));
        listNames.add(2, bundle.getString("list3"));
        listNames.add(3, bundle.getString("list4"));


        Log.v("Parent list is ", parentList);

        // Instantiate the Navigation Drawer
        mDrawerList = (ListView) findViewById(R.id.navList);

        // Show the toggle for the Navigation drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Get reference to the drawer layout and current activity
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        // Instantiate the entries in the nav drawer
        addNavDrawerItems();
        setupDrawer();

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.listviewpager);

        // Create an adapter that knows which fragment should be shown on each page
        listFragmentPagerAdapter adapter = new listFragmentPagerAdapter(getSupportFragmentManager(), parentList);

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    // Sync the Nav Drawer icon with the current activity
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void addNavDrawerItems() {
        final String[] activitiesArray = {listNames.get(0), listNames.get(1), listNames.get(2), listNames.get(3)};
        if (activitiesArray[0].equals("l1") || activitiesArray[0].isEmpty()) {
            activitiesArray[0] = getString(R.string.list1);
        }
        if (activitiesArray[1].equals("l2") || activitiesArray[1].isEmpty()) {
            activitiesArray[1] = getString(R.string.list2);
        }
        if (activitiesArray[2].equals("l3") || activitiesArray[2].isEmpty()) {
            activitiesArray[2] = getString(R.string.list3);
        }
        if (activitiesArray[3].equals("l4") || activitiesArray[3].isEmpty()) {
            activitiesArray[3] = getString(R.string.list4);
        }
        final Class[] classesArray = {Main2Activity.class};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, activitiesArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String activity = activitiesArray[position];
                if (position == 0 && (!parentList.equals(listNames.get(0)))) {
                    // Open an intent to the first list
                    Intent list1Intent = new Intent(Main2Activity.this, classesArray[0]);
                    new Utils().putListExtrasInIntentBundle(list1Intent, listNames);
                    list1Intent.putExtra("parentList", listNames.get(0));
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    startActivity(list1Intent);
                } else if (position == 1 && (!parentList.equals(listNames.get(1)))) {
                    // Open an intent to the second list
                    Intent list2Intent = new Intent(Main2Activity.this, classesArray[0]);
                    new Utils().putListExtrasInIntentBundle(list2Intent, listNames);
                    list2Intent.putExtra("parentList", listNames.get(1));
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    startActivity(list2Intent);
                } else if (position == 2 && (!parentList.equals(listNames.get(2)))) {
                    // Open an intent to the third list
                    Intent list3Intent = new Intent(Main2Activity.this, classesArray[0]);
                    new Utils().putListExtrasInIntentBundle(list3Intent, listNames);
                    list3Intent.putExtra("parentList", listNames.get(2));
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    startActivity(list3Intent);
                } else if (position == 3 && (!parentList.equals(listNames.get(3)))) {
                    // Open an intent to the fourth list
                    Intent list4Intent = new Intent(Main2Activity.this, classesArray[0]);
                    new Utils().putListExtrasInIntentBundle(list4Intent, listNames);
                    list4Intent.putExtra("parentList", listNames.get(3));
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    startActivity(list4Intent);
                } else {
                }
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item click
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public String getList() {
        return parentList;
    }

    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}