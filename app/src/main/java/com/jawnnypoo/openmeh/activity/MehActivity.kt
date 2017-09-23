package com.jawnnypoo.openmeh.activity

import `in`.uncod.android.bypass.Bypass
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.commit451.addendum.parceler.getParcelerParcelable
import com.commit451.addendum.parceler.putParcelerParcelable
import com.commit451.alakazam.Alakazam
import com.commit451.bypassglideimagegetter.BypassGlideImageGetter
import com.commit451.easel.Easel
import com.commit451.reptar.ComposableSingleObserver
import com.commit451.reptar.kotlin.fromIoToMainThread
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.jawnnypoo.openmeh.App
import com.jawnnypoo.openmeh.BuildConfig
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.adapter.ImageAdapter
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
import com.jawnnypoo.openmeh.util.MehUtil
import com.jawnnypoo.openmeh.util.Navigator
import com.novoda.simplechromecustomtabs.SimpleChromeCustomTabs
import me.relex.circleindicator.CircleIndicator
import timber.log.Timber

/**
 * Activity that shows the meh.com deal of the day
 */
class MehActivity : BaseActivity() {

    companion object {

        private val STATE_MEH_RESPONSE = "STATE_MEH_RESPONSE"
        private val EXTRA_BUY_NOW = "key_meh_response"
        private val ANIMATION_TIME = 800

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, MehActivity::class.java)
            return intent
        }

        fun newIntentForInstaBuy(context: Context): Intent {
            val intent = Intent(context, MehActivity::class.java)
            intent.putExtra(EXTRA_BUY_NOW, true)
            return intent
        }
    }

    @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
    @BindView(R.id.activity_root) lateinit var root: View
    @BindView(R.id.swipe_refresh) lateinit var swipeRefreshLayout: SwipeRefreshLayout
    @BindView(R.id.failed) lateinit var failedView: View
    @BindView(R.id.indicator) lateinit var indicator: CircleIndicator
    @BindView(R.id.deal_image_background) lateinit var imageBackground: ImageView
    @BindView(R.id.deal_image_view_pager) lateinit var viewPager: ViewPager
    @BindView(R.id.deal_buy_button) lateinit var buttonBuy: AppCompatButton
    @BindView(R.id.deal_title) lateinit var textTitle: TextView
    @BindView(R.id.deal_description) lateinit var textDescription: TextView
    @BindView(R.id.deal_full_specs) lateinit var textFullSpecs: TextView
    @BindView(R.id.story_title) lateinit var textStoryTitle: TextView
    @BindView(R.id.story_body) lateinit var textStoryBody: TextView
    @BindView(R.id.video_root) lateinit var rootVideo: ViewGroup

    lateinit var imagePagerAdapter: ImageAdapter
    var youTubeFragment: YouTubePlayerSupportFragment? = null
    var youTubePlayer: YouTubePlayer? = null

    lateinit var bypass: Bypass
    var savedMehResponse: MehResponse? = null
    var fullScreen = false
    var buyOnLoad = false

    @OnClick(R.id.deal_full_specs)
    fun onFullSpecsClick() {
        if (savedMehResponse != null && savedMehResponse!!.deal != null) {
            val topicUrl = savedMehResponse?.deal?.topic?.url
            if (topicUrl != null) {
                val color = safeAccentColor(savedMehResponse)
                IntentUtil.openUrl(this, topicUrl, color)
            }
        }
    }

    @OnClick(R.id.failed)
    fun onErrorClick() {
        loadMeh()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        layoutInflater.factory = this
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meh)
        ButterKnife.bind(this)
        bypass = Bypass(this)
        toolbar.setTitle(R.string.app_name)
        toolbar.inflateMenu(R.menu.menu_main)
        if (BuildConfig.DEBUG) {
            toolbar.inflateMenu(R.menu.post_notification)
        }
        toolbar.setOnMenuItemClickListener{ item->
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
                    Navigator.navigateToFullScreenImageViewer(this@MehActivity, view, savedMehResponse?.deal?.theme, photos)
                }
            }
        })
        viewPager.adapter = imagePagerAdapter

        youTubeFragment = YouTubePlayerSupportFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .replace(R.id.video_root, youTubeFragment)
                .commit()
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

    fun loadMeh() {
        swipeRefreshLayout.isEnabled = true
        swipeRefreshLayout.isRefreshing = true
        failedView.visibility = View.GONE
        root.visibility = View.GONE
        imageBackground.visibility = View.GONE
        App.get().meh.getMeh()
                .compose(bindToLifecycle())
                .fromIoToMainThread()
                .subscribe(object : ComposableSingleObserver<MehResponse>() {
                    override fun success(response: MehResponse) {
                        swipeRefreshLayout.isEnabled = false
                        swipeRefreshLayout.isRefreshing = false
                        if (response.deal == null) {
                            Timber.e("There was a meh response, but it was null or the deal was null or something")
                            showError()
                            return
                        }
                        savedMehResponse = response
                        bindDeal(response.deal!!, true)
                        if (buyOnLoad) {
                            buttonBuy.callOnClick()
                            buyOnLoad = false
                        }
                    }

                    override fun error(t: Throwable) {
                        swipeRefreshLayout.isEnabled = false
                        swipeRefreshLayout.isRefreshing = false
                        Timber.e(t)
                        showError()
                    }
                })
    }

    fun bindDeal(deal: Deal, animate: Boolean) {
        swipeRefreshLayout.isEnabled = false
        swipeRefreshLayout.isRefreshing = false
        failedView.visibility = View.GONE
        imagePagerAdapter.setData(deal.photos)
        indicator.setIndicatorColor(deal.theme!!.safeForegroundColor())
        indicator.setViewPager(viewPager)
        if (deal.isSoldOut()) {
            buttonBuy.isEnabled = false
            buttonBuy.setText(R.string.sold_out)
        } else {
            buttonBuy.text = deal.getPriceRange() + "\n" + getString(R.string.buy_it)
            buttonBuy.setOnClickListener {
                val color = safeAccentColor(savedMehResponse)
                IntentUtil.openUrl(this@MehActivity, deal.getCheckoutUrl(), color)
            }
        }
        root.visibility = View.VISIBLE
        imageBackground.visibility = View.VISIBLE
        if (animate) {
            root.alpha = 0f
            root.animate().alpha(1.0f).setDuration(ANIMATION_TIME.toLong()).startDelay = ANIMATION_TIME.toLong()
            imageBackground.alpha = 0f
            imageBackground.animate().alpha(1.0f).setStartDelay(ANIMATION_TIME.toLong()).setDuration(ANIMATION_TIME.toLong()).startDelay = ANIMATION_TIME.toLong()
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

    fun bindVideo(video: Video) {
        val videoUrl = video.url
        if (MehUtil.isYouTubeInstalled(this) && videoUrl != null) {
            val videoId = MehUtil.getYouTubeIdFromUrl(videoUrl)
            Timber.d("videoId: " + videoId!!)
            if (!TextUtils.isEmpty(videoId)) {
                bindYouTubeVideo(videoId)
                return
            }
        }
        bindVideoLink(video)
    }

    fun bindYouTubeVideo(videoId: String) {
        Timber.d("bindingYouTubeVideo")

        youTubeFragment?.initialize(BuildConfig.OPEN_MEH_GOOGLE_API_KEY, object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer, wasRestored: Boolean) {
                Timber.d("onInitializationSuccess")
                this@MehActivity.youTubePlayer = youTubePlayer
                if (!wasRestored) {
                    youTubePlayer.cueVideo(videoId)
                }
                youTubePlayer.setOnFullscreenListener({ b ->
                    fullScreen = b
                })
            }

            override fun onInitializationFailure(provider: YouTubePlayer.Provider, youTubeInitializationResult: YouTubeInitializationResult) {
                Timber.d("onInitializationFailure")
                supportFragmentManager
                        .beginTransaction()
                        .remove(youTubeFragment)
                        .commit()
                savedMehResponse?.video?.let {
                    bindVideoLink(it)
                }
            }
        })
    }

    fun bindVideoLink(video: Video) {
        Timber.d("YouTube didn't work. Just link it")
        supportFragmentManager.beginTransaction().remove(youTubeFragment).commitAllowingStateLoss()
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

    fun bindTheme(deal: Deal, animate: Boolean) {
        val theme = deal.theme!!
        val accentColor = theme.safeAccentColor()
        val darkerAccentColor = Easel.getDarkerColor(accentColor)
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
            ViewCompat.setBackgroundTintList(buttonBuy, ColorUtil.createColorStateList(accentColor, Easel.getDarkerColor(accentColor)))
            buttonBuy.setTextColor(theme.safeBackgroundColor())
        }
        textFullSpecs.setTextColor(foreGround)
        textStoryTitle.setTextColor(accentColor)
        textStoryBody.setTextColor(foreGround)
        textStoryBody.setLinkTextColor(foreGround)
        toolbar.setTitleTextColor(backgroundColor)

        val decorView = window.decorView
        if (animate) {
            Alakazam.backgroundColorAnimator(toolbar, accentColor)
                    .setDuration(ANIMATION_TIME.toLong())
                    .start()
            if (Build.VERSION.SDK_INT >= 21) {
                Alakazam.statusBarColorAnimator(window, darkerAccentColor)
                        .setDuration(ANIMATION_TIME.toLong())
                        .start()
                Alakazam.navigationBarColorAnimator(window, darkerAccentColor)
                        .setDuration(ANIMATION_TIME.toLong())
                        .start()
            }
            Alakazam.backgroundColorAnimator(decorView, backgroundColor)
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
        Easel.tint(toolbar.menu, backgroundColor)
        Easel.tintOverflow(toolbar, backgroundColor)
        Glide.with(this)
                .load(theme.backgroundImage)
                .into(imageBackground)
    }

    fun markdownToCharSequence(textView: TextView, markdownString: String): CharSequence {
        return bypass.markdownToSpannable(markdownString, BypassGlideImageGetter(textView, Glide.with(this)))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelerParcelable(STATE_MEH_RESPONSE, savedMehResponse)
    }

    override fun onBackPressed() {
        if (fullScreen) {
            youTubePlayer?.setFullscreen(false)
        } else {
            super.onBackPressed()
        }
    }

    fun showError() {
        failedView.visibility = View.VISIBLE
        Snackbar.make(root, R.string.error_with_server, Snackbar.LENGTH_SHORT)
                .show()
    }

    fun safeAccentColor(response: MehResponse?): Int {
        return response?.deal?.theme?.safeAccentColor() ?: Color.WHITE
    }
}
