package barqsoft.footballscores;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final String LOG_TAG = ScoresViewHolder.class.getSimpleName();
    private ScoresAdapter.ScoresAdapterOnClickHandler mOnClickHandler;

    public TextView homeName;
    public TextView awayName;
    public TextView score;
    public TextView date;
    public ImageView homeCrest;
    public ImageView awayCrest;
    public FrameLayout shareFrame;
    public double matchId;

    public ScoresViewHolder(View view, ScoresAdapter.ScoresAdapterOnClickHandler onClickHandler) {
        super(view);

        homeName = (TextView) view.findViewById(R.id.home_name);
        awayName = (TextView) view.findViewById(R.id.away_name);
        score = (TextView) view.findViewById(R.id.score_textview);
        date = (TextView) view.findViewById(R.id.data_textview);
        homeCrest = (ImageView) view.findViewById(R.id.home_crest);
        awayCrest = (ImageView) view.findViewById(R.id.away_crest);
        shareFrame = (FrameLayout) view.findViewById(R.id.details_fragment_container);

        mOnClickHandler = onClickHandler;

        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.v(LOG_TAG, "ScoresViewHolder: CLICK");
        mOnClickHandler.onClick(Double.valueOf("12345"), this);
    }
}
