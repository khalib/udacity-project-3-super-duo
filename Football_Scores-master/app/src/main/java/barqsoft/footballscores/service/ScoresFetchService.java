package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class ScoresFetchService extends IntentService {

    private final String LOG_TAG = ScoresFetchService.class.getSimpleName();

    final String BASE_URL = "http://api.football-data.org/alpha/fixtures"; //Base URL
    final String QUERY_TIME_FRAME = "timeFrame"; //Time Frame parameter to determine days
    //final String QUERY_MATCH_DAY = "matchday";

    private Handler mHandler;

    // This set of league codes is for the 2015/2016 season. In fall of 2016, they will need to
    // be updated. Feel free to use the codes
    public static final int BUNDESLIGA1 = 394;
    public static final int BUNDESLIGA2 = 395;
    public static final int BUNDESLIGA3 = 403;
    public static final int LIGUE1 = 396;
    public static final int LIGUE2 = 397;
    public static final int PREMIER_LEAGUE = 398;
    public static final int PRIMERA_DIVISION = 399;
    public static final int SEGUNDA_DIVISION = 400;
    public static final int SERIE_A = 401;
    public static final int PRIMERA_LIGA = 402;
    public static final int EREDIVISIE = 404;
    public static final int CHAMPIONS_LEAGUE = 362;

    public ScoresFetchService() {
        super("ScoresFetchService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Obtain a handler from the main thread.
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(LOG_TAG, "===== onHandleIntent()");

        if (Utilities.isNetworkAvailable(this)) {
            Log.v(LOG_TAG, "YES network");
            getData("n2");
            getData("p2");
        } else {
            Log.v(LOG_TAG, "NO network");

            // Notify the user in the main thread that there is no connection.
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(LOG_TAG, "ERROR: No internet connection available");
                    Toast.makeText(ScoresFetchService.this, R.string.network_connection_error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getData(String timeFrame) {
        // Creating fetch URL
        Uri fetch_build = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(QUERY_TIME_FRAME, timeFrame).build();

        Log.v(LOG_TAG, "The url we are looking at is: " + fetch_build.toString());

        HttpURLConnection mConnectioon = null;
        BufferedReader reader = null;
        String jsonData = null;

        // Opening Connection
        try {
            URL fetch = new URL(fetch_build.toString());
            mConnectioon = (HttpURLConnection) fetch.openConnection();
            mConnectioon.setRequestMethod("GET");
            mConnectioon.addRequestProperty("X-Auth-Token", getString(R.string.api_key));
            mConnectioon.connect();

            // Read the input stream into a String
            InputStream inputStream = mConnectioon.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }

            jsonData = buffer.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG,"Exception here" + e.getMessage());
        } finally {
            if (mConnectioon != null) {
                mConnectioon.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG,"Error Closing Stream");
                }
            }
        }

        try {
            if (jsonData != null) {
                // This bit is to check if the data contains any matches. If not, we call processJson on the dummy data
                JSONArray matches = new JSONObject(jsonData).getJSONArray("fixtures");
                if (matches.length() == 0) {
                    Log.v(LOG_TAG, "Getting dummy data: " + getString(R.string.dummy_data));

                    // if there is no data, call the function on dummy data
                    // this is expected behavior during the off season.
                    processJSONdata(getString(R.string.dummy_data), getApplicationContext(), false);
                    return;
                }

                processJSONdata(jsonData, getApplicationContext(), true);
            } else {
                //Could not Connect
                Log.d(LOG_TAG, "Could not connect to server.");
            }
        } catch(Exception e) {
            Log.e(LOG_TAG,e.getMessage());
        }
    }

    private void processJSONdata (String JSONdata,Context mContext, boolean isReal) {
        // JSON data
        final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
        final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/";
        final String FIXTURES = "fixtures";
        final String LINKS = "_links";
        final String SOCCER_SEASON = "soccerseason";
        final String SELF = "self";
        final String MATCH_DATE = "date";
        final String HOME_TEAM = "homeTeamName";
        final String AWAY_TEAM = "awayTeamName";
        final String RESULT = "result";
        final String HOME_GOALS = "goalsHomeTeam";
        final String AWAY_GOALS = "goalsAwayTeam";
        final String MATCH_DAY = "matchday";

        // Match data
        int league = -1;
        String mDate = null;
        String mTime = null;
        String home = null;
        String away = null;
        String homeGoals = null;
        String awayGoals = null;
        String matchId = null;
        String matchDay = null;

        try {
            JSONArray matches = new JSONObject(JSONdata).getJSONArray(FIXTURES);

            // ContentValues to be inserted
            Vector<ContentValues> values = new Vector <ContentValues> (matches.length());

            for (int i = 0; i < matches.length(); i++) {
                JSONObject matchData = matches.getJSONObject(i);
                String href = matchData.getJSONObject(LINKS).getJSONObject(SOCCER_SEASON).getString("href");
                league = Integer.parseInt(href.replace(SEASON_LINK, ""));

                // This if statement controls which leagues we're interested in the data from.
                // add leagues here in order to have them be added to the DB.
                // If you are finding no data in the app, check that this contains all the leagues.
                // If it doesn't, that can cause an empty DB, bypassing the dummy data routine.
                if (league == PREMIER_LEAGUE ||
                        league == SERIE_A ||
                        league == BUNDESLIGA1 ||
                        league == BUNDESLIGA2 ||
                        league == BUNDESLIGA3 ||
                        league == SEGUNDA_DIVISION ||
                        league == LIGUE1 ||
                        league == LIGUE2 ||
                        league == PRIMERA_LIGA ||
                        league == EREDIVISIE ||
                        league == PRIMERA_DIVISION) {
                    matchId = matchData.getJSONObject(LINKS).getJSONObject(SELF).getString("href");
                    matchId = matchId.replace(MATCH_LINK, "");

                    // This if statement changes the match ID of the dummy data so that it all goes into the database
                    if (!isReal) {
                        matchId = matchId + Integer.toString(i);
                    }

                    mDate = matchData.getString(MATCH_DATE);
                    mTime = mDate.substring(mDate.indexOf("T") + 1, mDate.indexOf("Z"));
                    mDate = mDate.substring(0,mDate.indexOf("T"));
                    SimpleDateFormat matchDate = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                    matchDate.setTimeZone(TimeZone.getTimeZone("UTC"));

                    try {
                        Date parsedDate = matchDate.parse(mDate+mTime);
                        SimpleDateFormat new_date = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
                        new_date.setTimeZone(TimeZone.getDefault());
                        mDate = new_date.format(parsedDate);
                        mTime = mDate.substring(mDate.indexOf(":") + 1);
                        mDate = mDate.substring(0,mDate.indexOf(":"));

                        // This if statement changes the dummy data's date to match our current date range.
                        if (!isReal) {
                            Date fragmentdate = new Date(System.currentTimeMillis()+((i-2)*86400000));
                            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
                            mDate=mformat.format(fragmentdate);
                        }
                    }
                    catch (Exception e) {
                        Log.d(LOG_TAG, "error here!");
                        Log.e(LOG_TAG,e.getMessage());
                    }

                    home = matchData.getString(HOME_TEAM);
                    away = matchData.getString(AWAY_TEAM);
                    homeGoals = matchData.getJSONObject(RESULT).getString(HOME_GOALS);
                    awayGoals = matchData.getJSONObject(RESULT).getString(AWAY_GOALS);
                    matchDay = matchData.getString(MATCH_DAY);

                    ContentValues matchValues = new ContentValues();
                    matchValues.put(DatabaseContract.ScoreEntry.MATCH_ID, matchId);
                    matchValues.put(DatabaseContract.ScoreEntry.DATE_COL, mDate);
                    matchValues.put(DatabaseContract.ScoreEntry.TIME_COL, mTime);
                    matchValues.put(DatabaseContract.ScoreEntry.HOME_COL, home);
                    matchValues.put(DatabaseContract.ScoreEntry.AWAY_COL, away);
                    matchValues.put(DatabaseContract.ScoreEntry.HOME_GOALS_COL, homeGoals);
                    matchValues.put(DatabaseContract.ScoreEntry.AWAY_GOALS_COL, awayGoals);
                    matchValues.put(DatabaseContract.ScoreEntry.LEAGUE_COL, league);
                    matchValues.put(DatabaseContract.ScoreEntry.MATCH_DAY, matchDay);

                    values.add(matchValues);
                }
            }

            int insertedData = 0;
            ContentValues[] insert_data = new ContentValues[values.size()];
            values.toArray(insert_data);
            insertedData = mContext.getContentResolver().bulkInsert(
                    DatabaseContract.BASE_CONTENT_URI,insert_data);

            //Log.v(LOG_TAG,"Succesfully Inserted : " + String.valueOf(insertedData));
        } catch (JSONException e) {
            Log.e(LOG_TAG,e.getMessage());
        }
    }

}
