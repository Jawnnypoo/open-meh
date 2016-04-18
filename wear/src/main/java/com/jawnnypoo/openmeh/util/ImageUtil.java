package com.jawnnypoo.openmeh.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;

/**
 * Does image things
 */
public class ImageUtil {

    @Nullable
    public static Bitmap loadBitmapFromAsset(GoogleApiClient apiClient, Asset asset) {

        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                apiClient, asset).await().getInputStream();

        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }
}
