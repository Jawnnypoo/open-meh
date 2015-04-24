package com.jawnnypoo.openmeh;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.view.View;

import com.jawnnypoo.openmeh.util.MehUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Gotta give em credit...
 * Created by Jawn on 4/23/2015.
 */
public class AboutDialog extends AppCompatDialog {


    @OnClick(R.id.john_credit)
    void onJohnClick(View v) {
        MehUtil.openPage(getContext(), getContext().getString(R.string.jawn_url));
    }
    @OnClick(R.id.kyrsten_credit)
    void onKyrstenClick(View v) {
        MehUtil.openPage(getContext(), getContext().getString(R.string.kyrsten_url));
    }
    @OnClick(R.id.libraries_used)
    void onLibrariesUsedClicked(View v) {
        MehUtil.openPage(getContext(), getContext().getString(R.string.apache_url));
    }
    @OnClick(R.id.library)
    void onLibraryClicked(View v) {
        MehUtil.openPage(getContext(), getContext().getString(R.string.apache_url));
    }
    @OnClick(R.id.sauce)
    void onSourceClick(View v) {
        MehUtil.openPage(getContext(), getContext().getString(R.string.source_url));
    }

    public AboutDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_about);
        ButterKnife.inject(this);
    }
}
