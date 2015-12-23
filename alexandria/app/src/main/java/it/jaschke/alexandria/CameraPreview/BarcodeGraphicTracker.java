package it.jaschke.alexandria.CameraPreview;

import android.util.Log;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Handles barcode scanning from a camera source.  A graphical overlay is added to display where a
 * barcode is scanned.
 */
public class BarcodeGraphicTracker extends Tracker<Barcode> {

    private final String LOG_TAG = BarcodeGraphicTracker.class.getSimpleName();

    private GraphicOverlay<BarcodeGraphic> mOverlay;
    private BarcodeGraphic mGraphic;
    private OnNewItemListener mOnNewItemListener;

    BarcodeGraphicTracker(GraphicOverlay<BarcodeGraphic> overlay, BarcodeGraphic graphic,
                          OnNewItemListener listener) {
        mOverlay = overlay;
        mGraphic = graphic;
        mOnNewItemListener = listener;
    }

    /**
     * Callback interface for when a barcode is scanned.
     */
    public interface OnNewItemListener {
        void onNewItem(Barcode item);
    }

    /**
     * Callback when a new barcode is found.
     */
    @Override
    public void onNewItem(int id, Barcode item) {
        // Listener callback.
        if (mOnNewItemListener != null) {
            mOnNewItemListener.onNewItem(item);
        }

        mGraphic.setId(id);
    }

    /**
     * Update the position/characteristics of the item within the overlay.
     */
    @Override
    public void onUpdate(Detector.Detections<Barcode> detectionResults, Barcode item) {
        mOverlay.add(mGraphic);
        mGraphic.updateItem(item);
    }

    /**
     * Hide the graphic overlay if no barcode is found on the surface.
     */
    @Override
    public void onMissing(Detector.Detections<Barcode> detectionResults) {
        mOverlay.remove(mGraphic);
    }

    /**
     * Callback when a barcode item is gone from the surface.
     */
    @Override
    public void onDone() {
        mOverlay.remove(mGraphic);
    }

}
