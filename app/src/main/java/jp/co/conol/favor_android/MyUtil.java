package jp.co.conol.favor_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;


import java.util.Calendar;
import java.util.Date;

/**
 * Created by Masafumi_Ito on 2017/09/28.
 */

@SuppressWarnings("ALL")
public class MyUtil {

    public static class SharedPref {

        public static void save(Context context, String key, String objString) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            pref.edit().putString(key ,objString).apply();
        }

        public static String get(Context context, String key) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return pref.getString(key, null);
        }
    }

    public static class Network {

        /**
         * ネットワークに接続されているかをチェック
         * @param context
         * @return 接続：true / 未接続：false
         */
        public static boolean isConnected(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();

            return info != null && info.isConnected();
        }
    }

    public static class Transform {

        /**
         * Date型 -> Calendar型
         * @param date
         * @return calendar
         */
        public static Calendar dateToCalender(Date date){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        }

        /**
         * Calendar型 -> Date型
         * @param calendar
         * @return date
         */
        public static Date calendarTodate(Calendar calendar){
            Date date = calendar.getTime();
            return date;
        }
    }
}
