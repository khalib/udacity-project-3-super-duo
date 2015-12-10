package it.jaschke.alexandria.CameraPreview;

import android.Manifest;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.RequiresPermission;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.common.images.Size;

import java.io.IOException;


/**
 * Camera preview surface which displays a graphic overlay of barcodes being scanned.
 *
 * Created by lisah0 on 2012-02-24
 */
public class CameraPreview extends ViewGroup {

    private final String LOG_TAG = CameraPreview.class.getSimpleName();

    private Context mContext;
    private SurfaceView mSurfaceView;
    private boolean mStartRequested;
    private boolean mSurfaceAvailable;
    private CameraSource mCameraSource;
    private GraphicOverlay mOverlay;

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        mStartRequested = false;
        mSurfaceAvailable = false;
        mSurfaceView = new SurfaceView(context);
        mSurfaceView.getHolder().addCallback(new SurfaceCallback());
        addView(mSurfaceView);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.v(LOG_TAG, "===== onLayout()");

        int width = 320;
        int height = 240;
        if (mCameraSource != null) {
            Size size = mCameraSource.getPreviewSize();
            if (size != null) {
                width = size.getWidth();
                height = size.getHeight();
            }
        }

        // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
        if (isPortraitMode()) {
            int tmp = width;
            //noinspection SuspiciousNameCombination
            width = height;
            height = tmp;
        }

        final int layoutWidth = r - l;
        final int layoutHeight = b - t;

        // Computes height and width for potentially doing fit width.
        int childWidth = layoutWidth;
        int childHeight = (int)(((float) layoutWidth / (float) width) * height);

        // If height is too tall using fit width, does fit height instead.
        if (childHeight > layoutHeight) {
            childHeight = layoutHeight;
            childWidth = (int)(((float) layoutHeight / (float) height) * width);
        }

        for (int i = 0; i < getChildCount(); ++i) {
            getChildAt(i).layout(0, 0, childWidth, childHeight);
        }

        try {
            startIfReady();
        } catch (SecurityException se) {
            Log.e(LOG_TAG,"Do not have permission to start the camera", se);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not start camera source.", e);
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.v(LOG_TAG, "===== surfaceCreated()");

            mSurfaceAvailable = true;
            try {
                Log.v(LOG_TAG, "===== startIfReady()");
                startIfReady();
            } catch (SecurityException se) {
                Log.e(LOG_TAG,"Do not have permission to start the camera", se);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Could not start camera source.", e);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.v(LOG_TAG, "===== surfaceChanged()");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.v(LOG_TAG, "===== surfaceDestroyed()");

            mSurfaceAvailable = false;
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void start(CameraSource cameraSource) throws IOException, SecurityException {
        Log.v(LOG_TAG, "===== start() 1");

        if (cameraSource == null) {
            stop();
        }

        mCameraSource = cameraSource;

        if (mCameraSource != null) {
            mStartRequested = true;
            startIfReady();
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void start(CameraSource cameraSource, GraphicOverlay overlay) throws IOException, SecurityException {
        Log.v(LOG_TAG, "===== start() 2");

        mOverlay = overlay;
        start(cameraSource);
    }

    public void stop() {
        Log.v(LOG_TAG, "===== stop()");

        if (mCameraSource != null) {
            mCameraSource.stop();
        }
    }

    public void release() {
        Log.v(LOG_TAG, "===== release()");

        if (mCameraSource != null) {
            mCameraSource.release();
            mCameraSource = null;
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    private void startIfReady() throws IOException, SecurityException {
        Log.v(LOG_TAG, "===== startIfReady()");

        if (mStartRequested && mSurfaceAvailable) {
            mCameraSource.start(mSurfaceView.getHolder());
            if (mOverlay != null) {
                Size size = mCameraSource.getPreviewSize();
                int min = Math.min(size.getWidth(), size.getHeight());
                int max = Math.max(size.getWidth(), size.getHeight());
                if (isPortraitMode()) {
                    // Swap width and height sizes when in portrait, since it will be rotated by
                    // 90 degrees
                    mOverlay.setCameraInfo(min, max, mCameraSource.getCameraFacing());
                } else {
                    mOverlay.setCameraInfo(max, min, mCameraSource.getCameraFacing());
                }
                mOverlay.clear();
            }
            mStartRequested = false;
        }
    }

    private boolean isPortraitMode() {
        Log.v(LOG_TAG, "===== isPortraitMode()");

        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }

        Log.d(LOG_TAG, "isPortraitMode returning false by default");
        return false;
    }

}
