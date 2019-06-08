package com.jawnnypoo.openmeh.extension

import androidx.viewpager.widget.ViewPager

fun ViewPager.addOnPageScrollStateChange(block: (state: Int) -> Unit) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {}

        override fun onPageScrollStateChanged(state: Int) {
            block.invoke(state)
        }
    })
}