package com.example.android.inventoryapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * {@link ProductAdapter } is an {@link ArrayAdapter} that can provide the layout for each list item
 * based on a data source, which is a list of {@link Product} objects.
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    // Instantiate dbHandler
    InventoryAppDBHelper dbHandler;

    private Context mContext;

    char table;

    String tableName;
    List thisList;
    Activity thisActivity;

    /**
     * Create a new {@link ProductAdapter} object.
     *
     * @param context  is the current context (i.e. Activity) that the adapter is being created in.
     * @param products is the list of products to be displayed.
     */
    public ProductAdapter(Context context, ArrayList<Product> products, char the_table, String name,
                          List list, Activity activity) {
        super(context, 0, products);
        this.mContext = context;
        thisList = list;
        tableName = name;
        thisActivity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the Product object located at this position in the list
        final Product currentProduct = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID product_name_list
        TextView productNameTextView = (TextView) listItemView.findViewById(R.id.product_name_list);
        // Get the product name from the currentProduct object and set this text on
        // the name TextView.
        productNameTextView.setText(currentProduct.getmName());

        // Find the TextView in the list_item.xml layout with the ID quantity_list.
        final TextView productQuantityTextView = (TextView) listItemView.findViewById(R.id.quantity_list);
        // Get the quantity from the currentProduct object and set this text on
        // the quantity TextView.
        productQuantityTextView.setText("Quantity: " + currentProduct.getStringmQuantity());

        // Find the TextView in the list_item.xml layout with the ID price_list.
        TextView productPriceTextView = (TextView) listItemView.findViewById(R.id.price_list);
        // Get the quantity from the currentProduct object and set this text on
        // the price TextView.
        productPriceTextView.setText("Barcode: " + currentProduct.getmUPC());

        // Find the Button in the list_item.xml layout with the ID sell_button_list
        Button sellButton = (Button) listItemView.findViewById(R.id.decrement_button_list);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Instantiate a new DBHelper
                int newQuant = currentProduct.decrementQuantityByOne();
                dbHandler = new InventoryAppDBHelper(view.getContext(), tableName, null, 1);
                productQuantityTextView.setText("Quantity: " + Integer.toString(newQuant));
                // Instantiate a new DBHelper
                dbHandler.updateProductQuantity(currentProduct.getmKey(), newQuant, table);
                dbHandler.close();

                // Update quantity if increment/decrement performed.
               String updateJSON = currentProduct.buildSetAllJSON("", StockFragment.updateAction.update);
                    if (!thisList.getmParentId().equals("-1") && !updateJSON.isEmpty()) {
                        Log.v("INCDEC", "POSTING");
                        new updateAsyncTask(StockFragment.updateAction.update,
                                currentProduct.getmApiId(), currentProduct.getmKey(),
                                updateJSON, "", "", thisActivity, thisList, table).execute();
                    }

            }
        });


        // Find the thumbnail ImageView in list_item.xml
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image_list);
        // Check if an image is provided for this Product or not
        if (currentProduct.getmImage() == null) {
            // Otherwise hide the ImageView (set visibility to GONE)
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setImageBitmap(currentProduct.getmImage());
            // Make sure the view is visible
            imageView.setVisibility(View.VISIBLE);
        }

        // Return the whole list item layout (containing 2 TextViews) so that it can be shown in
        // the ListView.
        return listItemView;
    }
}
