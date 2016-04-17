package com.jawnnypoo.openmeh.activity;

import android.app.Activity;
import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ButterKnife.bind(this);

        load();
    }

    private void load() {
        MehResponse mehResponse = new Gson().fromJson(
                AssetUtil.loadJSONFromAsset(this, "4-25-2015.json"), MehResponse.class);
        TinyMehResponse tinyMehResponse = TinyMehResponse.create(mehResponse);
        startActivity(MehActivity.newIntent(this, tinyMehResponse));
        finish();
    }
}
