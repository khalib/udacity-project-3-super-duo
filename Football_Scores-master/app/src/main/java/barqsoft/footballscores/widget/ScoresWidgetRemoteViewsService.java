package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainScreenFragment;
import barqsoft.footballscores.R;

/**
 * RemoteViewsService controlling the data being shown in the scrollable scores detail widget.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ScoresWidgetRemoteViewsService extends RemoteViewsService {

    private final String LOG_TAG = ScoresWidgetRemoteViewsService.class.getSimpleName();

    private static final String[] SCORE_COLUMNS = {
            DatabaseContract.SCORES_TABLE + "." + DatabaseContract.ScoreEntry._ID,
            DatabaseContract.ScoreEntry.LEAGUE_COL,
            DatabaseContract.ScoreEntry.DATE_COL,
            DatabaseContract.ScoreEntry.TIME_COL,
            DatabaseContract.ScoreEntry.HOME_COL,
            DatabaseContract.ScoreEntry.AWAY_COL,
            DatabaseContract.ScoreEntry.HOME_GOALS_COL,
            DatabaseContract.ScoreEntry.AWAY_GOALS_COL,
            DatabaseContract.ScoreEntry.MATCH_ID,
            DatabaseContract.ScoreEntry.MATCH_DAY
    };

    static final int INDEX_SCORE_ID = 0;
    static final int INDEX_SCORE_LEAGUE = 1;
    static final int INDEX_SCORE_DATE = 2;
    static final int INDEX_SCORE_TIME = 3;
    static final int INDEX_SCORE_HOME_TEAM = 4;
    static final int INDEX_SCORE_AWAY_TEAM = 5;
    static final int INDEX_SCORE_HOME_GOALS = 6;
    static final int INDEX_SCORE_AWAY_GOALS = 7;
    static final int INDEX_SCORE_MATCH_ID = 8;
    static final int INDEX_SCORE_MATCH_DAY = 9;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                Log.v(LOG_TAG, "===== onCreate()");
            }

            @Override
            public void onDataSetChanged() {
                Log.v(LOG_TAG, "===== onDataSetChanged()");

                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();

                // Get today's date.
                Date scoreDate = new Date(System.currentTimeMillis());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String[] args = new String[1];
                args[0] = dateFormat.format(scoreDate);

                Log.v(LOG_TAG, "scoreDate: " + scoreDate.toString());
                Log.v(LOG_TAG, "dateFormat.format(scoreDate): " + dateFormat.format(scoreDate));

                Uri scoresByDateUri = DatabaseContract.ScoreEntry.buildScoreWithDate();
                data = getContentResolver().query(
                        scoresByDateUri,
                        SCORE_COLUMNS,
                        null,
                        args,
                        DatabaseContract.ScoreEntry.DATE_COL + " ASC");

                Log.v(LOG_TAG, "DatabaseContract.ScoreEntry.DATE_COL: " + DatabaseContract.ScoreEntry.DATE_COL);
                Log.v(LOG_TAG, "getCount(): " + Integer.toString(data.getCount()));

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                Log.v(LOG_TAG, "===== onDestroy()");

                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                Log.v(LOG_TAG, "===== getCount()");
                int count = data == null ? 0 : data.getCount();
                Log.v(LOG_TAG, "count: " + Integer.toString(count));

                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                Log.v(LOG_TAG, "===== getViewAt()");
                Log.v(LOG_TAG, "position: " + position);
                Log.v(LOG_TAG, "position == AdapterView.INVALID_POSITION: " + Boolean.toString(position == AdapterView.INVALID_POSITION));
                Log.v(LOG_TAG, "data == null: " + Boolean.toString(data == null));
                Log.v(LOG_TAG, "data.moveToPosition(position): " + Boolean.toString(data.moveToPosition(position)));

//                if (position == AdapterView.INVALID_POSITION ||
//                        data == null || data.moveToPosition(position)) {
//                    return null;
//                }

                Log.v(LOG_TAG, "RemoteViews RemoteViews RemoteViews RemoteViews RemoteViews RemoteViews ");
                RemoteViews remoteViews = new RemoteViews(getPackageName(),
                        R.layout.widget_collection_list_item);

                remoteViews.setTextViewText(R.id.widget_test_textview, "yo yo yo");

                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                Log.v(LOG_TAG, "===== getLoadingView()");

                return new RemoteViews(getPackageName(), R.layout.widget_collection_list_item);
            }

            @Override
            public int getViewTypeCount() {
                Log.v(LOG_TAG, "===== getViewTypeCount()");

                return 1;
            }

            @Override
            public long getItemId(int position) {
                Log.v(LOG_TAG, "===== getItemId()");

                if (data.moveToPosition(position)) {
                    return data.getLong(INDEX_SCORE_ID);
                }

                return position;
            }

            @Override
            public boolean hasStableIds() {
                Log.v(LOG_TAG, "===== hasStableIds()");

                return true;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                Log.v(LOG_TAG, "===== setRemoteContentDescription()");

                views.setContentDescription(R.id.widget_icon, description);
            }
        };
    }

}
