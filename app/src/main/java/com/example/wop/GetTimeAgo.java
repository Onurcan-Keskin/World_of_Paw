package com.example.wop;

import android.app.Application;
import android.content.Context;

public class GetTimeAgo extends Application {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static  String getTimeAgo(long time, Context ctx){
        if (time < 1000000000){
            return null;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0){
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS){
            return "Just now";
        }else if (diff < 2 * MINUTE_MILLIS){
            return "A minute ago";
        }else if (diff < 50 * MINUTE_MILLIS){
            return diff /MINUTE_MILLIS + " Minute ago";
        }else if (diff < 90 * MINUTE_MILLIS){
            return "An hour ago";
        }else if (diff < 24 * HOUR_MILLIS){
            return diff / HOUR_MILLIS + " Hours ago";
        }else if (diff < 48 * HOUR_MILLIS){
            return "Yesterday";
        }else {
            return diff / DAY_MILLIS + " Days ago";
        }
    }
}
