package com.jawnnypoo.openmeh.util;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.gson.Gson;
import com.jawnnypoo.openmeh.model.MehWearResponse;
import com.jawnnypoo.openmeh.shared.communication.DataValues;
import com.jawnnypoo.openmeh.shared.communication.TinyMehResponse;

import java.lang.ref.WeakReference;
import java.util.UUID;

/**
 * Runs a task to parse out a {@link MehWearResponse}
 */
public class ParseMehDataItemTask {

    private GoogleApiClient mGoogleApiClient;
    private DataItem mDataItem;

    public ParseMehDataItemTask(GoogleApiClient apiClient, DataItem dataItem) {
        mGoogleApiClient = apiClient;
        mDataItem = dataItem;
    }

    public void enqueue(Callback<MehWearResponse> callback) {
        //Not really queuing it, but I like to match Retrofit syntax
        new TheAsyncTask(mGoogleApiClient, callback).execute(mDataItem);
    }

    private static class TheAsyncTask extends AsyncTask<DataItem, Void, MehWearResponse> {

        private GoogleApiClient mGoogleApiClient;
        private WeakReference<Callback<MehWearResponse>> mCallbackWeakReference;

        public TheAsyncTask(GoogleApiClient googleApiClient, Callback<MehWearResponse> callback) {
            mGoogleApiClient = googleApiClient;
            mCallbackWeakReference = new WeakReference<>(callback);
        }

        @Nullable
        @Override
        protected MehWearResponse doInBackground(DataItem... params) {
            DataItem dataItem = params[0];
            DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
            String tinyMehResponseJson = dataMap.getString(DataValues.DATA_KEY_MEH_RESPONSE);
            Asset asset = dataMap.getAsset(DataValues.DATA_KEY_MEH_IMAGE);
            String imageId = null;
            if (asset != null) {
                imageId = UUID.randomUUID().toString();
                ImageCache.putImage(imageId, ImageUtil.loadBitmapFromAsset(mGoogleApiClient, asset));
            }
            TinyMehResponse tinyMehResponse = new Gson().fromJson(tinyMehResponseJson, TinyMehResponse.class);
            return new MehWearResponse(tinyMehResponse, imageId);
        }

        @Override
        protected void onPostExecute(MehWearResponse response) {
            super.onPostExecute(response);
            Callback<MehWearResponse> callback = mCallbackWeakReference.get();
            if (callback != null) {
                callback.onSuccess(response);
            }
        }
    }
}