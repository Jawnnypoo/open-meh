package com.jawnnypoo.openmeh.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.commit451.easel.Easel;
import com.commit451.gitbal.Gimbal;
import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.github.Contributor;
import com.jawnnypoo.openmeh.github.GithubClient;
import com.jawnnypoo.openmeh.shared.model.Theme;
import com.jawnnypoo.openmeh.util.IntentUtil;
import com.jawnnypoo.physicslayout.Physics;
import com.jawnnypoo.physicslayout.PhysicsConfig;
import com.jawnnypoo.physicslayout.PhysicsFrameLayout;

import org.jbox2d.common.Vec2;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Thats what its all about
 */
public class AboutActivity extends BaseActivity {

    private static final String REPO_USER = "Jawnnypoo";
    private static final String REPO_NAME = "open-meh";

    public static Intent newInstance(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        return intent;
    }

    public static Intent newInstance(Context context, @Nullable Theme theme) {
        Intent intent = new Intent(context, AboutActivity.class);
        if (theme != null) {
            intent.putExtra(EXTRA_THEME, theme);
        }
        return intent;
    }

    @Bind(R.id.root)
    View mRoot;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mToolbarTitle;
    @Bind(R.id.contributors)
    TextView mContributors;
    @Bind(R.id.physics_layout)
    PhysicsFrameLayout mPhysicsLayout;
    @OnClick(R.id.sauce)
    void onSauceClick() {
        IntentUtil.openUrl(this, getString(R.string.source_url), mTheme == null ? Color.WHITE : mTheme.getAccentColor());
    }

    SensorManager mSensorManager;
    Sensor mGravitySensor;
    Theme mTheme;
    Gimbal mGimbal;

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
                if (mPhysicsLayout.getPhysics().getWorld() != null) {
                    mGimbal.normalizeGravityEvent(event);
                    mPhysicsLayout.getPhysics().getWorld().setGravity(new Vec2(-event.values[0], event.values[1]));
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    };

    private final Callback<List<Contributor>> contributorResponseCallback = new Callback<List<Contributor>>() {

        @Override
        public void onResponse(Call<List<Contributor>> call, final Response<List<Contributor>> response) {
            if (response.isSuccessful()) {
                mPhysicsLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        addContributors(response.body());
                    }
                });
            }
        }

        @Override
        public void onFailure(Call<List<Contributor>> call, Throwable t) {
            Timber.e(t.toString());
            Snackbar.make(getWindow().getDecorView(), R.string.error_getting_contributors, Snackbar.LENGTH_SHORT)
                    .show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGimbal = new Gimbal(this);
        mGimbal.lock();
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mToolbarTitle.setText(R.string.about);
        mPhysicsLayout.getPhysics().enableFling();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        GithubClient.instance().contributors(REPO_USER, REPO_NAME).enqueue(contributorResponseCallback);
        mTheme = getIntent().getParcelableExtra(EXTRA_THEME);
        if (mTheme != null) {
            applyTheme(mTheme);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mGravitySensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.do_nothing, R.anim.fade_out);
    }

    private void applyTheme(Theme theme) {
        //Tint widgets
        int accentColor = theme.getAccentColor();
        int foreGround = theme.getForeground() == Theme.FOREGROUND_LIGHT ? Color.WHITE : Color.BLACK;
        mToolbarTitle.setTextColor(theme.getBackgroundColor());
        mToolbar.setBackgroundColor(accentColor);
        mToolbar.getNavigationIcon().setColorFilter(theme.getBackgroundColor(), PorterDuff.Mode.MULTIPLY);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Easel.getDarkerColor(accentColor));
            getWindow().setNavigationBarColor(Easel.getDarkerColor(accentColor));
        }
        getWindow().getDecorView().setBackgroundColor(theme.getBackgroundColor());
    }

    private void addContributors(List<Contributor> contributors) {
        PhysicsConfig config = new PhysicsConfig.Builder()
                .setShapeType(PhysicsConfig.ShapeType.CIRCLE)
                .setDensity(1.0f)
                .setFriction(0.0f)
                .setRestitution(0.0f)
                .build();
        int borderSize = getResources().getDimensionPixelSize(R.dimen.border_size);
        int x = 0;
        int y = 0;
        int imageSize = getResources().getDimensionPixelSize(R.dimen.circle_size);
        for (int i=0; i<contributors.size(); i++) {
            Contributor contributor = contributors.get(i);
            CircleImageView imageView = new CircleImageView(this);
            FrameLayout.LayoutParams llp = new FrameLayout.LayoutParams(
                    imageSize,
                    imageSize);
            imageView.setLayoutParams(llp);
            imageView.setBorderWidth(borderSize);
            imageView.setBorderColor(Color.BLACK);
            Physics.setPhysicsConfig(imageView, config);
            mPhysicsLayout.addView(imageView);
            imageView.setX(x);
            imageView.setY(y);

            x = (x + imageSize);
            if (x > mPhysicsLayout.getWidth()) {
                x = 0;
                y = (y + imageSize) % mPhysicsLayout.getHeight();
            }
            Glide.with(this)
                    .load(contributor.avatarUrl)
                    .into(imageView);
        }
        mPhysicsLayout.getPhysics().onLayout(true);
    }
}
