package com.jawnnypoo.openmeh.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.commit451.elasticdragdismisslayout.ElasticDragDismissFrameLayout;
import com.commit451.elasticdragdismisslayout.ElasticDragDismissListener;
import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.adapter.ImageAdapter;
import com.jawnnypoo.openmeh.shared.model.Theme;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

/**
 * Shows the full screen images
 */
public class FullScreenImageViewerActivity extends BaseActivity {

    private static final String EXTRA_IMAGES = "images";

    public static Intent newInstance(Context context, @Nullable Theme theme, ArrayList<String> images) {
        Intent intent = new Intent(context, FullScreenImageViewerActivity.class);
        if (theme != null) {
            intent.putExtra(EXTRA_THEME, theme);
        }
        intent.putExtra(EXTRA_IMAGES, images);
        return intent;
    }

    @BindView(R.id.images)
    ViewPager mImageViewPager;
    @BindView(R.id.indicator)
    CircleIndicator mIndicator;
    @BindView(R.id.draggable_frame)
    ElasticDragDismissFrameLayout mDraggableFrame;

    ImageAdapter mImagePagerAdapter;

    @OnClick(R.id.close)
    void onCloseClicked() {
        onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image_viewer);
        ButterKnife.bind(this);

        ArrayList<String> images = getIntent().getStringArrayListExtra(EXTRA_IMAGES);

        Theme theme = getIntent().getParcelableExtra(EXTRA_THEME);


        mImagePagerAdapter = new ImageAdapter(true, new ImageAdapter.Listener() {
            @Override
            public void onImageClicked(View view, int position) {

            }
        });
        mImageViewPager.setAdapter(mImagePagerAdapter);
        mImagePagerAdapter.setData(images);
        if (theme != null) {
            mDraggableFrame.setBackgroundColor(theme.getBackgroundColor());
            mIndicator.setIndicatorColor(theme.getForegroundColor());
        }
        mIndicator.setViewPager(mImageViewPager);
        mDraggableFrame.addListener(new ElasticDragDismissListener() {
            @Override
            public void onDrag(float elasticOffset, float elasticOffsetPixels, float rawOffset, float rawOffsetPixels) {}

            @Override
            public void onDragDismissed() {
                //if you are targeting 21+ you might want to finish after transition
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }
}
