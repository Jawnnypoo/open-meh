package com.jawnnypoo.openmeh.service;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;
import com.jawnnypoo.openmeh.shared.communication.DataValues;
import com.jawnnypoo.openmeh.shared.communication.TinyMehResponse;

import java.util.List;
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
        //wut
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);

        for (DataEvent event : events) {
            Uri uri = event.getDataItem().getUri();

            if (event.getType() == DataEvent.TYPE_CHANGED) {
                switch (uri.getPath()) {
                    case DataValues.DATA_PATH_MEH_RESPONSE:
                        DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                        sendMehResponse(dataMap);
                        break;
                }
            }
        }
    }

    private void sendMehResponse(DataMap dataMap) {
        String tinyMehResponseJson = dataMap.getString(DataValues.DATA_KEY_MEH_RESPONSE);
        TinyMehResponse tinyMehResponse = new Gson().fromJson(tinyMehResponseJson, TinyMehResponse.class);
        Log.d("MehWearListenerService", "Got the deal: " + tinyMehResponse.getTitle());
    }
}
