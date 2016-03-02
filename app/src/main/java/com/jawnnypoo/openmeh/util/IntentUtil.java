package com.jawnnypoo.openmeh.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.api.MehResponse;
import com.jawnnypoo.openmeh.util.customtabs.BrowserFallback;
import com.jawnnypoo.openmeh.util.customtabs.CustomTabsActivityHelper;

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
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setToolbarColor(toolbarColor);
        intentBuilder.setStartAnimations(activity, R.anim.fade_in, R.anim.do_nothing);
        intentBuilder.setExitAnimations(activity, R.anim.do_nothing, R.anim.fade_out);
        CustomTabsActivityHelper.openCustomTab(activity, intentBuilder.build(), Uri.parse(url), new BrowserFallback());
    }

}
