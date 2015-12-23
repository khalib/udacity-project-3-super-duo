package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends RecyclerView.Adapter<ScoresViewHolder> {

    private final String LOG_TAG = ScoresAdapter.class.getSimpleName();

    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;
    public double detailMatchId = 0;

    final private Context mContext;
    final private ScoresAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;
    final private ItemChoiceManager mItemChoiceManager;
    private Cursor mCursor;

    public ScoresAdapter(Context context, ScoresAdapterOnClickHandler clickHandler, View emptyView, int choiceMode) {
        mContext = context;
        mClickHandler = clickHandler;
        mEmptyView = emptyView;
        mItemChoiceManager = new ItemChoiceManager(this);
        mItemChoiceManager.setChoiceMode(choiceMode);
    }

    public static interface ScoresAdapterOnClickHandler {
        void onClick(Double matchId, ScoresViewHolder viewHolder);
    }

    @Override
    public ScoresViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scores_list_item, parent, false);
            view.setFocusable(true);

            return new ScoresViewHolder(view, mClickHandler);
        } else {
            throw new RuntimeException("Not bound by RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(ScoresViewHolder scoresViewHolder, int position) {
        final ScoresViewHolder viewHolder = scoresViewHolder;
        mCursor.moveToPosition(position);

        // Load views.
        viewHolder.homeName.setText(mCursor.getString(COL_HOME));
        viewHolder.awayName.setText(mCursor.getString(COL_AWAY));
        viewHolder.date.setText(mCursor.getString(COL_MATCHTIME));
        viewHolder.score.setText(Utilities.getScores(mCursor.getInt(COL_HOME_GOALS), mCursor.getInt(COL_AWAY_GOALS)));
        viewHolder.matchId = mCursor.getDouble(COL_ID);

        viewHolder.homeCrest.setImageResource(Utilities.getTeamCrestByTeamName(
                mCursor.getString(COL_HOME)));
        viewHolder.homeCrest.setContentDescription(mContext.getString(R.string.a11y_home_team_image,
                mCursor.getString(COL_HOME)));

        viewHolder.awayCrest.setImageResource(Utilities.getTeamCrestByTeamName(
                mCursor.getString(COL_AWAY)));
        viewHolder.awayCrest.setContentDescription(mContext.getString(R.string.a11y_away_team_image,
                mCursor.getString(COL_AWAY)));

        LayoutInflater vi = (LayoutInflater) mContext.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);

        if (viewHolder.matchId == detailMatchId) {
            viewHolder.shareFrame.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            TextView matchDay = (TextView) v.findViewById(R.id.matchday_textview);
            matchDay.setText(Utilities.getMatchDay(mCursor.getInt(COL_MATCHDAY),
                    mCursor.getInt(COL_LEAGUE)));

            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utilities.getLeague(mCursor.getInt(COL_LEAGUE)));

            Button shareButton = (Button) v.findViewById(R.id.share_button);
            shareButton.setContentDescription(mContext.getString(R.string.a11y_share_button));
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    // add Share Action.
                    String shareText = mContext.getString(R.string.share_message,
                            viewHolder.homeName.getText(),
                            viewHolder.score.getText(),
                            viewHolder.awayName.getText());

                    mContext.startActivity(createShareIntent(shareText));
                }
            });
        } else {
            viewHolder.shareFrame.removeAllViews();
        }
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) {
            return 0;
        }

        return mCursor.getCount();
    }

    /**
     *
     *
     * @param newCursor
     */
    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * Creates a share intent.
     *
     * @param shareText - the text to be used when shared.
     * @return the share intent to be initialized in an activity.
     */
    public Intent createShareIntent(String shareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        return shareIntent;
    }

}
