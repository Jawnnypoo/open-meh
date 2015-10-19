package com.jawnnypoo.openmeh.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.lang.ref.WeakReference;

import in.uncod.android.bypass.Bypass;

// credits: http://stackoverflow.com/a/25530488/504611
public class GlideImageGetter implements Bypass.ImageGetter {

    private WeakReference<Context> mContextWeakReference;
    private final TextView textView;
    private int maxWidth = -1;

    public GlideImageGetter(Context context, final TextView textView) {
        mContextWeakReference = new WeakReference<>(context);
        this.textView = textView;
    }

    @Override
    public Drawable getDrawable(final String source) {

        final BitmapDrawablePlaceHolder result = new BitmapDrawablePlaceHolder();

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(final Void... meh) {
                try {
                    Context context = mContextWeakReference.get();
                    if (context == null) {
                        return null;
                    }
                    return Glide.with(context)
                            .load(source)
                            .asBitmap()
                            .centerCrop()
                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final Bitmap bitmap) {
                try {
                    if (maxWidth == -1) {
                        int horizontalPadding = textView.getPaddingLeft() + textView.getPaddingRight();
                        maxWidth = textView.getMeasuredWidth() - horizontalPadding;
                        if (maxWidth == 0) {
                            maxWidth = Integer.MAX_VALUE;
                        }
                    }

                    Context context = mContextWeakReference.get();
                    if (context == null) {
                        return;
                    }
                    final BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
                    final double aspectRatio = 1.0 * drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
                    final int width = Math.min(maxWidth, drawable.getIntrinsicWidth());
                    final int height = (int) (width / aspectRatio);

                    drawable.setBounds(0, 0, width, height);

                    result.setDrawable(drawable);
                    result.setBounds(0, 0, width, height);

                    textView.setText(textView.getText()); // invalidate() doesn't work correctly...
                } catch (Exception e) {
                    /* nom nom nom */
                }
            }

        }.execute((Void) null);

        return result;
    }

    static class BitmapDrawablePlaceHolder extends BitmapDrawable {

        protected Drawable drawable;

        @Override
        public void draw(final Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }

    }

}