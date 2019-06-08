package com.jawnnypoo.openmeh.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.jawnnypoo.openmeh.R
import java.util.*

/**
 * Simple PagerAdapter that shows images
 */
class ImageAdapter(private val allowZoom: Boolean, private val listener: Listener) : androidx.viewpager.widget.PagerAdapter() {

    private val data: ArrayList<String> = ArrayList()

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val v: View = if (allowZoom) {
            LayoutInflater.from(collection.context).inflate(R.layout.item_zoomable_image, collection, false)
        } else {
            LayoutInflater.from(collection.context).inflate(R.layout.item_deal_image, collection, false)
        }
        val imageView = v.findViewById<ImageView>(R.id.imageView)
        Glide.with(collection.context)
                .load(data[position])
                .into(imageView)

        collection.addView(v, 0)
        v.setOnClickListener { view -> listener.onImageClicked(view, position) }
        return v
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    fun setData(data: Collection<String>?) {
        if (data != null && !data.isEmpty()) {
            this.data.clear()
            this.data.addAll(data)
            notifyDataSetChanged()
        }
    }

    interface Listener {
        fun onImageClicked(view: View, position: Int)
    }
}
