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
import coil.load
import com.commit451.addendum.design.snackbar
import com.commit451.alakazam.backgroundColorAnimator
import com.commit451.alakazam.navigationBarColorAnimator
import com.commit451.alakazam.statusBarColorAnimator
import com.commit451.easel.Easel
import com.commit451.easel.tint
import com.commit451.easel.tintOverflow
import com.jawnnypoo.openmeh.App
import com.jawnnypoo.openmeh.BuildConfig
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.adapter.ImageAdapter
import com.jawnnypoo.openmeh.databinding.ActivityMehBinding
import com.jawnnypoo.openmeh.databinding.ActivityNotificationsBinding
import com.jawnnypoo.openmeh.extension.addOnPageScrollStateChange
import com.jawnnypoo.openmeh.extension.setMarkdownText
import com.jawnnypoo.openmeh.model.ParsedTheme
import com.jawnnypoo.openmeh.shared.extension.getCheckoutUrl
import com.jawnnypoo.openmeh.shared.extension.getPriceRange
import com.jawnnypoo.openmeh.shared.extension.isSoldOut
import com.jawnnypoo.openmeh.shared.model.Deal
import com.jawnnypoo.openmeh.shared.model.Video
import com.jawnnypoo.openmeh.shared.response.MehResponse
import com.jawnnypoo.openmeh.util.IntentUtil
import com.jawnnypoo.openmeh.util.Navigator
import com.jawnnypoo.openmeh.util.SwipeRefreshViewPagerSyncer
import com.jawnnypoo.openmeh.worker.ReminderWorker
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

    private lateinit var binding: ActivityMehBinding
    private lateinit var imagePagerAdapter: ImageAdapter
    private lateinit var syncer: SwipeRefreshViewPagerSyncer

    private var savedMehResponse: MehResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        layoutInflater.factory = this
        super.onCreate(savedInstanceState)
        binding = ActivityMehBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setTitle(R.string.app_name)
        binding.toolbar.inflateMenu(R.menu.menu_main)
        if (BuildConfig.DEBUG) {
            binding.toolbar.inflateMenu(R.menu.post_notification)
        }
        binding.toolbar.setOnMenuItemClickListener { item ->
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
                    IntentUtil.shareDeal(binding.root, savedMehResponse)
                    return@setOnMenuItemClickListener true
                }
                R.id.action_post_notification -> {
                    var timeToAlert = LocalDateTime.now()
                    timeToAlert = timeToAlert.plusMinutes(1)
                    launch {
                        ReminderWorker.schedule(
                            this@MehActivity,
                            timeToAlert.hour,
                            timeToAlert.minute
                        )
                    }
                    Toast.makeText(this, "Scheduled for one minute from now", Toast.LENGTH_SHORT)
                        .show()
                }
                R.id.nav_about -> {
                    Navigator.navigateToAbout(this@MehActivity, theme)
                    return@setOnMenuItemClickListener true
                }
                R.id.nav_account -> {
                    IntentUtil.openUrl(
                        this@MehActivity,
                        getString(R.string.url_account),
                        accentColor
                    )
                    return@setOnMenuItemClickListener true
                }
                R.id.nav_forum -> {
                    IntentUtil.openUrl(this@MehActivity, getString(R.string.url_forum), accentColor)
                    return@setOnMenuItemClickListener true
                }
                R.id.nav_orders -> {
                    IntentUtil.openUrl(
                        this@MehActivity,
                        getString(R.string.url_orders),
                        accentColor
                    )
                    return@setOnMenuItemClickListener true
                }
            }
            false
        }
        imagePagerAdapter = ImageAdapter(false, object : ImageAdapter.Listener {
            override fun onImageClicked(view: View, position: Int) {
                val photos = savedMehResponse?.deal?.photos
                if (photos != null) {
                    Navigator.navigateToFullScreenImageViewer(
                        this@MehActivity,
                        view,
                        theme(),
                        photos,
                        binding.viewPager.currentItem
                    )
                }
            }
        })
        syncer = SwipeRefreshViewPagerSyncer(binding.swipeRefreshLayout)
        binding.viewPager.adapter = imagePagerAdapter
        binding.viewPager.addOnPageScrollStateChange {
            syncer.sync(it)
        }

        binding.rootFailed.setOnClickListener {
            loadMeh()
        }
        binding.textFullSpecs.setOnClickListener {
            val topicUrl = savedMehResponse?.deal?.topic?.url
            if (topicUrl != null) {
                val color = safeAccentColor()
                IntentUtil.openUrl(this, topicUrl, color)
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener { loadMeh() }
        loadMeh()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        loadMeh()
    }

    private fun loadMeh() {
        binding.swipeRefreshLayout.isRefreshing = true
        binding.rootFailed.visibility = View.GONE
        binding.rootContent.visibility = View.GONE
        binding.imageDealBackground.visibility = View.GONE
        launch {
            try {
                val response = App.get().meh.meh()
                savedMehResponse = response
                bindDeal(response.deal)
            } catch (e: Exception) {
                Timber.e(e)
                showError()
            } finally {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindDeal(deal: Deal) {
        binding.swipeRefreshLayout.isRefreshing = false
        binding.rootFailed.visibility = View.GONE
        imagePagerAdapter.setData(deal.photos)
        val color = theme()?.safeForegroundColor() ?: Color.WHITE
        binding.indicator.setIndicatorBackgroundTint(color)
        binding.indicator.setViewPager(binding.viewPager)
        if (deal.isSoldOut()) {
            binding.buttonBuy.isEnabled = false
            binding.buttonBuy.setText(R.string.sold_out)
        } else {
            binding.buttonBuy.text = "${deal.getPriceRange()}\n${getString(R.string.buy_it)}"
            binding.buttonBuy.setOnClickListener {
                val accentColor = safeAccentColor()
                IntentUtil.openUrl(this@MehActivity, deal.getCheckoutUrl(), accentColor)
            }
        }
        binding.rootContent.visibility = View.VISIBLE
        binding.imageDealBackground.visibility = View.VISIBLE
        binding.rootContent.alpha = 0f
        binding.rootContent.animate().alpha(1.0f).setDuration(ANIMATION_TIME.toLong()).startDelay =
            ANIMATION_TIME.toLong()
        binding.imageDealBackground.alpha = 0f
        binding.imageDealBackground.animate().alpha(1.0f).setStartDelay(ANIMATION_TIME.toLong())
            .setDuration(ANIMATION_TIME.toLong()).startDelay = ANIMATION_TIME.toLong()
        binding.textTitle.text = deal.title
        binding.textDescription.setMarkdownText(deal.features)
        binding.textDescription.movementMethod = LinkMovementMethod.getInstance()
        binding.textStoryTitle.text = deal.story.title
        binding.textStoryBody.setMarkdownText(deal.story.body)
        binding.textStoryBody.movementMethod = LinkMovementMethod.getInstance()
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
        layoutInflater.inflate(R.layout.view_link_video, binding.rootVideo)
        binding.rootVideo.setOnClickListener {
            val color = theme()?.safeAccentColor() ?: Color.WHITE
            IntentUtil.openUrl(this@MehActivity, video.url, color)
        }
        val playIcon = binding.rootVideo.findViewById<ImageView>(R.id.video_play)
        val title = binding.rootVideo.findViewById<TextView>(R.id.video_title)
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

        binding.textTitle.setTextColor(foreGround)
        binding.textDescription.setTextColor(foreGround)
        binding.textDescription.setLinkTextColor(foreGround)

        if (deal.isSoldOut()) {
            binding.buttonBuy.background.setColorFilter(foreGround, PorterDuff.Mode.MULTIPLY)
            binding.buttonBuy.setTextColor(foreGroundInverse)
        } else {
            binding.buttonBuy.setBackgroundColor(Easel.darkerColor(accentColor))
            binding.buttonBuy.setTextColor(theme.safeBackgroundColor())
        }
        binding.textFullSpecs.setTextColor(foreGround)
        binding.textStoryTitle.setTextColor(accentColor)
        binding.textStoryBody.setTextColor(foreGround)
        binding.textStoryBody.setLinkTextColor(foreGround)
        binding.toolbar.setTitleTextColor(backgroundColor)

        val decorView = window.decorView
        binding.toolbar.backgroundColorAnimator(accentColor)
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
        binding.toolbar.menu.tint(backgroundColor)
        binding.toolbar.tintOverflow(backgroundColor)
        binding.imageDealBackground.load(theme.backgroundImage)
    }

    private fun showError() {
        binding.rootFailed.visibility = View.VISIBLE
        binding.root.snackbar(R.string.error_with_server)
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
