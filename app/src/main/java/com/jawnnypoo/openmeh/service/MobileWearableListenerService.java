package com.jawnnypoo.openmeh.service;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;
import com.jawnnypoo.openmeh.api.MehClient;
import com.jawnnypoo.openmeh.shared.api.MehResponse;
import com.jawnnypoo.openmeh.shared.communication.DataValues;
import com.jawnnypoo.openmeh.shared.communication.MessageType;
import com.jawnnypoo.openmeh.shared.communication.TinyMehResponse;

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
                loadMehAndSendItToWearable();
                break;
        }
    }

    private void loadMehAndSendItToWearable() {
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
            return;
        }
        if (mehResponse == null) {
            Timber.e("The meh response was null. Lame");
            return;
        }

        TinyMehResponse tinyMehResponse = TinyMehResponse.create(mehResponse);
        String tinyMehResponseJson = new Gson().toJson(tinyMehResponse);

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(DataValues.DATA_PATH_MEH_RESPONSE);
        putDataMapReq.getDataMap().putString(DataValues.DATA_KEY_MEH_RESPONSE, tinyMehResponseJson);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        //https://developers.google.com/android/reference/com/google/android/gms/wearable/PutDataRequest.html#setUrgent()
        putDataReq = putDataReq.setUrgent();
        Wearable.DataApi.putDataItem(googleApiClient, putDataReq);
    }
}
