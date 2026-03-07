package com.commit451.coilimagegetter

import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.TextView
import coil.ImageLoader
import coil.imageLoader
import coil.request.ImageRequest

/**
 * Minimal local replacement for the old CoilImageGetter dependency.
 */
class CoilImageGetter(
    private val textView: TextView,
    private val imageLoader: ImageLoader = textView.context.imageLoader,
    private val sourceModifier: ((source: String) -> String)? = null,
) : Html.ImageGetter {

    override fun getDrawable(source: String): Drawable {
        val finalSource = sourceModifier?.invoke(source) ?: source

        val drawablePlaceholder = DrawablePlaceHolder()
        imageLoader.enqueue(
            ImageRequest.Builder(textView.context)
                .data(finalSource)
                .target { drawable ->
                    drawablePlaceholder.updateDrawable(drawable)
                    textView.text = textView.text
                }
                .build(),
        )

        return drawablePlaceholder
    }

    @Suppress("DEPRECATION")
    private class DrawablePlaceHolder : BitmapDrawable() {

        private var drawable: Drawable? = null

        override fun draw(canvas: Canvas) {
            drawable?.draw(canvas)
        }

        fun updateDrawable(drawable: Drawable) {
            this.drawable = drawable
            val width = drawable.intrinsicWidth
            val height = drawable.intrinsicHeight
            drawable.setBounds(0, 0, width, height)
            setBounds(0, 0, width, height)
        }
    }
}
