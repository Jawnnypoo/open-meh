package com.jawnnypoo.openmeh.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;

import com.jawnnypoo.openmeh.R;
import com.novoda.simplechromecustomtabs.navigation.NavigationFallback;

/**
 * A fallback to open the url in the browser
 */
public class BrowserFallback implements NavigationFallback {

    private Activity mActivity;

    public BrowserFallback(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onFallbackNavigateTo(Uri url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(url);
        try {
            mActivity.startActivity(i);
        } catch (ActivityNotFoundException e) {
            Snackbar.make(mActivity.getWindow().getDecorView(), R.string.error_no_browser, Snackbar.LENGTH_SHORT)
                    .show();
        }
    }
}
