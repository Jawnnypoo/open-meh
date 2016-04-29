package com.jawnnypoo.openmeh.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.shared.api.MehResponse;
import com.novoda.simplechromecustomtabs.SimpleChromeCustomTabs;
import com.novoda.simplechromecustomtabs.navigation.IntentCustomizer;
import com.novoda.simplechromecustomtabs.navigation.SimpleChromeCustomTabsIntentBuilder;

/**
 * All the intents
 */
public class IntentUtil {

    public static void shareDeal(View root, MehResponse mehResponse) {
        if (mehResponse == null || mehResponse.getDeal() == null) {
            Snackbar.make(root, R.string.error_nothing_to_share, Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, root.getContext().getString(R.string.share_subject));
        shareIntent.putExtra(Intent.EXTRA_TEXT, mehResponse.getDeal().getUrl());
        root.getContext().startActivity(Intent.createChooser(shareIntent, root.getContext().getString(R.string.share_subject)));
    }

    public static void openUrl(Activity activity, String url, int toolbarColor) {
        SimpleChromeCustomTabs.getInstance()
                .withFallback(new BrowserFallback(activity))
                .withIntentCustomizer(new MehIntentCustomizer(activity, toolbarColor))
                .navigateTo(Uri.parse(url), activity);
    }

    private static class MehIntentCustomizer implements IntentCustomizer {

        private int mToolbarColor;
        private Activity mActivity;
        public MehIntentCustomizer(Activity activity, int toolbarColor) {
            mToolbarColor = toolbarColor;
            mActivity = activity;
        }

        @Override
        public SimpleChromeCustomTabsIntentBuilder onCustomiseIntent(SimpleChromeCustomTabsIntentBuilder simpleChromeCustomTabsIntentBuilder) {
            return simpleChromeCustomTabsIntentBuilder.withToolbarColor(mToolbarColor)
                    .withStartAnimations(mActivity, R.anim.fade_in, R.anim.do_nothing)
                    .withExitAnimations(mActivity, R.anim.do_nothing, R.anim.fade_out);
        }
    }

}
