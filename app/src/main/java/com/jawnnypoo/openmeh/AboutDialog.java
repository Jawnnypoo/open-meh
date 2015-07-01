package com.jawnnypoo.openmeh;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.jawnnypoo.openmeh.github.Contributor;
import com.jawnnypoo.openmeh.github.GithubClient;
import com.jawnnypoo.openmeh.util.MehUtil;
import com.jawnnypoo.physicslayout.Physics;
import com.jawnnypoo.physicslayout.PhysicsConfig;
import com.jawnnypoo.physicslayout.PhysicsFrameLayout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Gotta give em credit...
 * Created by Jawn on 4/23/2015.
 */
public class AboutDialog extends AppCompatDialog {

    private static final String REPO_USER = "Jawnnypoo";
    private static final String REPO_NAME = "open-meh";

    @Bind(R.id.root)
    View root;
    @Bind(R.id.physics_layout)
    PhysicsFrameLayout physicsLayout;

    private SensorManager sensorManager;
    private Sensor gravitySensor;

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
                //TODO make this work
                //physicsLayout.getPhysics().set
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    };

    @OnClick(R.id.john_credit)
    void onJohnClick(View v) {
        MehUtil.openPage(root, getContext().getString(R.string.jawn_url));
    }
    @OnClick(R.id.kyrsten_credit)
    void onKyrstenClick(View v) {
        MehUtil.openPage(root, getContext().getString(R.string.kyrsten_url));
    }
    @OnClick(R.id.libraries_used)
    void onLibrariesUsedClicked(View v) {
        MehUtil.openPage(root, getContext().getString(R.string.apache_url));
    }
    @OnClick(R.id.library)
    void onLibraryClicked(View v) {
        MehUtil.openPage(root, getContext().getString(R.string.apache_url));
    }
    @OnClick(R.id.sauce)
    void onSourceClick(View v) {
        MehUtil.openPage(root, getContext().getString(R.string.source_url));
    }

    private final Callback<List<Contributor>> contributorResponseCallback = new Callback<List<Contributor>>() {
        @Override
        public void success(List<Contributor> contributorList, Response response) {
            addContributors(contributorList);
        }

        @Override
        public void failure(RetrofitError error) {
            Timber.e(error.toString());
        }
    };

    public AboutDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_about);
        ButterKnife.bind(this);
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        physicsLayout.getPhysics().enableFling();
        GithubClient.instance().contributors(REPO_USER, REPO_NAME, contributorResponseCallback);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        sensorManager.registerListener(sensorEventListener, gravitySensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        sensorManager.unregisterListener(sensorEventListener);
    }

    private void addContributors(List<Contributor> contributors) {
        PhysicsConfig config = new PhysicsConfig.Builder()
                .setShapeType(PhysicsConfig.ShapeType.CIRCLE)
                .setDensity(1.0f)
                .setFriction(0.0f)
                .setRestitution(0.0f)
                .build();
        int borderSize = getContext().getResources().getDimensionPixelSize(R.dimen.border_size);
        for (int i=0; i<contributors.size(); i++) {
            Contributor contributor = contributors.get(i);
            CircleImageView imageView = new CircleImageView(getContext());
            FrameLayout.LayoutParams llp = new FrameLayout.LayoutParams(
                    getContext().getResources().getDimensionPixelSize(R.dimen.contributor_size),
                    getContext().getResources().getDimensionPixelSize(R.dimen.contributor_size));
            imageView.setLayoutParams(llp);
            imageView.setBorderWidth(borderSize);
            imageView.setBorderColor(Color.BLACK);
            Physics.setPhysicsConfig(imageView, config);
            physicsLayout.addView(imageView);

            Glide.with(getContext())
                    .load(contributor.avatarUrl)
                    .into(imageView);
        }
        physicsLayout.getPhysics().onLayout(true);
    }
}
