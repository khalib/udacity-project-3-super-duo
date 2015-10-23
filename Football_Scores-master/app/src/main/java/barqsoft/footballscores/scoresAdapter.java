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

    public ScoresAdapter(Context context, ScoresAdapterOnClickHandler clickHandler, View emptyView, int choiceMode)
    {
        mContext = context;
        mClickHandler = clickHandler;
        mEmptyView = emptyView;
        mItemChoiceManager = new ItemChoiceManager(this);
        mItemChoiceManager.setChoiceMode(choiceMode);
    }

    public static interface ScoresAdapterOnClickHandler {
        void onClick(Long date, ScoresAdapterOnClickHandler vh);
    }

    @Override
    public ScoresViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scores_list_item, parent, false);
            view.setFocusable(true);

            return new ScoresViewHolder(view);
        } else {
            throw new RuntimeException("Not bound by RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(ScoresViewHolder scoresViewHolder, int position) {
        mCursor.moveToPosition(position);

        // Load views.
        scoresViewHolder.homeName.setText(mCursor.getString(COL_HOME));
        scoresViewHolder.awayName.setText(mCursor.getString(COL_AWAY));
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) {
            return 0;
        }

        return mCursor.getCount();
    }

    /*
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ScoresViewHolder mHolder = new ScoresViewHolder(mItem);
        mItem.setTag(mHolder);
        //Log.v(FetchScoreTask.LOG_TAG,"new View inflated");
        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor)
    {
        final ScoresViewHolder scoresViewHolder = (ScoresViewHolder) view.getTag();

        // Load views.
        scoresViewHolder.homeName.setText(cursor.getString(COL_HOME));
        scoresViewHolder.awayName.setText(cursor.getString(COL_AWAY));
        scoresViewHolder.date.setText(cursor.getString(COL_MATCHTIME));
        scoresViewHolder.score.setText(Utilies.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));
        scoresViewHolder.matchId = cursor.getDouble(COL_ID);

        scoresViewHolder.homeCrest.setImageResource(Utilies.getTeamCrestByTeamName(
                cursor.getString(COL_HOME)));
        scoresViewHolder.homeCrest.setContentDescription(context.getString(R.string.a11y_home_team_image,
                cursor.getString(COL_HOME)));

        scoresViewHolder.awayCrest.setImageResource(Utilies.getTeamCrestByTeamName(
                cursor.getString(COL_AWAY)));
        scoresViewHolder.awayCrest.setContentDescription(context.getString(R.string.a11y_away_team_image,
                cursor.getString(COL_AWAY)));

        //Log.v(FetchScoreTask.LOG_TAG,mHolder.home_name.getText() + " Vs. " + mHolder.away_name.getText() +" id " + String.valueOf(mHolder.match_id));
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(detailMatchId));

        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);

        if (scoresViewHolder.matchId == detailMatchId) {
            //Log.v(FetchScoreTask.LOG_TAG,"will insert extraView");

            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            TextView matchDay = (TextView) v.findViewById(R.id.matchday_textview);
            matchDay.setText(Utilies.getMatchDay(cursor.getInt(COL_MATCHDAY),
                    cursor.getInt(COL_LEAGUE)));

            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utilies.getLeague(cursor.getInt(COL_LEAGUE)));

            Button shareButton = (Button) v.findViewById(R.id.share_button);
            shareButton.setContentDescription(context.getString(R.string.a11y_share_button));
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    // add Share Action.
                    String shareText = context.getString(R.string.share_message,
                            scoresViewHolder.homeName.getText(),
                            scoresViewHolder.score.getText(),
                            scoresViewHolder.awayName.getText());

                    context.startActivity(createShareIntent(shareText));
                }
            });
        } else {
            container.removeAllViews();
        }

    }
    */

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
