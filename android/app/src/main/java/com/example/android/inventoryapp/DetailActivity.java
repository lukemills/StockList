package com.example.android.inventoryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.name;

/**
 * DetailActivity displays the details of the product the user clicked on.
 */
public class DetailActivity extends AppCompatActivity {

    static final int EDIT_PRODUCT = 1;

    // Instantiate new dbHandler
    InventoryAppDBHelper dbHandler;

    char table;

    String tableName;

    Product product;

    int key, position;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        Toolbar detailToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(detailToolbar);
        detailToolbar.setTitleTextColor(0xFFFFFFFF);

        Bundle bundle = getIntent().getExtras();

        key = bundle.getInt("key");
        tableName = bundle.getString("tableName");
        position = bundle.getInt("position");
        final ImageView productImageView = (ImageView) findViewById(R.id.image_detail);
        TextView nameTextView = (TextView) findViewById(R.id.product_name_detail);
        final TextView quantityTextView = (TextView) findViewById(R.id.reorder_detail);
        final TextView unitTextView = (TextView) findViewById(R.id.units_detail);
        final TextView descriptionTextView = (TextView) findViewById(R.id.description_detail);
        table = bundle.getChar("table");

        // Instantiate a new DBHelper
        dbHandler = new InventoryAppDBHelper(this, tableName, null, 1);

        // Buttons
        Button incrementButton = (Button) findViewById(R.id.reorder_increment_detail);
        Button decrementButton = (Button) findViewById(R.id.reorder_decrement_detail);

        product = dbHandler.getProduct(key, table);

        populateDetailActivityLayout();

        // OnClickListener for increment
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product.incrementQuantity(1);
                dbHandler.updateProductQuantity(key, product.getmQuantity(), table);
                quantityTextView.setText(Integer.toString(product.getmQuantity()));
                Intent returnIntent = getIntent();
                returnIntent.putExtra("position", position);
                returnIntent.putExtra("newQuantity", product.getmQuantity());
                setResult(StockFragment.DETAIL_RESULT_INC_OR_DECREMENT, returnIntent);
            }
        });

        // OnClickListener for increment
        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (product.getmQuantity() > 0) {
                    product.incrementQuantity(-1);
                    dbHandler.updateProductQuantity(key, product.getmQuantity(), table);
                    quantityTextView.setText(Integer.toString(product.getmQuantity()));
                    Intent returnIntent = getIntent();
                    returnIntent.putExtra("nameTextView", name);
                    returnIntent.putExtra("position", position);
                    returnIntent.putExtra("newQuantity", product.getmQuantity());
                    setResult(StockFragment.DETAIL_RESULT_INC_OR_DECREMENT, returnIntent);
                }
            }
        });

    }

    private void transitionItemToTable(Product product, char from_table, char to_table) {
        dbHandler.deleteProduct(product.getmKey(), from_table);
        if (product.getmImage() != null) {
            dbHandler.addProduct(product, BitMapUtility.getBytes(product.getmImage()), to_table);
        } else {
            dbHandler.addProduct(product, null, to_table);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.move_action_detail:
                int listsArray = R.array.instock_move_array;
                switch (table) {
                    case 'i':
                        listsArray = R.array.instock_move_array;
                        break;
                    case 'o':
                        listsArray = R.array.outstock_move_array;
                        break;
                    case 's':
                        listsArray = R.array.shopstock_move_array;
                        break;
                    default:
                        break;
                }
                // Prompt the user to confirm deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle(getString(R.string.transition_prompt_heading));
                builder.setItems(listsArray, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                char moveTo;
                                switch (table) {
                                    case 'i':
                                        if (which == 0) {
                                            moveTo = 'o';
                                        } else {
                                            moveTo = 's';
                                        }
                                        transitionItemToTable(product, 'i', moveTo);
                                        break;
                                    case 'o':
                                        if (which == 0) {
                                            moveTo = 's';
                                        } else {
                                            moveTo = 'i';
                                        }
                                        transitionItemToTable(product, 'o', moveTo);
                                        break;
                                    default:
                                        if (which == 0) {
                                            moveTo = 'i';
                                        } else {
                                            moveTo = 'o';
                                        }
                                        transitionItemToTable(product, 's', moveTo);
                                        break;
                                }
                                dbHandler.close();
                                Intent returnIntent = getIntent();
                                returnIntent.putExtra("position", position);
                                returnIntent.putExtra("key", product.getmKey());
                                returnIntent.putExtra("moveTo", moveTo);
                                Log.v("Moving item to ", Character.toString(moveTo));
                                setResult(StockFragment.DETAIL_RESULT_MOVE, returnIntent);
                                finish();
                            }
                        }
                ).show();

                return true;

            case R.id.delete_action_detail:
                // Prompt the user to confirm deletion
                new AlertDialog.Builder(DetailActivity.this)
                        .setTitle(getString(R.string.delete_prompt_heading))
                        .setMessage(getString(R.string.delete_prompt_body))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            // If user confirms, then delete product
                            public void onClick(DialogInterface dialog, int which) {
                                dbHandler.deleteProduct(product.getmKey(), table);
                                dbHandler.close();
                                Intent returnIntent = getIntent();
                                returnIntent.putExtra("position", position);
                                setResult(StockFragment.DETAIL_RESULT_DELETE, returnIntent);
                                finish();
                            }
                        })
                        // Otherwise depict a toast indicating product wasn't deleted
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(DetailActivity.this, getString(R.string.not_deleted), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setIcon(android.R.drawable.ic_delete)
                        .show();
                return true;

            case R.id.edit_action_detail:
                Intent editProductIntent = new Intent(DetailActivity.this, ProductEditActivity.class);
                editProductIntent.putExtra("table", table);
                editProductIntent.putExtra("tableName", tableName);
                editProductIntent.putExtra("key", key);
                editProductIntent.putExtra("context", "editProduct");
                startActivityForResult(editProductIntent, EDIT_PRODUCT);
                Intent returnIntent = getIntent();
                returnIntent.putExtra("position", position);
                setResult(StockFragment.DETAIL_RESULT_INC_OR_DECREMENT, returnIntent);
                finish();

            default:
                // If we got here, user action wasn't recognized;
                // thus invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        Log.v("Detail", "closing connection to db");
        dbHandler.close();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_PRODUCT) {
            if (resultCode == RESULT_OK) {
                int keyResult = data.getIntExtra("key", -1);
                if (keyResult != -1) {
                    product = dbHandler.getProduct(keyResult, table);
                    //Log.v("Desc:", product.getmDescription());
                    populateDetailActivityLayout();
                }
            }
        }
    }

    private void populateDetailActivityLayout() {
        final ImageView productImageView = (ImageView) findViewById(R.id.image_detail);
        TextView nameTextView = (TextView) findViewById(R.id.product_name_detail);
        final TextView quantityTextView = (TextView) findViewById(R.id.reorder_detail);
        final TextView unitTextView = (TextView) findViewById(R.id.units_detail);
        final TextView descriptionTextView = (TextView) findViewById(R.id.description_detail);

        nameTextView.setText(product.getmName());
        quantityTextView.setText(Integer.toString(product.getmQuantity()));
        unitTextView.setText(product.getmUnits());
        if (product.getmImage() != null) {
            productImageView.setImageBitmap(product.getmImage());
        } else {
            productImageView.setImageResource(R.drawable.default_image);
        }

        if (product.getmDescription() == null || product.getmDescription().equals("null")) {
            // If no descriptionTextView, hide the descriptionTextView text view
            descriptionTextView.setVisibility(View.GONE);
        } else {
            // Otherwise show it
            descriptionTextView.setText(product.getmDescription());
            // Make sure the view is visible
            descriptionTextView.setVisibility(View.VISIBLE);
        }
    }
}
