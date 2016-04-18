package com.jawnnypoo.openmeh.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.jawnnypoo.openmeh.App;
import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.event.FetchMehEvent;
import com.jawnnypoo.openmeh.shared.communication.DataValues;
import com.jawnnypoo.openmeh.shared.communication.MessageType;
import com.jawnnypoo.openmeh.shared.communication.TinyMehResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Loads the meh thingy
 */
public class LoadingActivity extends Activity {

    GoogleApiClient mGoogleApiClient;
    Node mPhoneNode;
    EventReciever mEventReciever;

    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Wearable.DataApi.addListener(mGoogleApiClient, mDataListener);
            Wearable.MessageApi.addListener(mGoogleApiClient, mMessageListener);
            Wearable.NodeApi.getConnectedNodes(mGoogleApiClient)
                    .setResultCallback(mGetConnectedNodesResultResultCallback);
        }

        @Override
        public void onConnectionSuspended(int i) {
        }
    };

    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            showError();
        }
    };

    private MessageApi.MessageListener mMessageListener = new MessageApi.MessageListener() {
        @Override
        public void onMessageReceived(MessageEvent messageEvent) {
            if (messageEvent.getPath().equals(MessageType.TYPE_FETCH_FAILED)) {
                showError();
            }
        }
    };

    private ResultCallback<NodeApi.GetConnectedNodesResult> mGetConnectedNodesResultResultCallback = new ResultCallback<NodeApi.GetConnectedNodesResult>() {
        @Override
        public void onResult(@NonNull NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
            if (getConnectedNodesResult.getNodes() != null && !getConnectedNodesResult.getNodes().isEmpty()) {
                for (Node node : getConnectedNodesResult.getNodes()) {
                    if (node.isNearby()) {
                        mPhoneNode = node;
                        load();
                        return;
                    }
                }
            }
            showError();
        }
    };

    private ResultCallback<DataApi.DataItemResult> mDataItemResultResultCallback = new ResultCallback<DataApi.DataItemResult>() {
        @Override
        public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
            if (dataItemResult.getStatus().isSuccess() && dataItemResult.getDataItem() != null) {
                Timber.d("Got the cached data from the url " + dataItemResult.getDataItem().getUri());
                parseResult(dataItemResult.getDataItem());
            } else {
                Timber.d("Cached data was null. Asking phone for new data...");
                Wearable.MessageApi.sendMessage(mGoogleApiClient, mPhoneNode.getId(), MessageType.TYPE_FETCH_MEH, null);
            }
        }
    };

    private DataApi.DataListener mDataListener = new DataApi.DataListener() {
        @Override
        public void onDataChanged(DataEventBuffer dataEventBuffer) {
            Timber.d("Got data result");
            //wut
            final List<DataEvent> events = FreezableUtils.freezeIterable(dataEventBuffer);

            for (DataEvent event : events) {
                Uri uri = event.getDataItem().getUri();
                String expectedPath = DataValues.DATA_PATH_MEH_RESPONSE + "/" + DataValues.getDataMehPathForToday();
                if (expectedPath.equals(uri.getPath())) {
                    parseResult(event.getDataItem());
                    return;
                }
            }
            showError();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ButterKnife.bind(this);
        initGoogleApi();
        mGoogleApiClient.connect();
        mEventReciever = new EventReciever();
        App.getInstance().getEventBus().register(mEventReciever);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Wearable.DataApi.removeListener(mGoogleApiClient, mDataListener);
        if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.disconnect();
        }
        App.getInstance().getEventBus().unregister(mEventReciever);
    }

    private void initGoogleApi() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mOnConnectionFailedListener)
                .addApi(Wearable.API)
                .build();
    }

    private void load() {
        Uri existingDataUri = Uri.parse("wear://" + mPhoneNode.getId() + DataValues.DATA_PATH_MEH_RESPONSE + "/" + DataValues.getDataMehPathForToday());
        Wearable.DataApi.getDataItem(mGoogleApiClient, existingDataUri).setResultCallback(mDataItemResultResultCallback);
    }

    private void parseResult(DataItem dataItem) {
        Timber.d("Parsing data result");
        DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
        String tinyMehResponseJson = dataMap.getString(DataValues.DATA_KEY_MEH_RESPONSE);
        TinyMehResponse tinyMehResponse = new Gson().fromJson(tinyMehResponseJson, TinyMehResponse.class);
        moveAlong(tinyMehResponse);
    }

    private void showError() {
        Toast.makeText(LoadingActivity.this, R.string.unable_to_connect_to_phone, Toast.LENGTH_SHORT)
                .show();
        finish();
    }

    private void moveAlong(TinyMehResponse response) {
        startActivity(MehActivity.newIntent(LoadingActivity.this, response));
        finish();
    }

    private class EventReciever {

        @Subscribe
        public void onMehLoaded(FetchMehEvent event) {

        }
    }
}
