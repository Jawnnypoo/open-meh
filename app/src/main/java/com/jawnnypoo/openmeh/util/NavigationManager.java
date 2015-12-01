package com.jawnnypoo.openmeh.util;

import android.app.Activity;

import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.activities.MehActivity;
import com.jawnnypoo.openmeh.activities.NotificationActivity;
import com.jawnnypoo.openmeh.data.Theme;

/**
 * Manages all navigation
 * Created by Jawn on 12/1/2015.
 */
public class NavigationManager {

    public static void navigateToMeh(Activity activity) {
        activity.startActivity(MehActivity.newIntent(activity));
        activity.overridePendingTransition(R.anim.fade_in, R.anim.do_nothing);
    }

    public static void navigateToNotifications(Activity activity, Theme theme) {
        activity.startActivity(NotificationActivity.newInstance(activity, theme));
        activity.overridePendingTransition(R.anim.fade_in, R.anim.do_nothing);
    }
}
