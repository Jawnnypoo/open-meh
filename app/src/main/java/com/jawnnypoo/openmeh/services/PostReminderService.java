package com.jawnnypoo.openmeh.services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.jawnnypoo.openmeh.api.MehClient;
import com.jawnnypoo.openmeh.api.MehResponse;
import com.jawnnypoo.openmeh.data.Deal;
import com.jawnnypoo.openmeh.util.MehNotificationManager;
import com.jawnnypoo.openmeh.util.MehPreferencesManager;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import retrofit2.Response;
import timber.log.Timber;

/**
 * Service that pretty much just posts a notification then goes away
 * Created by Jawn on 4/20/2015.
 */
public class PostReminderService extends IntentService {

    private static final String TAG = PostReminderService.class.getSimpleName();

    public PostReminderService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        postNotification();
    }

    private void postNotification() {
        if (!MehPreferencesManager.getNotificationsPreference(getApplicationContext())) {
            //Notifications disabled, go away
            return;
        }
        Response<MehResponse> response = null;
        try {
            response = MehClient.instance().getMeh().execute();
        } catch (IOException error) {
            Timber.e(error.toString());
        }
        if (response == null || !response.isSuccess() || response.body() == null) {
            Timber.e("Response was null. Will not notify");
            return;
        }


        Deal deal = response.body().getDeal();
        Bitmap icon = null;
        //Shoot for the highest resolution
        //http://graphicdesign.stackexchange.com/questions/15776/issues-with-creating-a-hi-res-large-icon-for-android-notifications-in-jelly-bean
        try {
            icon = Glide.with(getApplicationContext())
                    .load(deal.getPhotos().get(0))
                    .asBitmap()
                    .centerCrop()
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (ExecutionException | InterruptedException e) {
            Timber.e(e.toString());
        }
        MehNotificationManager.postDailyNotification(getApplicationContext(), response.body(), icon);
    }
}
