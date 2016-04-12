package com.jawnnypoo.openmeh.util;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.activity.AboutActivity;
import com.jawnnypoo.openmeh.activity.MehActivity;
import com.jawnnypoo.openmeh.activity.NotificationActivity;
import com.jawnnypoo.openmeh.shared.Theme;

/**
 * Manages all navigation
 */
public class NavigationManager {

    public static void navigateToMeh(Activity activity) {
        activity.startActivity(MehActivity.newIntent(activity));
        activity.overridePendingTransition(R.anim.fade_in, R.anim.do_nothing);
    }

    public static void navigateToNotifications(Activity activity, @Nullable Theme theme) {
        activity.startActivity(NotificationActivity.newInstance(activity, theme));
        activity.overridePendingTransition(R.anim.fade_in, R.anim.do_nothing);
    }

    public static void navigateToAbout(Activity activity, @Nullable Theme theme) {
        activity.startActivity(AboutActivity.newInstance(activity, theme));
        activity.overridePendingTransition(R.anim.fade_in, R.anim.do_nothing);
    }
}
