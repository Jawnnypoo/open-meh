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

import com.jawnnypoo.openmeh.activity.MehActivity;
import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.shared.api.MehResponse;
import com.jawnnypoo.openmeh.shared.model.Deal;
import com.jawnnypoo.openmeh.shared.model.Theme;

/**
 * Manages notification stuff
 */
public class MehNotificationManager {

    private static final int UNIQUE_ID = 42;

    public static void postDailyNotification(final Context context, MehResponse response, Bitmap icon) {
        postIt(context, response, icon);
    }

    private static void postIt(Context context, MehResponse response, Bitmap icon) {
        Deal deal = response.getDeal();
        Theme theme = deal.getTheme();
        String priceString = deal.isSoldOut() ? context.getString(R.string.sold_out)
                : deal.getPriceRange();
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(deal.getTitle())
                        .setTicker(deal.getPriceRange())
                        .setContentText(priceString)
                        .setColor(theme.getBackgroundColor())
                        .setAutoCancel(true);
        if (icon != null) {
            notificationBuilder.setLargeIcon(icon);
            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(icon)
                    .bigPicture(icon)
                    .setBigContentTitle(deal.getTitle())
                    .setSummaryText(priceString));
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
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MehActivity.class);
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
