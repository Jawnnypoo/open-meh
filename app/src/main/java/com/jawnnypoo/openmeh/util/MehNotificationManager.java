package com.jawnnypoo.openmeh.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.jawnnypoo.openmeh.MainActivity;
import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.data.Deal;
import com.jawnnypoo.openmeh.data.Theme;
import com.jawnnypoo.openmeh.service.MehResponse;

/**
 * Created by Jawn on 4/20/2015.
 */
public class MehNotificationManager {

    private static final int UNIQUE_ID = 42;

    public static void postDailyNotification(final Context context, MehResponse response, Bitmap icon) {

        postIt(context, response, icon);
    }

    private static void postIt(Context context, MehResponse response, Bitmap icon) {
        Deal deal = response.getDeal();
        Theme theme = deal.getTheme();
        
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(deal.getTitle())
                        .setTicker(deal.getPriceRange())
                        .setContentText(deal.getPriceRange())
                        .setColor(theme.getBackgroundColor())
                        .setAutoCancel(true);
        if (icon != null) {
            notificationBuilder.setLargeIcon(icon);
        }

        if (MehPreferencesManager.getNotificationSound(context)) {
            notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }
        if (MehPreferencesManager.getNotificationVibrate(context)) {
            notificationBuilder.setVibrate(new long[]{100, 200, 100, 200});
        }

        if (Build.VERSION.SDK_INT >= 21) {
            notificationBuilder.setSmallIcon(theme.getForeground() == Theme.FOREGROUND_DARK ?
                    R.drawable.ic_meh_black : R.drawable.ic_meh);
        } else {
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
        }

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack
        //stackBuilder.addParentStack(JournalActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(UNIQUE_ID, notificationBuilder.build());
    }

    public static void removeAllNotifications(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }
}
