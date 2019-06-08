package com.jawnnypoo.openmeh.util

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager

/**
 * Syncs state between [SwipeRefreshLayout], disabling the swipe
 * to refresh when the view pager is being paged
 */
class SwipeRefreshViewPagerSyncer(private val swipeRefreshLayout: SwipeRefreshLayout) {

    /**
     * Sync the state. Call this within OnPageChangeListener.onPageScrollStateChanged

     * @param state the state from the on page change listener
     */
    fun sync(state: Int) {
        swipeRefreshLayout.isEnabled = state == ViewPager.SCROLL_STATE_IDLE
    }
}