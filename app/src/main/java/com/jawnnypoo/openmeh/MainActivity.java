package com.jawnnypoo.openmeh;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.jawnnypoo.openmeh.api.MehClient;
import com.jawnnypoo.openmeh.api.MehResponse;
import com.jawnnypoo.openmeh.data.Deal;
import com.jawnnypoo.openmeh.data.Theme;
import com.jawnnypoo.openmeh.data.Topic;
import com.jawnnypoo.openmeh.data.Video;
import com.jawnnypoo.openmeh.services.PostReminderService;
import com.jawnnypoo.openmeh.util.ColorUtil;
import com.jawnnypoo.openmeh.util.MehUtil;
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


public class MainActivity extends BaseActivity {

    private static final String KEY_MEH_RESPONSE = "KEY_MEH_RESPONSE";
    private static final String YOUTUBE_FRAGMENT_TAG = "YOUTUBE_FRAGMENT_TAG";
    private static final int ANIMATION_TIME = 800;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.toolbar_title)
    TextView toolbarTitle;
    @InjectView(R.id.activity_root)
    View root;
    @InjectView(R.id.progress)
    View progress;
    @InjectView(R.id.failed)
    View failedView;
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
    @InjectView(R.id.deal_full_specs)
    TextView fullSpecs;
    @InjectView(R.id.story_title)
    TextView storyTitle;
    @InjectView(R.id.story_body)
    TextView storyBody;
    @InjectView(R.id.video_root)
    ViewGroup videoRoot;

    YouTubePlayerSupportFragment youTubeFragment;

    Menu menu;
    MehResponse savedMehResponse;

    @OnClick(R.id.deal_full_specs)
    void onFullSpecsClick(View view) {
        if (savedMehResponse != null && savedMehResponse.getDeal() != null) {
            Topic topic = savedMehResponse.getDeal().getTopic();
            if (topic != null && !TextUtils.isEmpty(topic.getUrl())) {
                MehUtil.openPage(this, topic.getUrl());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        toolbarTitle.setText(R.string.app_name);
        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AboutDialog(MainActivity.this).show();
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        imagePagerAdapter = new ImageAdapter(this);
        imageViewPager.setAdapter(imagePagerAdapter);
        failedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMeh();
            }
        });
        if (savedInstanceState != null) {
            String mehResponseJson = savedInstanceState.getString(KEY_MEH_RESPONSE);
            if (!TextUtils.isEmpty(mehResponseJson)) {
                savedMehResponse = gson.fromJson(mehResponseJson, MehResponse.class);
                Timber.d("Restored from savedInstanceState");
                bindDeal(savedMehResponse.getDeal(), false);
            }
        }
//        testMeh();
        if (savedMehResponse == null) {
            loadMeh();
        }
    }

    private void loadMeh() {
        progress.setVisibility(View.VISIBLE);
        failedView.setVisibility(View.GONE);
        root.setVisibility(View.GONE);
        MehClient.instance().getMeh(new Callback<MehResponse>() {
            @Override
            public void success(MehResponse mehResponse, Response response) {
                if (mehResponse == null || mehResponse.getDeal() == null) {
                    showError();
                    return;
                }
                progress.setVisibility(View.GONE);
                root.setVisibility(View.VISIBLE);
                savedMehResponse = mehResponse;
                bindDeal(mehResponse.getDeal(), true);
            }

            @Override
            public void failure(RetrofitError error) {
                progress.setVisibility(View.GONE);
                error.printStackTrace();
                showError();
            }
        });
    }

    private void bindDeal(final Deal deal, boolean animate) {
        progress.setVisibility(View.GONE);
        failedView.setVisibility(View.GONE);
        imagePagerAdapter.setData(deal.getPhotos());
        indicator.setViewPager(imageViewPager);
        if (deal.isSoldOut()) {
            buy.setEnabled(false);
            buy.setText(R.string.sold_out);
        } else {
            buy.setText(deal.getPriceRange() + "\n" + getString(R.string.buy_it));
            buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MehUtil.openPage(MainActivity.this, deal.getUrl());
                }
            });
        }
        root.setVisibility(View.VISIBLE);
        if (animate) {
            root.setAlpha(0f);
            root.animate().alpha(1.0f).setDuration(ANIMATION_TIME).setStartDelay(ANIMATION_TIME);
        }
        title.setText(deal.getTitle());
        description.setText(deal.getFeatures());
        if (deal.getStory() != null) {
            storyTitle.setText(deal.getStory().getTitle());
            storyBody.setText(deal.getStory().getBody());
        }
        if (savedMehResponse.getVideo() != null) {
            bindVideo(savedMehResponse.getVideo());
        }
        bindTheme(deal, animate);

    }

    private void bindVideo(Video video) {
        final String videoUrl = video.getUrl();
        if (MehUtil.isYouTubeInstalled(this)) {
            String videoId = MehUtil.getYouTubeIdFromUrl(videoUrl);
            Timber.d("videoId: " + videoId);
            if (!TextUtils.isEmpty(videoId)) {
                bindYouTubeVideo(videoId);
                return;
            }
        }
        bindVideoLink(video);
    }

    private void bindYouTubeVideo(final String videoId) {
        Timber.d("bindingYouTubeVideo");
        youTubeFragment = YouTubePlayerSupportFragment.newInstance();
        youTubeFragment.initialize(BuildConfig.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                getSupportFragmentManager().beginTransaction().add(R.id.video_root, youTubeFragment).commit();
                if (!wasRestored) {
                    youTubePlayer.cueVideo(videoId);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                bindVideoLink(savedMehResponse.getVideo());
            }
        });
    }

    private void bindVideoLink(final Video video) {
        Timber.d("YouTube didn't work. Just link it");
        getLayoutInflater().inflate(R.layout.view_link_vide, videoRoot);
        videoRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MehUtil.openPage(MainActivity.this, video.getUrl());
            }
        });
        ImageView playIcon = (ImageView) videoRoot.findViewById(R.id.video_play);
        TextView title = (TextView) videoRoot.findViewById(R.id.video_title);
        title.setText(video.getTitle());
        playIcon.getDrawable().setColorFilter(savedMehResponse.getDeal().getTheme().getAccentColor(), PorterDuff.Mode.MULTIPLY);
    }

    private void bindTheme(Deal deal, boolean animate) {
        Theme theme = deal.getTheme();
        int accentColor = theme.getAccentColor();
        int darkerAccentColor = ColorUtil.getDarkerColor(accentColor);
        int backgroundColor = theme.getBackgroundColor();
        int foreGround = theme.getForeground() == Theme.FOREGROUND_LIGHT ? Color.WHITE : Color.BLACK;
        int foreGroundInverse = theme.getForeground() == Theme.FOREGROUND_LIGHT ? Color.BLACK : Color.WHITE;
        title.setTextColor(foreGround);
        description.setTextColor(foreGround);
        if (deal.isSoldOut()) {
            buy.getBackground().setColorFilter(foreGround, PorterDuff.Mode.MULTIPLY);
            buy.setTextColor(foreGroundInverse);
        } else {
            buy.setSupportBackgroundTintList(ColorUtil.createColorStateList(accentColor, ColorUtil.getDarkerColor(accentColor)));
            buy.setTextColor(theme.getBackgroundColor());
        }
        fullSpecs.setTextColor(foreGround);
        storyTitle.setTextColor(accentColor);
        storyBody.setTextColor(foreGround);
        toolbarTitle.setTextColor(backgroundColor);

        View decorView = getWindow().getDecorView();
        if (animate) {
            ColorUtil.backgroundColor(toolbar, accentColor, ANIMATION_TIME);
            ColorUtil.animateStatusBarAndNavBarColors(getWindow(), darkerAccentColor, ANIMATION_TIME);
            ColorUtil.backgroundColor(decorView, backgroundColor, ANIMATION_TIME);
        } else {
            toolbar.setBackgroundColor(accentColor);
            ColorUtil.setStatusBarAndNavBarColor(getWindow(), darkerAccentColor);
            decorView.setBackgroundColor(backgroundColor);
        }

        if (menu != null) {
            colorMenuIcons(backgroundColor);
        }
    }

    private void colorMenuIcons(int color) {
        for (int i=0; i<menu.size(); i++) {
            Drawable icon = menu.getItem(i).getIcon();
            if (icon != null) {
                icon.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (savedMehResponse != null) {
            outState.putString(KEY_MEH_RESPONSE, gson.toJson(savedMehResponse));
        }
    }

    private void showError() {
        failedView.setVisibility(View.VISIBLE);
        SnackbarManager.show(
                Snackbar.with(MainActivity.this)
                        .text(R.string.error_with_server));
    }

    private void testNotification() {
        startService(new Intent(this, PostReminderService.class));
    }

    private void testMeh() {
        savedMehResponse = gson.fromJson(
                MehUtil.loadJSONFromAsset(this, "4-20-2015.json"), MehResponse.class);
        Timber.d(savedMehResponse.toString());
        bindDeal(savedMehResponse.getDeal(), true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        if (savedMehResponse != null && savedMehResponse.getDeal() != null) {
            colorMenuIcons(savedMehResponse.getDeal().getTheme().getBackgroundColor());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notifications:
                Intent intent = savedMehResponse == null ?
                        NotificationActivity.newInstance(this) :
                        NotificationActivity.newInstance(this, savedMehResponse.getDeal().getTheme());
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.do_nothing);
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
