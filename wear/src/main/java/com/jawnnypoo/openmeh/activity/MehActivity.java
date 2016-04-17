package com.jawnnypoo.openmeh.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.adapter.DealGridPagerAdapter;
import com.jawnnypoo.openmeh.shared.api.MehResponse;
import com.jawnnypoo.openmeh.shared.communication.TinyMehResponse;
import com.jawnnypoo.openmeh.shared.model.Theme;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MehActivity extends Activity {

    private static final String EXTRA_MEH_RESPONSE = "response";

    public static Intent newIntent(Context context, TinyMehResponse tinyMehResponse) {
        Intent intent = new Intent(context, MehActivity.class);
        intent.putExtra(EXTRA_MEH_RESPONSE, Parcels.wrap(tinyMehResponse));
        return intent;
    }

    @Bind(R.id.root)
    ViewGroup mRoot;
    @Bind(R.id.grid_view_pager)
    GridViewPager mGridViewPager;
    @Bind(R.id.indicator)
    DotsPageIndicator mDotsPageIndicator;

    GoogleApiClient mGoogleApiClient;
    MehResponse mMehResponse;

    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            if (mMehResponse == null) {
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
        TinyMehResponse response = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_MEH_RESPONSE));

        mGridViewPager.setAdapter(new DealGridPagerAdapter(getFragmentManager(), response));
        mDotsPageIndicator.setPager(mGridViewPager);
        initGoogleApi();
        bind(response.getTheme());
    }

    private void initGoogleApi() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mOnConnectionFailedListener)
                .addApi(Wearable.API)
                .build();
    }

    private void bind(Theme theme) {
        mRoot.setBackgroundColor(theme.getBackgroundColor());
        int dotColor = theme.getForegroundColor();
        mDotsPageIndicator.setDotColor(dotColor);
        mDotsPageIndicator.setDotColorSelected(dotColor);
    }
}
