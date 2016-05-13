package com.jawnnypoo.openmeh.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.ProgressSpinner;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.adapter.DealGridPagerAdapter;
import com.jawnnypoo.openmeh.model.MehWearResponse;
import com.jawnnypoo.openmeh.shared.communication.DataValues;
import com.jawnnypoo.openmeh.shared.communication.MessageType;
import com.jawnnypoo.openmeh.shared.model.Theme;
import com.jawnnypoo.openmeh.util.Callback;
import com.jawnnypoo.openmeh.util.MessageSender;
import com.jawnnypoo.openmeh.util.ParseMehDataItemTask;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MehActivity extends Activity implements MessageSender {

    @BindView(R.id.root)
    ViewGroup mRoot;
    @BindView(R.id.grid_view_pager)
    GridViewPager mGridViewPager;
    @BindView(R.id.indicator)
    DotsPageIndicator mDotsPageIndicator;
    @BindView(R.id.progress)
    ProgressSpinner mProgressSpinner;

    GoogleApiClient mGoogleApiClient;
    Node mPhoneNode;

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

    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
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

    private MessageApi.MessageListener mMessageListener = new MessageApi.MessageListener() {
        @Override
        public void onMessageReceived(MessageEvent messageEvent) {
            if (messageEvent.getPath().equals(MessageType.TYPE_FETCH_FAILED)) {
                showError();
            }
        }
    };

    private DataApi.DataListener mDataListener = new DataApi.DataListener() {
        @Override
        public void onDataChanged(DataEventBuffer dataEventBuffer) {
            Timber.d("Got data from phone result");
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

    private Callback<MehWearResponse> mCallback = new Callback<MehWearResponse>() {
        @Override
        public void onSuccess(MehWearResponse response) {
            mProgressSpinner.animate().alpha(0.0f);
            mGridViewPager.setAdapter(new DealGridPagerAdapter(getFragmentManager(), response));
            bind(response.getTinyMehResponse().getTheme());
        }

        @Override
        public void onFailure(Throwable t) {
            showError();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mDotsPageIndicator.setPager(mGridViewPager);
        initGoogleApi();
        mGoogleApiClient.connect();
        mProgressSpinner.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void initGoogleApi() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mOnConnectionFailedListener)
                .addApi(Wearable.API)
                .build();
    }

    @Override
    public boolean sendMessage(String path, byte[] data) {
        if (!mGoogleApiClient.isConnected() || mPhoneNode == null) {
            return false;
        }
        Wearable.MessageApi.sendMessage(mGoogleApiClient, mPhoneNode.getId(), path, data);
        return true;
    }

    private void load() {
        Uri existingDataUri = Uri.parse("wear://" + mPhoneNode.getId() + DataValues.DATA_PATH_MEH_RESPONSE + "/" + DataValues.getDataMehPathForToday());
        Wearable.DataApi.getDataItem(mGoogleApiClient, existingDataUri).setResultCallback(mDataItemResultResultCallback);
    }

    private void parseResult(DataItem dataItem) {
        Timber.d("Parsing and binding data result");
        new ParseMehDataItemTask(mGoogleApiClient, dataItem).enqueue(mCallback);

    }

    private void bind(Theme theme) {
        mRoot.setBackgroundColor(theme.getBackgroundColor());
        int dotColor = theme.getForegroundColor();
        mDotsPageIndicator.setDotColor(dotColor);
        mDotsPageIndicator.setDotColorSelected(dotColor);
    }

    private void showError() {
        Toast.makeText(MehActivity.this, R.string.unable_to_connect_to_phone, Toast.LENGTH_SHORT)
                .show();
        finish();
    }
}
