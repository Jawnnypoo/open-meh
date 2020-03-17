package com.jawnnypoo.openmeh.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import coil.api.load
import com.commit451.alakazam.backgroundColorAnimator
import com.commit451.alakazam.navigationBarColorAnimator
import com.commit451.alakazam.statusBarColorAnimator
import com.commit451.easel.Easel
import com.commit451.easel.tint
import com.commit451.easel.tintOverflow
import com.google.android.material.snackbar.Snackbar
import com.jawnnypoo.openmeh.App
import com.jawnnypoo.openmeh.BuildConfig
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.adapter.ImageAdapter
import com.jawnnypoo.openmeh.extension.addOnPageScrollStateChange
import com.jawnnypoo.openmeh.extension.setMarkdownText
import com.jawnnypoo.openmeh.model.ParsedTheme
import com.jawnnypoo.openmeh.shared.extension.getCheckoutUrl
import com.jawnnypoo.openmeh.shared.extension.getPriceRange
import com.jawnnypoo.openmeh.shared.extension.isSoldOut
import com.jawnnypoo.openmeh.shared.model.Deal
import com.jawnnypoo.openmeh.shared.model.Video
import com.jawnnypoo.openmeh.shared.response.MehResponse
import com.jawnnypoo.openmeh.util.ColorUtil
import com.jawnnypoo.openmeh.util.IntentUtil
import com.jawnnypoo.openmeh.util.Navigator
import com.jawnnypoo.openmeh.util.SwipeRefreshViewPagerSyncer
import com.jawnnypoo.openmeh.worker.ReminderWorker
import com.novoda.simplechromecustomtabs.SimpleChromeCustomTabs
import kotlinx.android.synthetic.main.activity_meh.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import timber.log.Timber

/**
 * Activity that shows the meh.com deal of the day
 */
class MehActivity : BaseActivity() {

    companion object {
        private const val ANIMATION_TIME = 800
    }

    private lateinit var imagePagerAdapter: ImageAdapter
    private lateinit var syncer: SwipeRefreshViewPagerSyncer

    private var savedMehResponse: MehResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        layoutInflater.factory = this
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meh)
        toolbar.setTitle(R.string.app_name)
        toolbar.inflateMenu(R.menu.menu_main)
        if (BuildConfig.DEBUG) {
            toolbar.inflateMenu(R.menu.post_notification)
        }
        toolbar.setOnMenuItemClickListener { item ->
            val theme: ParsedTheme? = savedMehResponse?.deal?.theme?.let {
                ParsedTheme.fromTheme(it)
            }
            val accentColor = theme?.safeAccentColor() ?: Color.WHITE
            when (item.itemId) {
                R.id.nav_notifications -> {
                    Navigator.navigateToNotifications(this@MehActivity, theme)
                    return@setOnMenuItemClickListener true
                }
                R.id.action_share -> {
                    IntentUtil.shareDeal(root, savedMehResponse)
                    return@setOnMenuItemClickListener true
                }
                R.id.action_post_notification -> {
                    var timeToAlert = LocalDateTime.now()
                    timeToAlert = timeToAlert.plusMinutes(1)
                    launch {
                        ReminderWorker.schedule(this@MehActivity, timeToAlert.hour, timeToAlert.minute)
                    }
                    Toast.makeText(this, "Scheduled for one minute from now", Toast.LENGTH_SHORT)
                            .show()
                }
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
        imagePagerAdapter = ImageAdapter(false, object : ImageAdapter.Listener {
            override fun onImageClicked(view: View, position: Int) {
                val photos = savedMehResponse?.deal?.photos
                if (photos != null) {
                    Navigator.navigateToFullScreenImageViewer(this@MehActivity, view, theme(), photos, viewPager.currentItem)
                }
            }
        })
        syncer = SwipeRefreshViewPagerSyncer(swipeRefreshLayout)
        viewPager.adapter = imagePagerAdapter
        viewPager.addOnPageScrollStateChange {
            syncer.sync(it)
        }

        rootFailed.setOnClickListener {
            loadMeh()
        }
        textFullSpecs.setOnClickListener {
            val topicUrl = savedMehResponse?.deal?.topic?.url
            if (topicUrl != null) {
                val color = safeAccentColor()
                IntentUtil.openUrl(this, topicUrl, color)
            }
        }
        swipeRefreshLayout.setOnRefreshListener { loadMeh() }
        // loadMeh()
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        loadMeh()
    }

    private fun loadMeh() {
        swipeRefreshLayout.isRefreshing = true
        rootFailed.visibility = View.GONE
        rootContent.visibility = View.GONE
        imageDealBackground.visibility = View.GONE
        launch {
            try {
                val response = App.get().meh.meh()
                savedMehResponse = response
                bindDeal(response.deal)
            } catch (e: Exception) {
                Timber.e(e)
                showError()
            } finally {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindDeal(deal: Deal) {
        swipeRefreshLayout.isRefreshing = false
        rootFailed.visibility = View.GONE
        imagePagerAdapter.setData(deal.photos)
        val color = theme()?.safeForegroundColor() ?: Color.WHITE
        indicator.setIndicatorBackgroundTint(color)
        indicator.setViewPager(viewPager)
        if (deal.isSoldOut()) {
            buttonBuy.isEnabled = false
            buttonBuy.setText(R.string.sold_out)
        } else {
            buttonBuy.text = "${deal.getPriceRange()}\n${getString(R.string.buy_it)}"
            buttonBuy.setOnClickListener {
                val accentColor = safeAccentColor()
                IntentUtil.openUrl(this@MehActivity, deal.getCheckoutUrl(), accentColor)
            }
        }
        rootContent.visibility = View.VISIBLE
        imageDealBackground.visibility = View.VISIBLE
        rootContent.alpha = 0f
        rootContent.animate().alpha(1.0f).setDuration(ANIMATION_TIME.toLong()).startDelay = ANIMATION_TIME.toLong()
        imageDealBackground.alpha = 0f
        imageDealBackground.animate().alpha(1.0f).setStartDelay(ANIMATION_TIME.toLong()).setDuration(ANIMATION_TIME.toLong()).startDelay = ANIMATION_TIME.toLong()
        textTitle.text = deal.title
        textDescription.setMarkdownText(deal.features)
        textDescription.movementMethod = LinkMovementMethod.getInstance()
        textStoryTitle.text = deal.story.title
        textStoryBody.setMarkdownText(deal.story.body)
        textStoryBody.movementMethod = LinkMovementMethod.getInstance()
        val video = savedMehResponse?.video
        if (video != null) {
            bindVideo(video)
        }
        bindTheme(deal)
    }

    private fun bindVideo(video: Video) {
        bindVideoLink(video)
    }

    private fun bindVideoLink(video: Video) {
        layoutInflater.inflate(R.layout.view_link_video, rootVideo)
        rootVideo.setOnClickListener {
            val color = theme()?.safeAccentColor() ?: Color.WHITE
            IntentUtil.openUrl(this@MehActivity, video.url, color)
        }
        val playIcon = rootVideo.findViewById<ImageView>(R.id.video_play)
        val title = rootVideo.findViewById<TextView>(R.id.video_title)
        title.text = video.title
        val color = theme()?.safeAccentColor() ?: Color.WHITE
        playIcon.drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    private fun bindTheme(deal: Deal) {
        val theme = theme()!!
        val accentColor = theme.safeAccentColor()
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
        toolbar.backgroundColorAnimator(accentColor)
                .setDuration(ANIMATION_TIME.toLong())
                .start()
        window.statusBarColorAnimator(accentColor)
                .setDuration(ANIMATION_TIME.toLong())
                .start()
        window.navigationBarColorAnimator(accentColor)
                .setDuration(ANIMATION_TIME.toLong())
                .start()
        decorView.backgroundColorAnimator(backgroundColor)
                .setDuration(ANIMATION_TIME.toLong())
                .start()
        toolbar.menu.tint(backgroundColor)
        toolbar.tintOverflow(backgroundColor)
        imageDealBackground.load(theme.backgroundImage)
    }

    private fun showError() {
        rootFailed.visibility = View.VISIBLE
        Snackbar.make(root, R.string.error_with_server, Snackbar.LENGTH_SHORT)
                .show()
    }

    private fun safeAccentColor(): Int {
        return theme()?.safeAccentColor() ?: Color.WHITE
    }

    private fun theme(): ParsedTheme? {
        return savedMehResponse?.deal?.theme?.let {
            ParsedTheme.fromTheme(it)
        }
    }
}
