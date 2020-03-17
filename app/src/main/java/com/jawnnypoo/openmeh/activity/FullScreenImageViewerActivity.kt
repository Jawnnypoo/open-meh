package com.jawnnypoo.openmeh.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.adapter.ImageAdapter
import com.jawnnypoo.openmeh.extension.lightStatusBar
import com.jawnnypoo.openmeh.model.ParsedTheme
import kotlinx.android.synthetic.main.activity_full_screen_image_viewer.*

/**
 * Shows the full screen images
 */
class FullScreenImageViewerActivity : BaseActivity() {

    companion object {

        private const val EXTRA_IMAGES = "images"
        private const val EXTRA_INDEX = "index"

        fun newInstance(context: Context, theme: ParsedTheme?, images: ArrayList<String>, index: Int): Intent {
            return Intent(context, FullScreenImageViewerActivity::class.java).apply {
                putExtra(KEY_THEME, theme)
                putExtra(EXTRA_IMAGES, images)
                putExtra(EXTRA_INDEX, index)
            }
        }
    }

    private lateinit var pagerAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image_viewer)

        val images = intent.getStringArrayListExtra(EXTRA_IMAGES)

        val theme = intent.getParcelableExtra<ParsedTheme>(KEY_THEME)

        pagerAdapter = ImageAdapter(true, object : ImageAdapter.Listener {
            override fun onImageClicked(view: View, position: Int) {
                //nothing yet
            }

        })
        viewPager.adapter = pagerAdapter
        pagerAdapter.setData(images)
        if (theme != null) {
            val safeForegroundColor = theme.safeForegroundColor()
            val safeBackgroundColor = theme.safeBackgroundColor()
            buttonClose.drawable.colorFilter = PorterDuffColorFilter(safeForegroundColor, PorterDuff.Mode.MULTIPLY)
            root.setBackgroundColor(safeBackgroundColor)
            indicator.setIndicatorBackgroundTint(safeForegroundColor)
            window.statusBarColor = safeBackgroundColor
            lightStatusBar(safeForegroundColor == Color.BLACK)
        }
        indicator.setViewPager(viewPager)
        buttonClose.setOnClickListener { onBackPressed() }
        val index = intent.getIntExtra(EXTRA_INDEX, 0)
        viewPager.setCurrentItem(index, false)
    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
    }
}
