package barqsoft.footballscores;

import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;

/**
 * The ItemChoiceManager class keeps track of which positions have been selected.  Note that it
 * doesn't take advantage of new adapter features to track changes in the underlying data.
 */
public class ItemChoiceManager {

    private final String LOG_TAG = ItemChoiceManager.class.getSimpleName();
    private int mChoiceMode;

    private RecyclerView.Adapter mAdapter;

    /**
     * Running state of which positions are currently checked
     */
    SparseBooleanArray mCheckStates = new SparseBooleanArray();

    /**
     * Running state of which IDs are currently checked.
     * If there is a value for a given key, the checked state for that ID is true
     * and the value holds the last known position in the adapter for that id.
     */
    LongSparseArray<Integer> mCheckedIdStates = new LongSparseArray<Integer>();

    public ItemChoiceManager(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
    }

    /**
     * Defines the choice behavior for the RecyclerView. By default, RecyclerViewChoiceMode does
     * not have any choice behavior (AbsListView.CHOICE_MODE_NONE). By setting the choiceMode to
     * AbsListView.CHOICE_MODE_SINGLE, the RecyclerView allows up to one item to  be in a
     * chosen state.
     *
     * @param choiceMode One of AbsListView.CHOICE_MODE_NONE, AbsListView.CHOICE_MODE_SINGLE
     */
    public void setChoiceMode(int choiceMode) {
        if (mChoiceMode != choiceMode) {
            mChoiceMode = choiceMode;
            clearSelections();
        }
    }

    void clearSelections() {
        mCheckStates.clear();
        mCheckedIdStates.clear();
    }

}
