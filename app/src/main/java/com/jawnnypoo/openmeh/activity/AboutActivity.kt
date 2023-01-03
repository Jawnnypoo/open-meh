package com.jawnnypoo.openmeh.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import coil.load
import com.commit451.addendum.design.snackbar
import com.commit451.gimbal.Gimbal
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.databinding.ActivityAboutBinding
import com.jawnnypoo.openmeh.github.Contributor
import com.jawnnypoo.openmeh.github.GitHubClient
import com.jawnnypoo.openmeh.model.ParsedTheme
import com.jawnnypoo.openmeh.util.IntentUtil
import com.jawnnypoo.physicslayout.Physics
import com.jawnnypoo.physicslayout.PhysicsConfig
import com.jawnnypoo.physicslayout.Shape
import com.wefika.flowlayout.FlowLayout
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
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
                intent.putExtra(KEY_THEME, theme)
            }
            return intent
        }
    }

    private lateinit var binding: ActivityAboutBinding
    private lateinit var sensorManager: SensorManager
    private var gravitySensor: Sensor? = null
    private lateinit var gimbal: Gimbal
    private var theme: ParsedTheme? = null

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_GRAVITY) {
                val world = binding.physicsLayout.physics.world
                if (world != null) {
                    gimbal.normalizeGravityEvent(event)
                    world.gravity = Vec2(-event.values[0], event.values[1])
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gimbal = Gimbal(this)
        gimbal.lock()
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        binding.toolbar.setNavigationOnClickListener { goBack() }
        binding.textToolbarTitle.setText(R.string.about)
        binding.physicsLayout.physics.isFlingEnabled = true
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        theme = intent.getParcelableExtra(KEY_THEME)
        theme?.let {
            applyTheme(it)
        }

        launch {
            try {
                val contributors = GitHubClient.contributors(REPO_USER, REPO_NAME)
                binding.physicsLayout.post { addContributors(contributors) }
            } catch (e: Exception) {
                Timber.e(e)
                binding.root.snackbar(R.string.error_getting_contributors)
            }
        }

        binding.rootSource.setOnClickListener {
            val color = theme?.safeAccentColor() ?: Color.WHITE
            IntentUtil.openUrl(this, getString(R.string.source_url), color)
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            sensorEventListener,
            gravitySensor,
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorEventListener)
    }

    private fun applyTheme(theme: ParsedTheme) {
        //Tint widgets
        val accentColor = theme.safeAccentColor()
        binding.textToolbarTitle.setTextColor(theme.safeBackgroundColor())
        binding.toolbar.setBackgroundColor(theme.safeAccentColor())
        binding.toolbar.navigationIcon?.setColorFilter(
            theme.safeBackgroundColor(),
            PorterDuff.Mode.MULTIPLY
        )
        val safeForegroundColor = theme.safeForegroundColor()
        binding.textContributors.setTextColor(safeForegroundColor)
        binding.textSource.setTextColor(safeForegroundColor)
        binding.textSecret.setTextColor(safeForegroundColor)
        window.statusBarColor = accentColor
        window.navigationBarColor = accentColor
        window.decorView.setBackgroundColor(theme.safeBackgroundColor())
    }

    private fun addContributors(contributors: List<Contributor>) {
        val config = PhysicsConfig(
            shape = Shape.CIRCLE
        )
        val borderSize = resources.getDimensionPixelSize(R.dimen.border_size)
        val imageSize = resources.getDimensionPixelSize(R.dimen.circle_size)
        for (i in contributors.indices) {
            val contributor = contributors[i]
            val imageView = CircleImageView(this)
            val llp = FlowLayout.LayoutParams(
                imageSize,
                imageSize
            )
            imageView.layoutParams = llp
            imageView.borderWidth = borderSize
            imageView.borderColor = Color.BLACK
            Physics.setPhysicsConfig(imageView, config)
            binding.physicsLayout.addView(imageView)

            imageView.load(contributor.avatarUrl)
        }
        binding.physicsLayout.requestLayout()
    }

}
