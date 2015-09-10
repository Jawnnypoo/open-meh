package com.jawnnypoo.openmeh;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jawnnypoo.openmeh.data.Theme;
import com.jawnnypoo.openmeh.github.Contributor;
import com.jawnnypoo.openmeh.github.GithubClient;
import com.jawnnypoo.openmeh.util.ColorUtil;
import com.jawnnypoo.openmeh.util.IntentUtil;
import com.jawnnypoo.openmeh.util.WindowUtil;
import com.jawnnypoo.physicslayout.Physics;
import com.jawnnypoo.physicslayout.PhysicsConfig;
import com.jawnnypoo.physicslayout.PhysicsFrameLayout;

import org.jbox2d.common.Vec2;
import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Thats what its all about
 * Created by Jawn on 7/15/2015.
 */
public class AboutActivity extends BaseActivity {

    private static final String REPO_USER = "Jawnnypoo";
    private static final String REPO_NAME = "open-meh";

    public static Intent newInstance(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        return intent;
    }

    public static Intent newInstance(Context context, Theme theme) {
        Intent intent = new Intent(context, AboutActivity.class);
        if (theme != null) {
            intent.putExtra(EXTRA_THEME, Parcels.wrap(theme));
        }
        return intent;
    }

    @Bind(R.id.root)
    View root;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.contributors)
    TextView contributors;
    @Bind(R.id.physics_layout)
    PhysicsFrameLayout physicsLayout;
    @OnClick(R.id.sauce)
    void onSauceClick() {
        IntentUtil.openUrl(this, getString(R.string.source_url), mTheme == null ? Color.WHITE : mTheme.getAccentColor());
    }

    SensorManager sensorManager;
    Sensor gravitySensor;
    Theme mTheme;

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
                if (physicsLayout.getPhysics().getWorld() != null) {
                    WindowUtil.normalizeForOrientation(getWindow(), event);
                    physicsLayout.getPhysics().getWorld().setGravity(new Vec2(-event.values[0], event.values[1]));
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    };

    private final Callback<List<Contributor>> contributorResponseCallback = new Callback<List<Contributor>>() {
        @Override
        public void success(List<Contributor> contributorList, Response response) {
            addContributors(contributorList);
        }

        @Override
        public void failure(RetrofitError error) {
            error.printStackTrace();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtil.lockToCurrentOrientation(this);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbarTitle.setText(R.string.about);
        physicsLayout.getPhysics().enableFling();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        GithubClient.instance().contributors(REPO_USER, REPO_NAME, contributorResponseCallback);
        mTheme = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_THEME));
        if (mTheme != null) {
            applyTheme(mTheme);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, gravitySensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
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
        toolbarTitle.setTextColor(theme.getBackgroundColor());
        toolbar.setBackgroundColor(accentColor);
        toolbar.getNavigationIcon().setColorFilter(theme.getBackgroundColor(), PorterDuff.Mode.MULTIPLY);
        ColorUtil.setStatusBarAndNavBarColor(getWindow(), ColorUtil.getDarkerColor(accentColor));
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
            physicsLayout.addView(imageView);
            imageView.setX(x);
            imageView.setY(y);

            x = (x + imageSize);
            if (x > physicsLayout.getWidth()) {
                x = 0;
                y = (y + imageSize) % physicsLayout.getHeight();
            }
            Glide.with(this)
                    .load(contributor.avatarUrl)
                    .into(imageView);
        }
        physicsLayout.getPhysics().onLayout(true);
    }
}
