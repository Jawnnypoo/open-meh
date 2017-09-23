package com.jawnnypoo.openmeh.util

import android.app.Activity
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.activity.AboutActivity
import com.jawnnypoo.openmeh.activity.FullScreenImageViewerActivity
import com.jawnnypoo.openmeh.activity.NotificationActivity
import com.jawnnypoo.openmeh.shared.model.Theme
import java.util.*

/**
 * Manages all navigation
 */
object Navigator {

    fun navigateToNotifications(activity: Activity, theme: Theme?) {
        activity.startActivity(NotificationActivity.newInstance(activity, theme))
        activity.overridePendingTransition(R.anim.fade_in, R.anim.do_nothing)
    }

    fun navigateToAbout(activity: Activity, theme: Theme?) {
        activity.startActivity(AboutActivity.newInstance(activity, theme))
        activity.overridePendingTransition(R.anim.fade_in, R.anim.do_nothing)
    }

    fun navigateToFullScreenImageViewer(activity: AppCompatActivity, image: View, theme: Theme?, images: MutableList<String>) {
        val intent = FullScreenImageViewerActivity.newInstance(activity, theme, images)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, image, activity.getString(R.string.transition_images))
        activity.startActivity(intent, options.toBundle())
    }
}
