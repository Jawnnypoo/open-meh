package com.jawnnypoo.openmeh.util;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.api.MehResponse;

/**
 * All the intents
 * Created by Jawn on 7/16/2015.
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

    public static void openPage(View root, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        try {
            root.getContext().startActivity(i);
        } catch (ActivityNotFoundException e) {
            Snackbar.make(root, R.string.error_no_browser, Snackbar.LENGTH_SHORT)
                    .show();
        }
    }
}
