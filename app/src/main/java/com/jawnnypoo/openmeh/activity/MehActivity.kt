package com.jawnnypoo.openmeh.activity

import `in`.uncod.android.bypass.Bypass
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.system.Os.bind
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.commit451.addendum.parceler.getParcelerParcelable
import com.commit451.addendum.parceler.putParcelerParcelable
import com.commit451.alakazam.backgroundColorAnimator
import com.commit451.alakazam.navigationBarColorAnimator
import com.commit451.alakazam.statusBarColorAnimator
import com.commit451.bypassglideimagegetter.BypassGlideImageGetter
import com.commit451.easel.Easel
import com.commit451.easel.tint
import com.commit451.easel.tintOverflow
import com.google.android.material.snackbar.Snackbar
import com.jawnnypoo.openmeh.App
import com.jawnnypoo.openmeh.BuildConfig
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.adapter.ImageAdapter
import com.jawnnypoo.openmeh.extension.bind
import com.jawnnypoo.openmeh.job.ReminderTestJob
import com.jawnnypoo.openmeh.shared.extension.getCheckoutUrl
import com.jawnnypoo.openmeh.shared.extension.getPriceRange
import com.jawnnypoo.openmeh.shared.extension.isSoldOut
import com.jawnnypoo.openmeh.shared.model.Deal
import com.jawnnypoo.openmeh.shared.model.Theme
import com.jawnnypoo.openmeh.shared.model.Video
import com.jawnnypoo.openmeh.shared.response.MehResponse
import com.jawnnypoo.openmeh.util.ColorUtil
import com.jawnnypoo.openmeh.util.IntentUtil
import com.jawnnypoo.openmeh.util.Navigator
import com.novoda.simplechromecustomtabs.SimpleChromeCustomTabs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_meh.*
import timber.log.Timber

/**
 * Activity that shows the meh.com deal of the day
 */
class MehActivity : BaseActivity() {

    companion object {
        private const val STATE_MEH_RESPONSE = "STATE_MEH_RESPONSE"
        private const val EXTRA_BUY_NOW = "key_meh_response"
        private const val ANIMATION_TIME = 800
    }

    private lateinit var imagePagerAdapter: ImageAdapter

    private lateinit var bypass: Bypass
    private var savedMehResponse: MehResponse? = null
    private var buyOnLoad = false

    override fun onCreate(savedInstanceState: Bundle?) {
        layoutInflater.factory = this
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meh)
        bypass = Bypass(this)
        toolbar.setTitle(R.string.app_name)
        toolbar.inflateMenu(R.menu.menu_main)
        if (BuildConfig.DEBUG) {
            toolbar.inflateMenu(R.menu.post_notification)
        }
        toolbar.setOnMenuItemClickListener { item ->
            var theme: Theme? = null
            if (savedMehResponse != null && savedMehResponse!!.deal != null) {
                theme = savedMehResponse!!.deal?.theme
            }
            val accentColor = if (theme == null) Color.WHITE else theme.safeAccentColor()
            when (item.itemId) {
                R.id.nav_notifications -> {
                    Navigator.navigateToNotifications(this@MehActivity, theme)
                    return@setOnMenuItemClickListener true
                }
                R.id.action_share -> {
                    IntentUtil.shareDeal(root, savedMehResponse)
                    return@setOnMenuItemClickListener true
                }
                R.id.action_refresh -> {
                    loadMeh()
                    return@setOnMenuItemClickListener true
                }
                R.id.action_post_notification -> ReminderTestJob.scheduleNow()
                R.id.nav_about -> {
                    Navigator.navigateToAbout(this@MehActivity, theme)
                    return@setOnMenuItemClickListener true
                }
                R.id.nav_account -> {
                    IntentUtil.openUrl(this@MehActivity, getString(R.string.url_account), accentColor)
                    return@setOnMenuItemClickListener true
                }
                R.id.nav_forum -> {
                    IntentUtil.openUrl(this@MehActivity, getString(R.string.url_forum), accentColor)
                    return@setOnMenuItemClickListener true
                }
                R.id.nav_orders -> {
                    IntentUtil.openUrl(this@MehActivity, getString(R.string.url_orders), accentColor)
                    return@setOnMenuItemClickListener true
                }
            }
            false
        }
        swipeRefreshLayout.setProgressViewOffset(false, 0, resources.getDimensionPixelOffset(R.dimen.swipe_refresh_offset))
        imagePagerAdapter = ImageAdapter(false, object : ImageAdapter.Listener {
            override fun onImageClicked(view: View, position: Int) {
                val photos = savedMehResponse?.deal?.photos
                if (photos != null) {
                    Navigator.navigateToFullScreenImageViewer(this@MehActivity, view, savedMehResponse?.deal?.theme, photos, viewPager.currentItem)
                }
            }
        })
        viewPager.adapter = imagePagerAdapter

        rootFailed.setOnClickListener {
            loadMeh()
        }
        textFullSpecs.setOnClickListener {
            val topicUrl = savedMehResponse?.deal?.topic?.url
            if (topicUrl != null) {
                val color = safeAccentColor(savedMehResponse)
                IntentUtil.openUrl(this, topicUrl, color)
            }
        }
        if (savedInstanceState != null) {
            savedMehResponse = savedInstanceState.getParcelerParcelable<MehResponse>(STATE_MEH_RESPONSE)
            val response = savedMehResponse
            if (response != null) {
                Timber.d("Restored from savedInstanceState")
                response.deal?.let {
                    bindDeal(it, false)
                }
            }
        } else {
            buyOnLoad = intent.getBooleanExtra(EXTRA_BUY_NOW, false)
            loadMeh()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        buyOnLoad = intent.getBooleanExtra(EXTRA_BUY_NOW, false)
        loadMeh()
    }

    override fun onResume() {
        super.onResume()
        SimpleChromeCustomTabs.getInstance().connectTo(this)
    }

    override fun onPause() {
        if (SimpleChromeCustomTabs.getInstance().isConnected) {
            SimpleChromeCustomTabs.getInstance().disconnectFrom(this)
        }
        super.onPause()
    }

    private fun loadMeh() {
        swipeRefreshLayout.isEnabled = true
        swipeRefreshLayout.isRefreshing = true
        rootFailed.visibility = View.GONE
        rootContent.visibility = View.GONE
        imageDealBackground.visibility = View.GONE
        App.get().meh.getMeh()
                .bind(this)
                .subscribe({ response->
                    swipeRefreshLayout.isEnabled = false
                    swipeRefreshLayout.isRefreshing = false
                    if (response.deal == null) {
                        Timber.e("There was a meh response, but it was null or the deal was null or something")
                        showError()
                        return@subscribe
                    }
                    savedMehResponse = response
                    bindDeal(response.deal!!, true)
                    if (buyOnLoad) {
                        buttonBuy.callOnClick()
                        buyOnLoad = false
                    }
                }, {
                    swipeRefreshLayout.isEnabled = false
                    swipeRefreshLayout.isRefreshing = false
                    Timber.e(it)
                    showError()
                })
    }

    private fun bindDeal(deal: Deal, animate: Boolean) {
        swipeRefreshLayout.isEnabled = false
        swipeRefreshLayout.isRefreshing = false
        rootFailed.visibility = View.GONE
        imagePagerAdapter.setData(deal.photos)
        val color = deal.theme?.safeForegroundColor() ?: Color.WHITE
        indicator.setIndicatorBackgroundTint(color)
        indicator.setViewPager(viewPager)
        if (deal.isSoldOut()) {
            buttonBuy.isEnabled = false
            buttonBuy.setText(R.string.sold_out)
        } else {
            buttonBuy.text = deal.getPriceRange() + "\n" + getString(R.string.buy_it)
            buttonBuy.setOnClickListener {
                val accentColor = safeAccentColor(savedMehResponse)
                IntentUtil.openUrl(this@MehActivity, deal.getCheckoutUrl(), accentColor)
            }
        }
        rootContent.visibility = View.VISIBLE
        imageDealBackground.visibility = View.VISIBLE
        if (animate) {
            rootContent.alpha = 0f
            rootContent.animate().alpha(1.0f).setDuration(ANIMATION_TIME.toLong()).startDelay = ANIMATION_TIME.toLong()
            imageDealBackground.alpha = 0f
            imageDealBackground.animate().alpha(1.0f).setStartDelay(ANIMATION_TIME.toLong()).setDuration(ANIMATION_TIME.toLong()).startDelay = ANIMATION_TIME.toLong()
        }
        textTitle.text = deal.title
        textDescription.text = markdownToCharSequence(textDescription, deal.features!!)
        textDescription.movementMethod = LinkMovementMethod.getInstance()
        if (deal.story != null) {
            textStoryTitle.text = deal.story!!.title
            textStoryBody.text = markdownToCharSequence(textStoryBody, deal.story!!.body!!)
            textStoryBody.movementMethod = LinkMovementMethod.getInstance()
        }
        val video = savedMehResponse?.video
        if (video != null) {
            bindVideo(video)
        }
        bindTheme(deal, animate)
    }

    private fun bindVideo(video: Video) {
        bindVideoLink(video)
    }

    private fun bindVideoLink(video: Video) {
        layoutInflater.inflate(R.layout.view_link_video, rootVideo)
        rootVideo.setOnClickListener {
            video.url?.let {
                val color = savedMehResponse?.deal?.theme?.safeAccentColor() ?: Color.WHITE
                IntentUtil.openUrl(this@MehActivity, it, color)
            }
        }
        val playIcon = rootVideo.findViewById<ImageView>(R.id.video_play)
        val title = rootVideo.findViewById<TextView>(R.id.video_title)
        title.text = video.title
        val color = savedMehResponse?.deal?.theme?.safeAccentColor() ?: Color.WHITE
        playIcon.drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    private fun bindTheme(deal: Deal, animate: Boolean) {
        val theme = deal.theme!!
        val accentColor = theme.safeAccentColor()
        val darkerAccentColor = Easel.darkerColor(accentColor)
        val backgroundColor = theme.safeBackgroundColor()
        val foreGround = theme.safeForegroundColor()
        val foreGroundInverse = theme.safeForegroundColorInverse()

        textTitle.setTextColor(foreGround)
        textDescription.setTextColor(foreGround)
        textDescription.setLinkTextColor(foreGround)

        if (deal.isSoldOut()) {
            buttonBuy.background.setColorFilter(foreGround, PorterDuff.Mode.MULTIPLY)
            buttonBuy.setTextColor(foreGroundInverse)
        } else {
            ViewCompat.setBackgroundTintList(buttonBuy, ColorUtil.createColorStateList(accentColor, Easel.darkerColor(accentColor)))
            buttonBuy.setTextColor(theme.safeBackgroundColor())
        }
        textFullSpecs.setTextColor(foreGround)
        textStoryTitle.setTextColor(accentColor)
        textStoryBody.setTextColor(foreGround)
        textStoryBody.setLinkTextColor(foreGround)
        toolbar.setTitleTextColor(backgroundColor)

        val decorView = window.decorView
        if (animate) {
            toolbar.backgroundColorAnimator(accentColor)
                    .setDuration(ANIMATION_TIME.toLong())
                    .start()
            if (Build.VERSION.SDK_INT >= 21) {
                window.statusBarColorAnimator(darkerAccentColor)
                        .setDuration(ANIMATION_TIME.toLong())
                        .start()
                window.navigationBarColorAnimator(darkerAccentColor)
                        .setDuration(ANIMATION_TIME.toLong())
                        .start()
            }
            decorView.backgroundColorAnimator(backgroundColor)
                    .setDuration(ANIMATION_TIME.toLong())
                    .start()
        } else {
            toolbar.setBackgroundColor(accentColor)
            if (Build.VERSION.SDK_INT >= 21) {
                window.statusBarColor = darkerAccentColor
                window.navigationBarColor = darkerAccentColor
            }
            decorView.setBackgroundColor(backgroundColor)
        }
        toolbar.menu.tint(backgroundColor)
        toolbar.tintOverflow(backgroundColor)
        Glide.with(this)
                .load(theme.backgroundImage)
                .into(imageDealBackground)
    }

    private fun markdownToCharSequence(textView: TextView, markdownString: String): CharSequence {
        return bypass.markdownToSpannable(markdownString, BypassGlideImageGetter(textView, Glide.with(this)))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelerParcelable(STATE_MEH_RESPONSE, savedMehResponse)
    }

    private fun showError() {
        rootFailed.visibility = View.VISIBLE
        Snackbar.make(root, R.string.error_with_server, Snackbar.LENGTH_SHORT)
                .show()
    }

    private fun safeAccentColor(response: MehResponse?): Int {
        return response?.deal?.theme?.safeAccentColor() ?: Color.WHITE
    }
}
