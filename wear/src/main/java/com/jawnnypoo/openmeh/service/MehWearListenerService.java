package com.jawnnypoo.openmeh.service;

import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;
import com.jawnnypoo.openmeh.App;
import com.jawnnypoo.openmeh.event.FetchMehEvent;
import com.jawnnypoo.openmeh.shared.communication.DataValues;
import com.jawnnypoo.openmeh.shared.communication.TinyMehResponse;

import java.util.concurrent.TimeUnit;

/**
 * Listens for responses from device
 */
public class MehWearListenerService extends WearableListenerService {

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult =
                googleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Log.e("MehWearListenerService", "Failed to connect GoogleApiClient");
            return;
        }

    }

    private void sendMehResponse(DataMap dataMap) {
        String tinyMehResponseJson = dataMap.getString(DataValues.DATA_KEY_MEH_RESPONSE);
        TinyMehResponse tinyMehResponse = new Gson().fromJson(tinyMehResponseJson, TinyMehResponse.class);
        App.getInstance().getEventBus().post(new FetchMehEvent(tinyMehResponse));
        Log.d("MehWearListenerService", "Got the deal: " + tinyMehResponse.getTitle());
    }
}
