package it.jaschke.alexandria;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import it.jaschke.alexandria.CameraPreview.BarcodeGraphic;
import it.jaschke.alexandria.CameraPreview.BarcodeGraphicTracker;
import it.jaschke.alexandria.CameraPreview.CameraPreview;
import it.jaschke.alexandria.CameraPreview.CameraSource;
import it.jaschke.alexandria.CameraPreview.BarcodeTrackerFactory;
import it.jaschke.alexandria.CameraPreview.GraphicOverlay;

/**
 * Activity for scanning a barcode.
 */
public final class BarcodeReaderActivity extends AppCompatActivity {

    private final String LOG_TAG = BarcodeReaderActivity.class.getSimpleName();

    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private static final boolean CAMERA_SETTINGS_FLASH_MODE = false;
    private static final boolean CAMERA_SETTINGS_FOCUS_MODE = true;
    public static final String BarcodeObject = "Barcode";

    private CameraPreview mCameraPreview;
    private CameraSource mCameraSource;
    private Button mCancelButton;
    private SurfaceHolder mSurfaceHolder;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_barcode_reader);

        mCameraPreview = (CameraPreview) findViewById(R.id.barcode_reader_surfaceview);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.barcode_reader_graphic_overlay);
        mCancelButton = (Button) findViewById(R.id.barcode_reader_cancel_button);

        // Check for camera permission.
        int cameraPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

    }

    /**
     * Creates and starts the camera to scan for a barcode.
     */
    private void createCameraSource() {
        Log.v(LOG_TAG, "===== createCameraSource()");

        Context context = getApplicationContext();

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, new BarcodeGraphicTracker.OnNewItemListener() {
            @Override
            public void onNewItem(Barcode item) {
                // Callback when a new barcode is found.
                Intent data = new Intent();
                data.putExtra(BarcodeObject, item);
                setResult(CommonStatusCodes.SUCCESS, data);
                finish();
            }
        });

        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

        // Checks to see if the barcode detector is installed.
        // On first run, the barcode API installs dependencies.
        if (!barcodeDetector.isOperational()) {
            Log.v(LOG_TAG, "Detector dependencies are not yet available.");

            // Check to see if the device has space to install the barcode scanner dependencies.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(LOG_TAG, getString(R.string.low_storage_error));
            }
        }

        // Build the camera for scanning.
        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1600)
                .setRequestedFps(15.0f);

        // Check that auto focus is available.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(CAMERA_SETTINGS_FOCUS_MODE
                    ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                    : null);
        }

        mCameraSource = builder.setFlashMode(CAMERA_SETTINGS_FLASH_MODE
                ? Camera.Parameters.FLASH_MODE_TORCH
                : null)
                .build();
    }

    /**
     * Requests for the camera permission.
     */
    private void requestCameraPermission() {
        Log.v(LOG_TAG, "===== requestCameraPermission()");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
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

        Snackbar.make(mGraphicOverlay, R.string.camera_permission_error,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok_button, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        Log.v(LOG_TAG, "===== startCameraSource()");

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mCameraPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * Click handler for the cancel button in the view.
     *
     * @param view
     */
    public void cancelBarcodeScan(View view) {
        // Close the activity.
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraPreview != null) {
            mCameraPreview.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraPreview != null) {
            mCameraPreview.release();
        }
    }

}
