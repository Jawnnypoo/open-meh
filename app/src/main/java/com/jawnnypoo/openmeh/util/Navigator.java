package com.jawnnypoo.openmeh.util;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.activity.AboutActivity;
import com.jawnnypoo.openmeh.activity.FullScreenImageViewerActivity;
import com.jawnnypoo.openmeh.activity.MehActivity;
import com.jawnnypoo.openmeh.activity.NotificationActivity;
import com.jawnnypoo.openmeh.shared.model.Theme;

import java.util.ArrayList;

/**
 * Manages all navigation
 */
public class Navigator {

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

    public static void navigateToFullScreenImageViewer(Activity activity, @Nullable Theme theme, ArrayList<String> images) {
        activity.startActivity(FullScreenImageViewerActivity.newInstance(activity, theme, images));
        activity.overridePendingTransition(R.anim.fade_in, R.anim.do_nothing);
    }
}
