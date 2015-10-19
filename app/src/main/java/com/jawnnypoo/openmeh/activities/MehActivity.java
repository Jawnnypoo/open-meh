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
import com.jawnnypoo.openmeh.util.GlideImageGetter;
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
import retrofit.Retrofit;
import timber.log.Timber;


public class MehActivity extends BaseActivity {

    private static final String KEY_MEH_RESPONSE = "KEY_MEH_RESPONSE";
    private static final int ANIMATION_TIME = 800;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.toolbar_title) TextView mToolbarTitle;
    @Bind(R.id.activity_root) View mRoot;
    @Bind(R.id.progress) View mProgress;
    @Bind(R.id.failed) View mFailedView;
    @Bind(R.id.indicator) CircleIndicator mIndicator;
    @Bind(R.id.deal_image_background) ImageView mImageBackground;
    @Bind(R.id.deal_image_view_pager) ViewPager mImageViewPager;
    ImageAdapter mImagePagerAdapter;
    @Bind(R.id.deal_buy_button) AppCompatButton mBuyButton;
    @Bind(R.id.deal_title) TextView mTitleTextView;
    @Bind(R.id.deal_description) TextView mDescriptionTextView;
    @Bind(R.id.deal_full_specs) TextView mFullSpecsTextView;
    @Bind(R.id.story_title) TextView mStoreTitleTextView;
    @Bind(R.id.story_body) TextView mStoryBodyTextView;
    @Bind(R.id.video_root) ViewGroup mVideoRoot;

    YouTubePlayerSupportFragment mYouTubeFragment;

    Bypass mBypass;
    MehResponse mSavedMehResponse;

    @OnClick(R.id.deal_full_specs)
    void onFullSpecsClick() {
        if (mSavedMehResponse != null && mSavedMehResponse.getDeal() != null) {
            Topic topic = mSavedMehResponse.getDeal().getTopic();
            if (topic != null && !TextUtils.isEmpty(topic.getUrl())) {
                IntentUtil.openUrl(this, topic.getUrl(), mSavedMehResponse.getDeal().getTheme().getAccentColor());
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
                    Intent intent = mSavedMehResponse == null ?
                            NotificationActivity.newInstance(MehActivity.this) :
                            NotificationActivity.newInstance(MehActivity.this,
                                    mSavedMehResponse.getDeal().getTheme());
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.do_nothing);
                    return true;
                case R.id.action_share:
                    IntentUtil.shareDeal(mRoot, mSavedMehResponse);
                    return true;
                case R.id.action_refresh:
                    loadMeh();
                    return true;
                case R.id.action_account:
                    int color = Color.WHITE;
                    if (mSavedMehResponse != null && mSavedMehResponse.getDeal() != null && mSavedMehResponse.getDeal().getTheme() != null) {
                        color = mSavedMehResponse.getDeal().getTheme().getAccentColor();
                    }
                    IntentUtil.openUrl(MehActivity.this, getString(R.string.url_account), color);
                    return true;
                case R.id.action_orders:
                    int colorWhichHasToHaveDifferentNameBecauseOfSwitchStatmentsWeirdScope = Color.WHITE;
                    if (mSavedMehResponse != null && mSavedMehResponse.getDeal() != null && mSavedMehResponse.getDeal().getTheme() != null) {
                        colorWhichHasToHaveDifferentNameBecauseOfSwitchStatmentsWeirdScope = mSavedMehResponse.getDeal().getTheme().getAccentColor();
                    }
                    IntentUtil.openUrl(MehActivity.this, getString(R.string.url_orders), colorWhichHasToHaveDifferentNameBecauseOfSwitchStatmentsWeirdScope);
                    return true;
                case R.id.action_forum:
                    int color2 = Color.WHITE;
                    if (mSavedMehResponse != null && mSavedMehResponse.getDeal() != null && mSavedMehResponse.getDeal().getTheme() != null) {
                        color2 = mSavedMehResponse.getDeal().getTheme().getAccentColor();
                    }
                    IntentUtil.openUrl(MehActivity.this, getString(R.string.url_forum), color2);
                    return true;

            }
            return false;
        }
    };

    private final Callback<MehResponse> mMehResponseCallback = new Callback<MehResponse>() {
        @Override
        public void onResponse(Response<MehResponse> response, Retrofit retrofit) {
            mProgress.setVisibility(View.GONE);
            if (!response.isSuccess() || response.body() == null || response.body().getDeal() == null) {
                Timber.e("There was a meh response, but it was null or the deal was null or something");
                showError();
                return;
            }
            mSavedMehResponse = response.body();
            bindDeal(mSavedMehResponse.getDeal(), true);
        }

        @Override
        public void onFailure(Throwable t) {
            mProgress.setVisibility(View.GONE);
            Timber.e(t.toString());
            showError();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meh);
        ButterKnife.bind(this);
        mBypass = new Bypass(this);
        mToolbarTitle.setText(R.string.app_name);
        mToolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = mSavedMehResponse == null ?
                        AboutActivity.newInstance(MehActivity.this) :
                        AboutActivity.newInstance(MehActivity.this,
                                mSavedMehResponse.getDeal().getTheme());
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.do_nothing);
            }
        });
        mToolbar.inflateMenu(R.menu.menu_main);
        mToolbar.setOnMenuItemClickListener(menuItemClickListener);
        mImagePagerAdapter = new ImageAdapter();
        mImageViewPager.setAdapter(mImagePagerAdapter);

        mYouTubeFragment = YouTubePlayerSupportFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.video_root, mYouTubeFragment).commit();
        if (savedInstanceState != null) {
            mSavedMehResponse = Parcels.unwrap(savedInstanceState.getParcelable(KEY_MEH_RESPONSE));
            if (mSavedMehResponse != null) {
                Timber.d("Restored from savedInstanceState");
                bindDeal(mSavedMehResponse.getDeal(), false);
            }
        }
        //testMeh();
        //testNotification();
        if (mSavedMehResponse == null) {
            loadMeh();
        }
    }

    private void loadMeh() {
        mProgress.setVisibility(View.VISIBLE);
        mFailedView.setVisibility(View.GONE);
        mRoot.setVisibility(View.GONE);
        mImageBackground.setVisibility(View.GONE);
        MehClient.instance().getMeh().enqueue(mMehResponseCallback);
    }

    private void bindDeal(final Deal deal, boolean animate) {
        mProgress.setVisibility(View.GONE);
        mFailedView.setVisibility(View.GONE);
        mImagePagerAdapter.setData(deal.getPhotos());
        mIndicator.setIndicatorColor(deal.getTheme().getForegroundColor());
        mIndicator.setViewPager(mImageViewPager);
        if (deal.isSoldOut()) {
            mBuyButton.setEnabled(false);
            mBuyButton.setText(R.string.sold_out);
        } else {
            mBuyButton.setText(deal.getPriceRange() + "\n" + getString(R.string.buy_it));
            mBuyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.openUrl(MehActivity.this, deal.getCheckoutUrl(), deal.getTheme().getAccentColor());
                }
            });
        }
        mRoot.setVisibility(View.VISIBLE);
        mImageBackground.setVisibility(View.VISIBLE);
        if (animate) {
            mRoot.setAlpha(0f);
            mRoot.animate().alpha(1.0f).setDuration(ANIMATION_TIME).setStartDelay(ANIMATION_TIME);
            mImageBackground.setAlpha(0f);
            mImageBackground.animate().alpha(1.0f).setStartDelay(ANIMATION_TIME).setDuration(ANIMATION_TIME).setStartDelay(ANIMATION_TIME);
        }
        mTitleTextView.setText(deal.getTitle());
        mDescriptionTextView.setText(markdownToCharSequence(mDescriptionTextView, deal.getFeatures()));
        mDescriptionTextView.setMovementMethod(LinkMovementMethod.getInstance());
        if (deal.getStory() != null) {
            mStoreTitleTextView.setText(deal.getStory().getTitle());
            mStoryBodyTextView.setText(markdownToCharSequence(mStoryBodyTextView, deal.getStory().getBody()));
            mStoryBodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        if (mSavedMehResponse.getVideo() != null) {
            bindVideo(mSavedMehResponse.getVideo());
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

        mYouTubeFragment.initialize(BuildConfig.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
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
                getSupportFragmentManager().beginTransaction().remove(mYouTubeFragment).commit();
                bindVideoLink(mSavedMehResponse.getVideo());
            }
        });
    }

    private void bindVideoLink(final Video video) {
        Timber.d("YouTube didn't work. Just link it");
        getSupportFragmentManager().beginTransaction().remove(mYouTubeFragment).commitAllowingStateLoss();
        getLayoutInflater().inflate(R.layout.view_link_video, mVideoRoot);
        mVideoRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.openUrl(MehActivity.this, video.getUrl(), mSavedMehResponse.getDeal().getTheme().getAccentColor());
            }
        });
        ImageView playIcon = (ImageView) mVideoRoot.findViewById(R.id.video_play);
        TextView title = (TextView) mVideoRoot.findViewById(R.id.video_title);
        title.setText(video.getTitle());
        playIcon.getDrawable().setColorFilter(mSavedMehResponse.getDeal().getTheme().getAccentColor(), PorterDuff.Mode.MULTIPLY);
    }

    private void bindTheme(Deal deal, boolean animate) {
        Theme theme = deal.getTheme();
        int accentColor = theme.getAccentColor();
        int darkerAccentColor = ColorUtil.getDarkerColor(accentColor);
        int backgroundColor = theme.getBackgroundColor();
        int foreGround = theme.getForeground() == Theme.FOREGROUND_LIGHT ? Color.WHITE : Color.BLACK;
        int foreGroundInverse = theme.getForeground() == Theme.FOREGROUND_LIGHT ? Color.BLACK : Color.WHITE;

        mTitleTextView.setTextColor(foreGround);
        mDescriptionTextView.setTextColor(foreGround);
        mDescriptionTextView.setLinkTextColor(foreGround);

        if (deal.isSoldOut()) {
            mBuyButton.getBackground().setColorFilter(foreGround, PorterDuff.Mode.MULTIPLY);
            mBuyButton.setTextColor(foreGroundInverse);
        } else {
            mBuyButton.setSupportBackgroundTintList(ColorUtil.createColorStateList(accentColor, ColorUtil.getDarkerColor(accentColor)));
            mBuyButton.setTextColor(theme.getBackgroundColor());
        }
        mFullSpecsTextView.setTextColor(foreGround);
        mStoreTitleTextView.setTextColor(accentColor);
        mStoryBodyTextView.setTextColor(foreGround);
        mStoryBodyTextView.setLinkTextColor(foreGround);
        mToolbarTitle.setTextColor(backgroundColor);

        View decorView = getWindow().getDecorView();
        if (animate) {
            ColorUtil.backgroundColor(mToolbar, accentColor, ANIMATION_TIME);
            ColorUtil.animateStatusBarAndNavBarColors(getWindow(), darkerAccentColor, ANIMATION_TIME);
            ColorUtil.backgroundColor(decorView, backgroundColor, ANIMATION_TIME);
        } else {
            mToolbar.setBackgroundColor(accentColor);
            ColorUtil.setStatusBarAndNavBarColor(getWindow(), darkerAccentColor);
            decorView.setBackgroundColor(backgroundColor);
        }
        ColorUtil.setMenuItemsColor(mToolbar.getMenu(), backgroundColor);
        ColorUtil.setOverflowColor(this, backgroundColor);
        Glide.with(this)
                .load(theme.getBackgroundImage())
                .into(mImageBackground);
    }

    private CharSequence markdownToCharSequence(TextView textView, String markdownString) {
        return mBypass.markdownToSpannable(markdownString, new GlideImageGetter(this, textView));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSavedMehResponse != null) {
            outState.putParcelable(KEY_MEH_RESPONSE, Parcels.wrap(mSavedMehResponse));
        }
    }

    private void showError() {
        mFailedView.setVisibility(View.VISIBLE);
        Snackbar.make(mRoot, R.string.error_with_server, Snackbar.LENGTH_SHORT)
            .show();
    }

    private void testNotification() {
        startService(new Intent(this, PostReminderService.class));
    }

    /**
     * Parse a fake API response, for testing
     */
    private void testMeh() {
        mSavedMehResponse = new Gson().fromJson(
                MehUtil.loadJSONFromAsset(this, "4-23-2015.json"), MehResponse.class);
        Timber.d(mSavedMehResponse.toString());
        bindDeal(mSavedMehResponse.getDeal(), true);
    }
}
