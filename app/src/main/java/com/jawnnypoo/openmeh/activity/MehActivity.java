package com.jawnnypoo.openmeh.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.commit451.easel.Easel;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.Gson;
import com.jawnnypoo.openmeh.BuildConfig;
import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.adapter.ImageAdapter;
import com.jawnnypoo.openmeh.api.MehClient;
import com.jawnnypoo.openmeh.service.PostReminderService;
import com.jawnnypoo.openmeh.shared.api.MehResponse;
import com.jawnnypoo.openmeh.shared.model.Deal;
import com.jawnnypoo.openmeh.shared.model.Theme;
import com.jawnnypoo.openmeh.shared.model.Topic;
import com.jawnnypoo.openmeh.shared.model.Video;
import com.jawnnypoo.openmeh.shared.util.AssetUtil;
import com.jawnnypoo.openmeh.util.ColorUtil;
import com.jawnnypoo.openmeh.util.GlideImageGetter;
import com.jawnnypoo.openmeh.util.IntentUtil;
import com.jawnnypoo.openmeh.util.MehUtil;
import com.jawnnypoo.openmeh.util.NavigationManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.uncod.android.bypass.Bypass;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Activity that shows the meh.com deal of the day
 */
public class MehActivity extends BaseActivity {

    private static final String STATE_MEH_RESPONSE = "STATE_MEH_RESPONSE";
    private static final String EXTRA_BUY_NOW = "key_meh_response";
    private static final int ANIMATION_TIME = 800;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MehActivity.class);
        return intent;
    }

    public static Intent newIntentForInstaBuy(Context context) {
        Intent intent = new Intent(context, MehActivity.class);
        intent.putExtra(EXTRA_BUY_NOW, true);
        return intent;
    }

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.activity_root) View mRoot;
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout mSwipeRefreshLayout;
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
    YouTubePlayer mYouTubePlayer;

    Bypass mBypass;
    MehResponse mSavedMehResponse;
    boolean mFullScreen = false;
    boolean mBuyOnLoad = false;

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

    private YouTubePlayer.OnFullscreenListener mOnFullscreenListener = new YouTubePlayer.OnFullscreenListener() {
        @Override
        public void onFullscreen(boolean b) {
            mFullScreen = b;
        }
    };

    private final Toolbar.OnMenuItemClickListener mMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Theme theme = null;
            if (mSavedMehResponse != null && mSavedMehResponse.getDeal() != null) {
                theme = mSavedMehResponse.getDeal().getTheme();
            }
            int accentColor = theme == null ? Color.WHITE : theme.getAccentColor();
            switch (item.getItemId()) {
                case R.id.nav_notifications:
                    NavigationManager.navigateToNotifications(MehActivity.this, theme);
                    return true;
                case R.id.action_share:
                    IntentUtil.shareDeal(mRoot, mSavedMehResponse);
                    return true;
                case R.id.action_refresh:
                    loadMeh();
                    return true;
                case R.id.nav_about:
                    NavigationManager.navigateToAbout(MehActivity.this, theme);
                    return true;
                case R.id.nav_account:
                    IntentUtil.openUrl(MehActivity.this, getString(R.string.url_account), accentColor);
                    return true;
                case R.id.nav_forum:
                    IntentUtil.openUrl(MehActivity.this, getString(R.string.url_forum), accentColor);
                    return true;
                case R.id.nav_orders:
                    IntentUtil.openUrl(MehActivity.this, getString(R.string.url_orders), accentColor);
                    return true;
            }
            return false;
        }
    };

    private final Callback<MehResponse> mMehResponseCallback = new Callback<MehResponse>() {
        @Override
        public void onResponse(Call<MehResponse> call, Response<MehResponse> response) {
            mSwipeRefreshLayout.setEnabled(false);
            mSwipeRefreshLayout.setRefreshing(false);
            if (!response.isSuccessful() || response.body() == null || response.body().getDeal() == null) {
                Timber.e("There was a meh response, but it was null or the deal was null or something");
                showError();
                return;
            }
            mSavedMehResponse = response.body();
            bindDeal(mSavedMehResponse.getDeal(), true);
            if (mBuyOnLoad) {
                mBuyButton.callOnClick();
                mBuyOnLoad = false;
            }
        }

        @Override
        public void onFailure(Call<MehResponse> call, Throwable t) {
            mSwipeRefreshLayout.setEnabled(false);
            mSwipeRefreshLayout.setRefreshing(false);
            Timber.e(t.toString());
            showError();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLayoutInflater().setFactory(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meh);
        ButterKnife.bind(this);
        mBypass = new Bypass(this);
        mToolbar.setTitle(R.string.app_name);
        mToolbar.inflateMenu(R.menu.menu_main);
        mToolbar.setOnMenuItemClickListener(mMenuItemClickListener);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelOffset(R.dimen.swipe_refresh_offset));
        mImagePagerAdapter = new ImageAdapter();
        mImageViewPager.setAdapter(mImagePagerAdapter);

        mYouTubeFragment = YouTubePlayerSupportFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.video_root, mYouTubeFragment).commit();
        if (savedInstanceState != null) {
            mSavedMehResponse = savedInstanceState.getParcelable(STATE_MEH_RESPONSE);
            if (mSavedMehResponse != null) {
                Timber.d("Restored from savedInstanceState");
                bindDeal(mSavedMehResponse.getDeal(), false);
            }
        } else {
            mBuyOnLoad = getIntent().getBooleanExtra(EXTRA_BUY_NOW, false);
            loadMeh();
        }
        //testMeh();
        //testNotification();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mBuyOnLoad = intent.getBooleanExtra(EXTRA_BUY_NOW, false);
        loadMeh();
    }

    private void loadMeh() {
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setRefreshing(true);
        mFailedView.setVisibility(View.GONE);
        mRoot.setVisibility(View.GONE);
        mImageBackground.setVisibility(View.GONE);
        MehClient.instance().getMeh().enqueue(mMehResponseCallback);
    }

    private void bindDeal(final Deal deal, boolean animate) {
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setRefreshing(false);
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
                mYouTubePlayer = youTubePlayer;
                if (!wasRestored) {
                    youTubePlayer.cueVideo(videoId);
                }
                youTubePlayer.setOnFullscreenListener(mOnFullscreenListener);
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
        int darkerAccentColor = Easel.getDarkerColor(accentColor);
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
            mBuyButton.setSupportBackgroundTintList(ColorUtil.createColorStateList(accentColor, Easel.getDarkerColor(accentColor)));
            mBuyButton.setTextColor(theme.getBackgroundColor());
        }
        mFullSpecsTextView.setTextColor(foreGround);
        mStoreTitleTextView.setTextColor(accentColor);
        mStoryBodyTextView.setTextColor(foreGround);
        mStoryBodyTextView.setLinkTextColor(foreGround);
        mToolbar.setTitleTextColor(backgroundColor);

        View decorView = getWindow().getDecorView();
        if (animate) {
            Easel.getBackgroundColorAnimator(mToolbar, accentColor)
                .setDuration(ANIMATION_TIME)
                .start();
            if (Build.VERSION.SDK_INT >= 21) {
                Easel.getStatusBarColorAnimator(getWindow(), darkerAccentColor)
                        .setDuration(ANIMATION_TIME)
                        .start();
                Easel.getNavigationBarColorAnimator(getWindow(), darkerAccentColor)
                        .setDuration(ANIMATION_TIME)
                        .start();
            }
            Easel.getBackgroundColorAnimator(decorView, backgroundColor)
                    .setDuration(ANIMATION_TIME)
                    .start();
        } else {
            mToolbar.setBackgroundColor(accentColor);
            if (Build.VERSION.SDK_INT >= 21) {
                getWindow().setStatusBarColor(darkerAccentColor);
                getWindow().setNavigationBarColor(darkerAccentColor);
            }
            decorView.setBackgroundColor(backgroundColor);
        }
        Easel.setTint(mToolbar.getMenu(), backgroundColor);
        Easel.setOverflowTint(this, backgroundColor);
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
            outState.putParcelable(STATE_MEH_RESPONSE, mSavedMehResponse);
        }
    }

    @Override
    public void onBackPressed() {
        if (mFullScreen) {
            mYouTubePlayer.setFullscreen(false);
            return;
        } else {
            super.onBackPressed();
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
                AssetUtil.loadJSONFromAsset(this, "4-23-2015.json"), MehResponse.class);
        Timber.d(mSavedMehResponse.toString());
        bindDeal(mSavedMehResponse.getDeal(), true);
    }
}
