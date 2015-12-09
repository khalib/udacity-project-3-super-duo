package it.jaschke.alexandria;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_barcode_reader);

        mCameraPreview = (CameraPreview) findViewById(R.id.barcode_reader_surfaceview);

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
        Context context = getApplicationContext();
    }

    /**
     * Requests for the camera permission.
     */
    private void requestCameraPermission() {

    }

}
