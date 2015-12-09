package it.jaschke.alexandria;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.google.android.gms.common.api.CommonStatusCodes;

import it.jaschke.alexandria.CameraPreview.CameraPreview;
import it.jaschke.alexandria.CameraPreview.CameraSource;

/**
 * Created by caleb on 12/3/15.
 */
public final class BarcodeReaderActivity extends AppCompatActivity {

    private final String LOG_TAG = BarcodeReaderActivity.class.getSimpleName();

    private CameraPreview mCameraPreview;
    private CameraSource mCameraSource;
    private SurfaceHolder mSurfaceHolder;
    private GestureDetector mGestureDetector;

    // Intent constants for extra data.
    public static final String BarcodeObject = "Barcode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_barcode_reader);

        mCameraPreview = (CameraPreview) findViewById(R.id.barcode_reader_surfaceview);
        mGestureDetector = new GestureDetector(this, new GestureListener());

        // Check for camera permission.
        int cameraPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            Log.v(LOG_TAG, "Camera permission is GRANTED");

            createCameraSource();
        } else {
            Log.v(LOG_TAG, "Camera permission is NOT GRANTED");

            requestCameraPermission();
        }

    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.v(LOG_TAG, "===== onSingleTapConfirmed()");

            return onSingleTap() || super.onSingleTapConfirmed(e);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isGestureDetector = mGestureDetector.onTouchEvent(event);

        return isGestureDetector || super.onTouchEvent(event);
    }

    /**
     *
     *
     * @return true if the activity is ending.
     */
    private boolean onSingleTap() {
        Intent data = new Intent();
        data.putExtra(BarcodeObject, "9780747532699");
        setResult(CommonStatusCodes.SUCCESS, data);
        finish();

        return true;
    }

    /**
     * Creates and starts the camera to scan for a barcode.
     */
    private void createCameraSource() {
        Context context = getApplicationContext();
    }

    /**
     * Requests for the camera permission.
     */
    private void requestCameraPermission() {

    }

}
