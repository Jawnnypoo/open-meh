package com.jawnnypoo.openmeh;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jawnnypoo.openmeh.data.Deal;
import com.jawnnypoo.openmeh.data.Theme;
import com.jawnnypoo.openmeh.service.MehClient;
import com.jawnnypoo.openmeh.service.MehResponse;
import com.jawnnypoo.openmeh.services.PostReminderService;
import com.jawnnypoo.openmeh.util.ColorUtil;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity {

    private static final String KEY_MEH_RESPONSE = "KEY_MEH_RESPONSE";
    NotificationDialog notificationDialog;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.activity_root)
    View root;
    @InjectView(R.id.progress)
    View progress;
    @InjectView(R.id.indicator)
    CircleIndicator indicator;
    @InjectView(R.id.deal_image_view_pager)
    ViewPager imageViewPager;
    ImageAdapter imagePagerAdapter;
    @InjectView(R.id.deal_buy_button)
    AppCompatButton buy;
    @InjectView(R.id.deal_title)
    TextView title;
    @InjectView(R.id.deal_description)
    TextView description;
    @InjectView(R.id.deal_story)
    ImageView story;
    @InjectView(R.id.deal_video)
    ImageView video;

    MehResponse savedMehResponse;
    Gson gson = new Gson();

    @OnClick(R.id.deal_video)
    void onVideoClick(View view) {
        openPage(savedMehResponse.getVideo().getUrl());
    }

    @OnClick(R.id.deal_story)
    void onStoryClick(View view) {
        //TODO show story
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        imagePagerAdapter = new ImageAdapter(this);
        imageViewPager.setAdapter(imagePagerAdapter);
        setupDialogs();
        if (savedInstanceState != null) {
            String mehResponseJson = savedInstanceState.getString(KEY_MEH_RESPONSE);
            if (!TextUtils.isEmpty(mehResponseJson)) {
                savedMehResponse = gson.fromJson(mehResponseJson, MehResponse.class);
                Timber.d("Restored from savedInstanceState");
                bindDeal(savedMehResponse.getDeal());
            }
        }
        if (savedMehResponse == null) {
            loadMeh();
        }
        //testNotification();
    }

    private void setupDialogs() {
        notificationDialog = (NotificationDialog) getSupportFragmentManager()
                .findFragmentByTag(NotificationDialog.TAG);
        if (notificationDialog == null) {
            notificationDialog = new NotificationDialog();
        }
        //Restore listeners if needed
    }

    private void loadMeh() {
        progress.setVisibility(View.VISIBLE);
        root.setVisibility(View.GONE);
        MehClient.instance().getMeh(new Callback<MehResponse>() {
            @Override
            public void success(MehResponse mehResponse, Response response) {
                progress.setVisibility(View.GONE);
                root.setVisibility(View.VISIBLE);
                if (mehResponse == null || mehResponse.getDeal() == null) {
                    showError();
                    return;
                }
                savedMehResponse = mehResponse;
                bindDeal(mehResponse.getDeal());
            }

            @Override
            public void failure(RetrofitError error) {
                progress.setVisibility(View.GONE);
                root.setVisibility(View.VISIBLE);
                error.printStackTrace();
                showError();
            }
        });
    }

    private void bindDeal(final Deal deal) {
        if (deal.isSoldOut()) {
            buy.setEnabled(false);
            buy.setText(R.string.sold_out);
        } else {
            buy.setText(deal.getPriceRange() + "\n" + getString(R.string.buy_it));
            buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPage(deal.getUrl());
                }
            });
        }
        title.setText(deal.getTitle());
        description.setText(deal.getFeatures());
        imagePagerAdapter.setData(deal.getPhotos());
        indicator.setViewPager(imageViewPager);
        bindTheme(deal.getTheme());
    }

    private void bindTheme(Theme theme) {
        int accentColor = theme.getAccentColor();
        int darkerAccentColor = ColorUtil.getDarkerColor(accentColor);
        int backgroundColor = theme.getBackgroundColor();
        int foreGround = theme.getForeground() == Theme.FOREGROUND_LIGHT ? Color.WHITE : Color.BLACK;
        title.setTextColor(accentColor);
        description.setTextColor(foreGround);
        toolbar.setBackgroundColor(accentColor);
        buy.setSupportBackgroundTintList(ColorUtil.createColorStateList(accentColor, ColorUtil.getDarkerColor(accentColor)));
        buy.setTextColor(backgroundColor);
        video.getDrawable().setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);
        story.getDrawable().setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);
        ColorUtil.setStatusBarAndNavBarColor(getWindow(), darkerAccentColor);
        getWindow().getDecorView().setBackgroundColor(backgroundColor);
        notificationDialog.setTheme(theme);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (savedMehResponse != null) {
            outState.putString(KEY_MEH_RESPONSE, gson.toJson(savedMehResponse));
        }
    }

    private void openPage(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            SnackbarManager.show(
                    Snackbar.with(this)
                            .text(R.string.error_no_browser));
        }
    }

    private void showError() {
        SnackbarManager.show(
                Snackbar.with(MainActivity.this)
                        .text(R.string.error_with_server));
    }

    private void testNotification() {
        startService(new Intent(this, PostReminderService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notifications:
                notificationDialog.show(getSupportFragmentManager(), NotificationDialog.TAG);
                return true;
            case R.id.action_share:
                shareDeal();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareDeal() {
        if (savedMehResponse == null || savedMehResponse.getDeal() == null) {
            SnackbarManager.show(
                    Snackbar.with(MainActivity.this)
                            .text(R.string.error_nothing_to_share));
            return;
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        shareIntent.putExtra(Intent.EXTRA_TEXT, savedMehResponse.getDeal().getUrl());
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_subject)));
    }

    private static class ImageAdapter extends PagerAdapter {

        private Context mContext;
        private ArrayList<String> mData = new ArrayList<>();

        public ImageAdapter(Context context) {
            mContext = context;
        }

        public void setData(Collection<String> data) {
            if (data != null && !data.isEmpty()) {
                mData.clear();
                mData.addAll(data);
                notifyDataSetChanged();
            }
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_deal_image, collection, false);
            ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
            Glide.with(mContext)
                    .load(mData.get(position))
                    .into(imageView);

            collection.addView(v, 0);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
