package com.jawnnypoo.openmeh.activity

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import com.commit451.addendum.parceler.getParcelerParcelableExtra
import com.commit451.addendum.parceler.putParcelerParcelableExtra
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.adapter.ImageAdapter
import com.jawnnypoo.openmeh.shared.model.Theme
import kotlinx.android.synthetic.main.activity_full_screen_image_viewer.*

/**
 * Shows the full screen images
 */
class FullScreenImageViewerActivity : BaseActivity() {

    companion object {

        private const val EXTRA_IMAGES = "images"
        private const val EXTRA_INDEX = "index"

        fun newInstance(context: Context, theme: Theme?, images: List<String>, index: Int): Intent {
            val intent = Intent(context, FullScreenImageViewerActivity::class.java)
            intent.putParcelerParcelableExtra(EXTRA_THEME, theme)
            intent.putParcelerParcelableExtra(EXTRA_IMAGES, images)
            intent.putExtra(EXTRA_INDEX, index)
            return intent
        }
    }

    private lateinit var pagerAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image_viewer)

        val images = intent.getParcelerParcelableExtra<List<String>>(EXTRA_IMAGES)

        val theme = intent.getParcelerParcelableExtra<Theme>(BaseActivity.Companion.EXTRA_THEME)

        pagerAdapter = ImageAdapter(true, object : ImageAdapter.Listener {
            override fun onImageClicked(view: View, position: Int) {
                //nothing yet
            }

        })
        viewPager.adapter = pagerAdapter
        pagerAdapter.setData(images)
        if (theme != null) {
            buttonClose.drawable.colorFilter = PorterDuffColorFilter(theme.safeForegroundColor(), PorterDuff.Mode.MULTIPLY)
            root.setBackgroundColor(theme.safeBackgroundColor())
            indicator.setIndicatorBackgroundTint(theme.safeForegroundColor())
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
