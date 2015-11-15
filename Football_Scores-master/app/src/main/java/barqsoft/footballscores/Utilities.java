package barqsoft.footballscores;

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
    public static String getLeague(int leagueNum) {
        switch (leagueNum) {
            case ScoresFetchService.SERIE_A:
                return "Seria A";

            case ScoresFetchService.PREMIER_LEAGUE:
                return "Premier League";

            case ScoresFetchService.CHAMPIONS_LEAGUE:
                return "UEFA Champions League";

            case ScoresFetchService.PRIMERA_DIVISION:
                return "Primera Division";

            case ScoresFetchService.BUNDESLIGA1:
                return "Bundesliga 1";

            case ScoresFetchService.BUNDESLIGA2:
                return "Bundesliga 2";

            case ScoresFetchService.BUNDESLIGA3:
                return "Bundesliga 3";

            case ScoresFetchService.SEGUNDA_DIVISION:
                return "Segunda Division";

            case ScoresFetchService.LIGUE1:
                return "France Ligue 1";

            case ScoresFetchService.LIGUE2:
                return "France Ligue 2";

            case ScoresFetchService.PRIMERA_LIGA:
                return "Liga BBVA";

            case ScoresFetchService.EREDIVISIE:
                return "Eredivisie";

            default:
                return "Not known League Please report";
        }
    }

    public static String getMatchDay(int match_day,int leagueNum) {
        if (leagueNum == ScoresFetchService.CHAMPIONS_LEAGUE) {
            if (match_day <= 6) {
                return "Group Stages, Matchday : 6";
            } else if (match_day == 7 || match_day == 8) {
                return "First Knockout round";
            } else if (match_day == 9 || match_day == 10) {
                return "QuarterFinal";
            } else if(match_day == 11 || match_day == 12) {
                return "SemiFinal";
            } else {
                return "Final";
            }
        } else {
            return "Matchday : " + String.valueOf(match_day);
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
        if (teamname==null) {
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

}
