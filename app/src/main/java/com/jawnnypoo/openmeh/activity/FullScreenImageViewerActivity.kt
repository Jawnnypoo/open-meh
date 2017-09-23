package com.jawnnypoo.openmeh.activity

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.commit451.addendum.parceler.getParcelerParcelableExtra
import com.commit451.addendum.parceler.putParcelerParcelableExtra
import com.commit451.elasticdragdismisslayout.ElasticDragDismissFrameLayout
import com.commit451.elasticdragdismisslayout.ElasticDragDismissListener
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.adapter.ImageAdapter
import com.jawnnypoo.openmeh.shared.model.Theme
import me.relex.circleindicator.CircleIndicator

/**
 * Shows the full screen images
 */
class FullScreenImageViewerActivity : BaseActivity() {

    companion object {

        private val EXTRA_IMAGES = "images"

        fun newInstance(context: Context, theme: Theme?, images: MutableList<String>): Intent {
            val intent = Intent(context, FullScreenImageViewerActivity::class.java)
            if (theme != null) {
                intent.putParcelerParcelableExtra(BaseActivity.EXTRA_THEME, theme)
            }
            intent.putParcelerParcelableExtra(EXTRA_IMAGES, images)
            return intent
        }
    }

    @BindView(R.id.close) lateinit var buttonClose: ImageView
    @BindView(R.id.images) lateinit var imageViewPager: ViewPager
    @BindView(R.id.indicator) lateinit var indicator: CircleIndicator
    @BindView(R.id.draggable_frame) lateinit var draggableFrame: ElasticDragDismissFrameLayout

    lateinit var pagerAdapter: ImageAdapter

    @OnClick(R.id.close)
    fun onCloseClicked() {
        onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image_viewer)
        ButterKnife.bind(this)

        val images = intent.getStringArrayListExtra(EXTRA_IMAGES)

        val theme = intent.getParcelerParcelableExtra<Theme>(BaseActivity.Companion.EXTRA_THEME)

        pagerAdapter = ImageAdapter(true, object : ImageAdapter.Listener {
            override fun onImageClicked(v: View, position: Int) {
                //nothing yet
            }

        })
        imageViewPager.adapter = pagerAdapter
        pagerAdapter.setData(images)
        if (theme != null) {
            buttonClose.drawable.colorFilter = PorterDuffColorFilter(theme.safeForegroundColor(), PorterDuff.Mode.MULTIPLY)
            draggableFrame.setBackgroundColor(theme.safeBackgroundColor())
            indicator.setIndicatorColor(theme.safeForegroundColor())
        }
        indicator.setViewPager(imageViewPager)
        draggableFrame.addListener(object : ElasticDragDismissListener {
            override fun onDrag(elasticOffset: Float, elasticOffsetPixels: Float, rawOffset: Float, rawOffsetPixels: Float) {}

            override fun onDragDismissed() {
                //if you are targeting 21+ you might want to finish after transition
                onBackPressed()
            }
        })
    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
    }
}
