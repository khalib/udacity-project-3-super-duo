package barqsoft.footballscores;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import barqsoft.footballscores.service.ScoresFetchService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MainScreenFragment.class.getSimpleName();

    public ScoresAdapter mScoresAdapter;
    private RecyclerView mRecyclerView;
    private int mChoiceMode;
    public static final int SCORES_LOADER = 0;
    private String[] fragmentdate = new String[1];
    private int lastSelectedItem = -1;

    public MainScreenFragment() {

    }

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);

        mChoiceMode = AbsListView.CHOICE_MODE_NONE;
    }

    private void update_scores() {
        Intent service_start = new Intent(getActivity(), ScoresFetchService.class);
        getActivity().startService(service_start);
    }

    public void setFragmentDate(String date) {
        fragmentdate[0] = date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        update_scores();

        // Define views.
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//        final ListView scoreList = (ListView) rootView.findViewById(R.id.scores_list);
        View emptyView = rootView.findViewById(R.id.recyclerview_scores_empty);

        // Get a reference to the RecyclerView, and attach this adapter to it.
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_scores);

        // Set the layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // The ScoresAdapter will take data from a source and use it to populate the RecyclerView
        // it's attached to.
        mScoresAdapter = new ScoresAdapter(getActivity(), new ScoresAdapter.ScoresAdapterOnClickHandler() {
            @Override
            public void onClick(Double matchId, ScoresViewHolder viewHolder) {
                Log.v(LOG_TAG, "MainScreenFragment: CLICK");
                mScoresAdapter.detailMatchId = viewHolder.matchId;
                MainActivity.selectedMatchId = (int) viewHolder.matchId;
                mScoresAdapter.notifyDataSetChanged();
            }
        }, emptyView, mChoiceMode);

        mScoresAdapter.detailMatchId = MainActivity.selectedMatchId;

        getLoaderManager().initLoader(SCORES_LOADER, null, this);

        // specify an adapter.
        mRecyclerView.setAdapter(mScoresAdapter);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), DatabaseContract.ScoreEntry.buildScoreWithDate(),
                null, null, fragmentdate, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            i++;
            cursor.moveToNext();
        }

        mScoresAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mScoresAdapter.swapCursor(null);
    }

}
