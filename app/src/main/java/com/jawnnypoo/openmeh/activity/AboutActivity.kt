package com.jawnnypoo.openmeh.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.commit451.easel.Easel
import com.commit451.gimbal.Gimbal
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.extension.bind
import com.jawnnypoo.openmeh.github.Contributor
import com.jawnnypoo.openmeh.github.GitHubClient
import com.jawnnypoo.openmeh.model.ParsedTheme
import com.jawnnypoo.openmeh.shared.model.Theme
import com.jawnnypoo.openmeh.util.IntentUtil
import com.jawnnypoo.physicslayout.Physics
import com.jawnnypoo.physicslayout.PhysicsConfig
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_about.*
import org.jbox2d.common.Vec2
import timber.log.Timber

/**
 * That's what its all about
 */
class AboutActivity : BaseActivity() {

    companion object {

        private const val REPO_USER = "Jawnnypoo"
        private const val REPO_NAME = "open-meh"

        fun newInstance(context: Context, theme: ParsedTheme?): Intent {
            val intent = Intent(context, AboutActivity::class.java)
            if (theme != null) {
                intent.putExtra(EXTRA_THEME, theme)
            }
            return intent
        }
    }

    private lateinit var sensorManager: SensorManager
    private var gravitySensor: Sensor? = null
    private lateinit var gimbal: Gimbal
    private var theme: ParsedTheme? = null

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_GRAVITY) {
                if (physicsLayout.physics.world != null) {
                    gimbal.normalizeGravityEvent(event)
                    physicsLayout.physics.world.gravity = Vec2(-event.values[0], event.values[1])
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gimbal = Gimbal(this)
        gimbal.lock()
        setContentView(R.layout.activity_about)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        textToolbarTitle.setText(R.string.about)
        physicsLayout.physics.enableFling()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        theme = intent.getParcelableExtra<ParsedTheme>(EXTRA_THEME)
        theme?.let {
            applyTheme(it)
        }

        GitHubClient.contributors(REPO_USER, REPO_NAME)
                .bind(this)
                .subscribe({
                    physicsLayout.post { addContributors(it) }
                }, {
                    Timber.e(it)
                    Snackbar.make(window.decorView, R.string.error_getting_contributors, Snackbar.LENGTH_SHORT)
                        .show()
                })

        rootSource.setOnClickListener {
            val color = theme?.safeAccentColor() ?: Color.WHITE
            IntentUtil.openUrl(this, getString(R.string.source_url), color)
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(sensorEventListener, gravitySensor, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorEventListener)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.do_nothing, R.anim.fade_out)
    }

    private fun applyTheme(theme: ParsedTheme) {
        //Tint widgets
        val accentColor = theme.safeAccentColor()
        textToolbarTitle.setTextColor(theme.safeBackgroundColor())
        toolbar.setBackgroundColor(theme.safeAccentColor())
        toolbar.navigationIcon?.setColorFilter(theme.safeBackgroundColor(), PorterDuff.Mode.MULTIPLY)
        if (Build.VERSION.SDK_INT >= 21) {
            window.statusBarColor = Easel.darkerColor(accentColor)
            window.navigationBarColor = Easel.darkerColor(accentColor)
        }
        window.decorView.setBackgroundColor(theme.safeBackgroundColor())
    }

    private fun addContributors(contributors: List<Contributor>) {
        val config = PhysicsConfig.create()
        config.shapeType = PhysicsConfig.SHAPE_TYPE_CIRCLE
        val borderSize = resources.getDimensionPixelSize(R.dimen.border_size)
        var x = 0
        var y = 0
        val imageSize = resources.getDimensionPixelSize(R.dimen.circle_size)
        for (i in contributors.indices) {
            val contributor = contributors[i]
            val imageView = CircleImageView(this)
            val llp = FrameLayout.LayoutParams(
                    imageSize,
                    imageSize)
            imageView.layoutParams = llp
            imageView.borderWidth = borderSize
            imageView.borderColor = Color.BLACK
            Physics.setPhysicsConfig(imageView, config)
            physicsLayout.addView(imageView)
            imageView.x = x.toFloat()
            imageView.y = y.toFloat()

            x += imageSize
            if (x > physicsLayout.width) {
                x = 0
                y = (y + imageSize) % physicsLayout.height
            }
            Glide.with(this)
                    .load(contributor.avatarUrl)
                    .into(imageView)
        }
        physicsLayout.physics.onLayout(true)
    }
}
