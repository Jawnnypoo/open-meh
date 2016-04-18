package com.jawnnypoo.openmeh.service;

import android.content.Intent;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;
import com.jawnnypoo.openmeh.activity.MehActivity;
import com.jawnnypoo.openmeh.api.MehClient;
import com.jawnnypoo.openmeh.shared.api.MehResponse;
import com.jawnnypoo.openmeh.shared.communication.DataValues;
import com.jawnnypoo.openmeh.shared.communication.MessageType;
import com.jawnnypoo.openmeh.shared.communication.TinyMehResponse;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

import retrofit2.Response;
import timber.log.Timber;

/**
 * Listens for messages from the wearable device
 */
public class MobileWearableListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        switch (messageEvent.getPath()) {
            case MessageType.TYPE_FETCH_MEH:
                loadMehAndSendItToWearable(messageEvent.getSourceNodeId());
                break;
            case MessageType.TYPE_OPEN_ON_PHONE:
                Intent intent = MehActivity.newIntent(this);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case MessageType.TYPE_BUY_ON_PHONE:
                Intent buyIntent = MehActivity.newIntentForInstaBuy(this);
                buyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(buyIntent);
                break;

        }
    }

    private void loadMehAndSendItToWearable(String nodeId) {
        Timber.d("loading meh to send to wearable");
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult =
                googleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Timber.d("Failed to connect to Google API on the mobile device");
        }

        MehResponse mehResponse = null;
        try {
            Response<MehResponse> response = MehClient.instance().getMeh().execute();
            if (response.isSuccessful()) {
                mehResponse = response.body();
            }
        } catch (Exception any) {
            Timber.e(any, "Failed to fetch meh deal for wearable");
        }
        if (mehResponse == null || mehResponse.getDeal() == null) {
            Timber.e("The meh response was null. Lame");
            sendError(googleApiClient, nodeId);
            return;
        }

        TinyMehResponse tinyMehResponse = TinyMehResponse.create(mehResponse);
        String tinyMehResponseJson = new Gson().toJson(tinyMehResponse);

        Bitmap icon = null;
        try {
            icon = Glide.with(getApplicationContext())
                    .load(mehResponse.getDeal().getPhotos().get(0))
                    .asBitmap()
                    .into(300, 300)
                    .get();
        } catch (Exception e) {
            Timber.e(e, null);
        }

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(DataValues.DATA_PATH_MEH_RESPONSE + "/" + DataValues.getDataMehPathForToday());
        putDataMapReq.getDataMap().putString(DataValues.DATA_KEY_MEH_RESPONSE, tinyMehResponseJson);
        if (icon != null) {
            Asset asset = createAssetFromBitmap(icon);
            putDataMapReq.getDataMap().putAsset(DataValues.DATA_KEY_MEH_IMAGE, asset);
        }
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        //https://developers.google.com/android/reference/com/google/android/gms/wearable/PutDataRequest.html#setUrgent()
        putDataReq = putDataReq.setUrgent();
        DataApi.DataItemResult result = Wearable.DataApi.putDataItem(googleApiClient, putDataReq).await();
        if (result.getStatus().isSuccess()) {
            Timber.d("Successfully placed the data!");
        } else {
            Timber.e("Failed to put data item for some reason");
            sendError(googleApiClient, nodeId);
        }
    }

    private void sendError(GoogleApiClient client, String nodeId) {
        Wearable.MessageApi.sendMessage(client, nodeId, MessageType.TYPE_FETCH_FAILED, null);
    }

    /**
     * https://developer.android.com/training/wearables/data-layer/assets.html
     */
    private Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }
}
