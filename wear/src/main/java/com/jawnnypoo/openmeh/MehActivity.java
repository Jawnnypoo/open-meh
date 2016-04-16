package com.jawnnypoo.openmeh;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.jawnnypoo.openmeh.shared.api.MehResponse;
import com.jawnnypoo.openmeh.shared.model.Deal;
import com.jawnnypoo.openmeh.shared.util.AssetUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MehActivity extends Activity {

    @Bind(R.id.image)
    ImageView mImage;
    @Bind(R.id.title)
    TextView mTextTitle;
    @Bind(R.id.price)
    TextView mTextPrice;
    @Bind(R.id.progress)
    View mProgress;

    GoogleApiClient mGoogleApiClient;
    MehResponse mMehResponse;

    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            if (mMehResponse == null) {
                load();
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
        }
    };

    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            //TODO show error
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initGoogleApi();
        load();
    }

    private void initGoogleApi() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mOnConnectionFailedListener)
                .addApi(Wearable.API)
                .build();
    }

    private void load() {
        mProgress.setVisibility(View.VISIBLE);
        MehResponse mehResponse = new Gson().fromJson(
                AssetUtil.loadJSONFromAsset(this, "4-25-2015.json"), MehResponse.class);
        bind(mehResponse.getDeal());
    }

    private void bind(@NonNull Deal deal) {
        mProgress.setVisibility(View.GONE);
        mTextTitle.setText(deal.getTitle());
        mTextPrice.setText(deal.getPriceRange());
    }
}
