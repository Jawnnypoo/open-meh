package com.jawnnypoo.openmeh.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.shared.api.MehResponse;
import com.jawnnypoo.openmeh.shared.communication.TinyMehResponse;
import com.jawnnypoo.openmeh.shared.util.AssetUtil;

import butterknife.ButterKnife;

/**
 * Loads the meh thingy
 */
public class LoadingActivity extends Activity {

    GoogleApiClient mGoogleApiClient;

    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            load();
        }

        @Override
        public void onConnectionSuspended(int i) {
        }
    };

    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Toast.makeText(LoadingActivity.this, R.string.unable_to_connect_to_phone, Toast.LENGTH_SHORT)
                    .show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ButterKnife.bind(this);
        initGoogleApi();
        mGoogleApiClient.connect();
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

    private void load() {
        MehResponse mehResponse = new Gson().fromJson(
                AssetUtil.loadJSONFromAsset(this, "4-25-2015.json"), MehResponse.class);
        TinyMehResponse tinyMehResponse = TinyMehResponse.create(mehResponse);
        startActivity(MehActivity.newIntent(this, tinyMehResponse));
        finish();
    }
}
