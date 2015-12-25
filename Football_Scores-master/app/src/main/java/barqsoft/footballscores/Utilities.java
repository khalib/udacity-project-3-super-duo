package barqsoft.footballscores;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import barqsoft.footballscores.service.ScoresFetchService;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilities {

    private static final String LOG_TAG = Utilities.class.getSimpleName();

    /**
     * Get the name of the league by ID.
     *
     * @param leagueNum
     * @return
     */
    public static String getLeague(Context context, int leagueNum) {
        switch (leagueNum) {
            case ScoresFetchService.SERIE_A:
                return context.getString(R.string.league_seria_a);

            case ScoresFetchService.PREMIER_LEAGUE:
                return context.getString(R.string.league_premier_league);

            case ScoresFetchService.CHAMPIONS_LEAGUE:
                return context.getString(R.string.league_uefa_champions_league);

            case ScoresFetchService.PRIMERA_DIVISION:
                return context.getString(R.string.league_primera_division);

            case ScoresFetchService.BUNDESLIGA1:
                return context.getString(R.string.league_bundesliga_1);

            case ScoresFetchService.BUNDESLIGA2:
                return context.getString(R.string.league_bundesliga_2);

            case ScoresFetchService.BUNDESLIGA3:
                return context.getString(R.string.league_bundesliga_3);

            case ScoresFetchService.SEGUNDA_DIVISION:
                return context.getString(R.string.league_segunda_division);

            case ScoresFetchService.LIGUE1:
                return context.getString(R.string.league_france_ligue_1);

            case ScoresFetchService.LIGUE2:
                return context.getString(R.string.league_france_ligue_2);

            case ScoresFetchService.PRIMERA_LIGA:
                return context.getString(R.string.league_liga_bbva);

            case ScoresFetchService.EREDIVISIE:
                return context.getString(R.string.league_eredivisie);

            default:
                return context.getString(R.string.league_not_found);
        }
    }

    public static String getMatchDay(Context context, int match_day,int leagueNum) {
        if (leagueNum == ScoresFetchService.CHAMPIONS_LEAGUE) {
            if (match_day <= 6) {
                return context.getString(R.string.league_match_day_group_stages);
            } else if (match_day == 7 || match_day == 8) {
                return context.getString(R.string.league_match_day_first_knockout_round);
            } else if (match_day == 9 || match_day == 10) {
                return context.getString(R.string.league_match_day_querter_final);
            } else if(match_day == 11 || match_day == 12) {
                return context.getString(R.string.league_match_day_semi_final);
            } else {
                return context.getString(R.string.league_match_day_final);
            }
        } else {
            return String.format(context.getString(R.string.league_match_day_default), String.valueOf(match_day));
        }
    }

    public static String getScores(int home_goals,int awaygoals) {
        if (home_goals < 0 || awaygoals < 0) {
            return " - ";
        } else {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName (String teamname) {
        if (teamname == null) {
            return R.drawable.no_icon;
        }

        switch (teamname) {
            // This is the set of icons that are currently in the app.
            // Feel free to find and add more as you go.
            case "Arsenal London FC" :
                return R.drawable.arsenal;

            case "Manchester United FC" :
                return R.drawable.manchester_united;

            case "Swansea City" :
                return R.drawable.swansea_city_afc;

            case "Leicester City" :
                return R.drawable.leicester_city_fc_hd_logo;

            case "Everton FC" :
                return R.drawable.everton_fc_logo1;

            case "West Ham United FC" :
                return R.drawable.west_ham;

            case "Tottenham Hotspur FC" :
                return R.drawable.tottenham_hotspur;

            case "West Bromwich Albion" :
                return R.drawable.west_bromwich_albion_hd_logo;

            case "Sunderland AFC" :
                return R.drawable.sunderland;

            case "Stoke City FC" :
                return R.drawable.stoke_city;

            default:
                return R.drawable.no_icon;
        }
    }

    /**
     * Returns true if the network is available or about to become available.
     *
     * @param c Context used to get the ConnectivityManager
     * @return true if the network is available
     */
    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

}
