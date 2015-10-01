package com.jawnnypoo.openmeh.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.Gson;
import com.jawnnypoo.openmeh.BuildConfig;
import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.adapters.ImageAdapter;
import com.jawnnypoo.openmeh.api.MehClient;
import com.jawnnypoo.openmeh.api.MehResponse;
import com.jawnnypoo.openmeh.data.Deal;
import com.jawnnypoo.openmeh.data.Theme;
import com.jawnnypoo.openmeh.data.Topic;
import com.jawnnypoo.openmeh.data.Video;
import com.jawnnypoo.openmeh.services.PostReminderService;
import com.jawnnypoo.openmeh.util.ColorUtil;
import com.jawnnypoo.openmeh.util.IntentUtil;
import com.jawnnypoo.openmeh.util.MehUtil;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.uncod.android.bypass.Bypass;
import me.relex.circleindicator.CircleIndicator;
import retrofit.Callback;
import retrofit.Response;
import timber.log.Timber;


public class MehActivity extends BaseActivity {

    private static final String KEY_MEH_RESPONSE = "KEY_MEH_RESPONSE";
    private static final int ANIMATION_TIME = 800;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbar_title) TextView toolbarTitle;
    @Bind(R.id.activity_root) View root;
    @Bind(R.id.progress) View progress;
    @Bind(R.id.failed) View failedView;
    @Bind(R.id.indicator) CircleIndicator indicator;
    @Bind(R.id.deal_image_background) ImageView imageBackground;
    @Bind(R.id.deal_image_view_pager) ViewPager imageViewPager;
    ImageAdapter imagePagerAdapter;
    @Bind(R.id.deal_buy_button) AppCompatButton buy;
    @Bind(R.id.deal_title) TextView title;
    @Bind(R.id.deal_description) TextView description;
    @Bind(R.id.deal_full_specs) TextView fullSpecs;
    @Bind(R.id.story_title) TextView storyTitle;
    @Bind(R.id.story_body) TextView storyBody;
    @Bind(R.id.video_root) ViewGroup videoRoot;

    YouTubePlayerSupportFragment youTubeFragment;

    Bypass bypass;
    MehResponse savedMehResponse;

    @OnClick(R.id.deal_full_specs)
    void onFullSpecsClick() {
        if (savedMehResponse != null && savedMehResponse.getDeal() != null) {
            Topic topic = savedMehResponse.getDeal().getTopic();
            if (topic != null && !TextUtils.isEmpty(topic.getUrl())) {
                IntentUtil.openUrl(this, topic.getUrl(), savedMehResponse.getDeal().getTheme().getAccentColor());
            }
        }
    }

    @OnClick(R.id.failed)
    void onErrorClick(){
        loadMeh();
    }

    private final Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_notifications:
                    Intent intent = savedMehResponse == null ?
                            NotificationActivity.newInstance(MehActivity.this) :
                            NotificationActivity.newInstance(MehActivity.this,
                                    savedMehResponse.getDeal().getTheme());
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.do_nothing);
                    return true;
                case R.id.action_share:
                    IntentUtil.shareDeal(root, savedMehResponse);
                    return true;
                case R.id.action_refresh:
                    loadMeh();
                    return true;
                case R.id.action_account:
                    int color = Color.WHITE;
                    if (savedMehResponse != null && savedMehResponse.getDeal() != null && savedMehResponse.getDeal().getTheme() != null) {
                        color = savedMehResponse.getDeal().getTheme().getAccentColor();
                    }
                    IntentUtil.openUrl(MehActivity.this, getString(R.string.url_account), color);
                    return true;
                case R.id.action_orders:
                    int colorWhichHasToHaveDifferentNameBecauseOfSwitchStatmentsWeirdScope = Color.WHITE;
                    if (savedMehResponse != null && savedMehResponse.getDeal() != null && savedMehResponse.getDeal().getTheme() != null) {
                        colorWhichHasToHaveDifferentNameBecauseOfSwitchStatmentsWeirdScope = savedMehResponse.getDeal().getTheme().getAccentColor();
                    }
                    IntentUtil.openUrl(MehActivity.this, getString(R.string.url_orders), colorWhichHasToHaveDifferentNameBecauseOfSwitchStatmentsWeirdScope);
                    return true;
                case R.id.action_forum:
                    int color2 = Color.WHITE;
                    if (savedMehResponse != null && savedMehResponse.getDeal() != null && savedMehResponse.getDeal().getTheme() != null) {
                        color2 = savedMehResponse.getDeal().getTheme().getAccentColor();
                    }
                    IntentUtil.openUrl(MehActivity.this, getString(R.string.url_forum), color2);
                    return true;

            }
            return false;
        }
    };

    private final Callback<MehResponse> mMehResponseCallback = new Callback<MehResponse>() {
        @Override
        public void onResponse(Response<MehResponse> response) {
            progress.setVisibility(View.GONE);
            if (!response.isSuccess() || response.body() == null || response.body().getDeal() == null) {
                Timber.e("There was a meh response, but it was null or the deal was null or something");
                showError();
                return;
            }
            savedMehResponse = response.body();
            bindDeal(savedMehResponse.getDeal(), true);
        }

        @Override
        public void onFailure(Throwable t) {
            progress.setVisibility(View.GONE);
            Timber.e(t.toString());
            showError();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meh);
        ButterKnife.bind(this);
        bypass = new Bypass(this);
        toolbarTitle.setText(R.string.app_name);
        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = savedMehResponse == null ?
                        AboutActivity.newInstance(MehActivity.this) :
                        AboutActivity.newInstance(MehActivity.this,
                                savedMehResponse.getDeal().getTheme());
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.do_nothing);
            }
        });
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(menuItemClickListener);
        imagePagerAdapter = new ImageAdapter();
        imageViewPager.setAdapter(imagePagerAdapter);

        youTubeFragment = YouTubePlayerSupportFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.video_root, youTubeFragment).commit();
        if (savedInstanceState != null) {
            savedMehResponse = Parcels.unwrap(savedInstanceState.getParcelable(KEY_MEH_RESPONSE));
            if (savedMehResponse != null) {
                Timber.d("Restored from savedInstanceState");
                bindDeal(savedMehResponse.getDeal(), false);
            }
        }
        //testMeh();
        //testNotification();
        if (savedMehResponse == null) {
            loadMeh();
        }
    }

    private void loadMeh() {
        progress.setVisibility(View.VISIBLE);
        failedView.setVisibility(View.GONE);
        root.setVisibility(View.GONE);
        imageBackground.setVisibility(View.GONE);
        MehClient.instance().getMeh().enqueue(mMehResponseCallback);
    }

    private void bindDeal(final Deal deal, boolean animate) {
        progress.setVisibility(View.GONE);
        failedView.setVisibility(View.GONE);
        imagePagerAdapter.setData(deal.getPhotos());
        indicator.setIndicatorColor(deal.getTheme().getForegroundColor());
        indicator.setViewPager(imageViewPager);
        if (deal.isSoldOut()) {
            buy.setEnabled(false);
            buy.setText(R.string.sold_out);
        } else {
            buy.setText(deal.getPriceRange() + "\n" + getString(R.string.buy_it));
            buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.openUrl(MehActivity.this, deal.getCheckoutUrl(), deal.getTheme().getAccentColor());
                }
            });
        }
        root.setVisibility(View.VISIBLE);
        imageBackground.setVisibility(View.VISIBLE);
        if (animate) {
            root.setAlpha(0f);
            root.animate().alpha(1.0f).setDuration(ANIMATION_TIME).setStartDelay(ANIMATION_TIME);
            imageBackground.setAlpha(0f);
            imageBackground.animate().alpha(1.0f).setStartDelay(ANIMATION_TIME).setDuration(ANIMATION_TIME).setStartDelay(ANIMATION_TIME);
        }
        title.setText(deal.getTitle());
        description.setText(markdownToCharSequence(deal.getFeatures()));
        description.setMovementMethod(LinkMovementMethod.getInstance());
        if (deal.getStory() != null) {
            storyTitle.setText(deal.getStory().getTitle());
            storyBody.setText(markdownToCharSequence(deal.getStory().getBody()));
            storyBody.setMovementMethod(LinkMovementMethod.getInstance());
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

        youTubeFragment.initialize(BuildConfig.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                Timber.d("onInitializationSuccess");
                if (!wasRestored) {
                    youTubePlayer.cueVideo(videoId);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Timber.d("onInitializationFailure");
                getSupportFragmentManager().beginTransaction().remove(youTubeFragment).commit();
                bindVideoLink(savedMehResponse.getVideo());
            }
        });
    }

    private void bindVideoLink(final Video video) {
        Timber.d("YouTube didn't work. Just link it");
        getSupportFragmentManager().beginTransaction().remove(youTubeFragment).commit();
        getLayoutInflater().inflate(R.layout.view_link_video, videoRoot);
        videoRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.openUrl(MehActivity.this, video.getUrl(), savedMehResponse.getDeal().getTheme().getAccentColor());
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
        description.setLinkTextColor(foreGround);

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
        storyBody.setLinkTextColor(foreGround);
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
        ColorUtil.setMenuItemsColor(toolbar.getMenu(), backgroundColor);
        Glide.with(this)
                .load(theme.getBackgroundImage())
                .into(imageBackground);
    }

    private CharSequence markdownToCharSequence(String markdownString) {
        return bypass.markdownToSpannable(markdownString);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (savedMehResponse != null) {
            outState.putParcelable(KEY_MEH_RESPONSE, Parcels.wrap(savedMehResponse));
        }
    }

    private void showError() {
        failedView.setVisibility(View.VISIBLE);
        Snackbar.make(root, R.string.error_with_server, Snackbar.LENGTH_SHORT)
            .show();
    }

    private void testNotification() {
        startService(new Intent(this, PostReminderService.class));
    }

    /**
     * Parse a fake API response, for testing
     */
    private void testMeh() {
        savedMehResponse = new Gson().fromJson(
                MehUtil.loadJSONFromAsset(this, "4-23-2015.json"), MehResponse.class);
        Timber.d(savedMehResponse.toString());
        bindDeal(savedMehResponse.getDeal(), true);
    }
}
