package com.example.android.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inventoryapp.BarcodeUtilities.BarcodeCaptureActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by Luke on 7/3/2016.
 */
public class ProductEditActivity extends AppCompatActivity {

    InventoryAppDBHelper dbHandler;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    static final String TAG = "EditProduct";
    private static final int RC_BARCODE_CAPTURE = 9001;

    Bitmap imageBitmap;

    boolean imageChanged = false;

    int key;

    String context;

    byte[] imageByteArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_product_activity);

        Bundle bundle = getIntent().getExtras();
        final char table = bundle.getChar("table");
        String tableName = bundle.getString("tableName");
        context = bundle.getString("context");

        // Instantiate a new DBHelper
        dbHandler = new InventoryAppDBHelper(this, tableName, null, 1);

        final Spinner unitsSpinner = (Spinner) findViewById(R.id.units_new);
        // Create an ArrayAdapter using the string array and a default unitsSpinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                ProductEditActivity.this, R.array.units_array, android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the unitsSpinner
        unitsSpinner.setAdapter(spinnerAdapter);

        // Obtain references to the necessary views
        Button newProductButton = (Button) findViewById(R.id.new_product_button);
        final EditText name = (EditText) findViewById(R.id.name_new);
        final EditText quantity = (EditText) findViewById(R.id.quantity_new);
        final EditText barcodeEditText = (EditText) findViewById(R.id.barcode);
        final Button imageButton = (Button) findViewById(R.id.image_new);
        final Button barcodeButton = (Button) findViewById(R.id.barcode_new);
        final EditText descriptionEditText = (EditText) findViewById(R.id.description_new);

        if (context.equals("editProduct")) {
            setTitle(R.string.edit_item);

            key = bundle.getInt("key");

            Product product = dbHandler.getProduct(key, table);

            name.setText(product.getmName());
            quantity.setText(Integer.toString(product.getmQuantity()));
            descriptionEditText.setText(product.getmDescription());
            barcodeEditText.setText(product.getmUPC());

            int spinnerPos = spinnerAdapter.getPosition(product.getmUnits());
            unitsSpinner.setSelection(spinnerPos);
            newProductButton.setText(R.string.submit_changes);
            imageChanged = false;
        }


        barcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // launch barcode activity.
                int rc = ActivityCompat.checkSelfPermission(ProductEditActivity.this, Manifest.permission.CAMERA);
                if (rc == PackageManager.PERMISSION_GRANTED) {
                    Intent barcodeScanIntent = new Intent(ProductEditActivity.this, BarcodeCaptureActivity.class);
                    barcodeScanIntent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                    barcodeScanIntent.putExtra(BarcodeCaptureActivity.UseFlash, false);
                    if (barcodeScanIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(barcodeScanIntent, RC_BARCODE_CAPTURE);
                    }
                } else {
                    requestCameraPermission();
                }

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check for the camera permission before accessing the camera.  If the
                // permission is not granted yet, request permission.
                int rc = ActivityCompat.checkSelfPermission(ProductEditActivity.this, Manifest.permission.CAMERA);
                if (rc == PackageManager.PERMISSION_GRANTED) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                } else {
                    requestCameraPermission();
                }

            }
        });
        // launch barcode activity.

        // Set a click listener on the search button view
        newProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameStr, unitStr;
                int quant;
                String barcode;
                nameStr = name.getText().toString();
                unitStr = unitsSpinner.getSelectedItem().toString();
                String description;

                // Ensure EditTexts aren't empty
                if ((nameStr.isEmpty()) || (quantity.getText().toString().length() < 1)) {
                    Toast.makeText(ProductEditActivity.this, getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
                } else if (quantity.getText().toString().length() > 9) {
                    Toast.makeText(ProductEditActivity.this, getString(R.string.invalid_quantity), Toast.LENGTH_SHORT).show();
                } else {

                    quant = Integer.parseInt(quantity.getText().toString());
                    barcode = barcodeEditText.getText().toString();

                    description = descriptionEditText.getText().toString();
                    if (description.length() < 1) {
                        description = null;
                    }

                    if(barcodeEditText.getText().toString().length() < 1){
                        barcode = null;
                    }

                    // Convert imageBitmap to byteArray for storage in database
                    imageByteArray = BitMapUtility.getBytes(imageBitmap);
                    Intent returnIntent = getIntent();

                    if (context.equals("newProduct")) {
                        dbHandler.addProduct(nameStr, barcode, imageByteArray, 0, quant, unitStr,
                                description, table, null);
                    } else {
                        int key = getIntent().getExtras().getInt("key");
                        dbHandler.updateProductValues(nameStr, barcode, imageByteArray, 0, quant,
                                unitStr, description, table, key);
                        returnIntent.putExtra("key", key);
                    }
                    dbHandler.close();
                    setResult(Activity.RESULT_OK, returnIntent);
                    Log.v("ProductEditActivity", "Done");
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    EditText barcodeText = (EditText) findViewById(R.id.barcode);
                    barcodeText.setText(barcode.displayValue);
                } else {
                    Toast.makeText(ProductEditActivity.this, "No barcode captured", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ProductEditActivity.this, "Unable to read barcode", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{android.Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RC_HANDLE_CAMERA_PERM:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    Toast.makeText(ProductEditActivity.this, R.string.no_camera_permission, Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}
