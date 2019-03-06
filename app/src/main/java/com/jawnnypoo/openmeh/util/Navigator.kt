package com.jawnnypoo.openmeh.util

import android.app.Activity
import androidx.core.app.ActivityOptionsCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.activity.AboutActivity
import com.jawnnypoo.openmeh.activity.FullScreenImageViewerActivity
import com.jawnnypoo.openmeh.activity.NotificationActivity
import com.jawnnypoo.openmeh.shared.model.Theme

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

    fun navigateToFullScreenImageViewer(activity: AppCompatActivity, image: View, theme: Theme?, images: List<String>, index: Int) {
        val intent = FullScreenImageViewerActivity.newInstance(activity, theme, images, index)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, image, activity.getString(R.string.transition_images))
        activity.startActivity(intent, options.toBundle())
    }
}
