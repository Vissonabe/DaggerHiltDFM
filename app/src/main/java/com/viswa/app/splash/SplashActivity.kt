package com.viswa.app.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.viswa.deeplink.IDeeplinkHandler
import com.viswa.dfm.databinding.SplashActivityBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * @author kienht
 * @since 15/09/2020
 */
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val splashViewModel by viewModels<SplashViewModel>()

    private lateinit var binding: SplashActivityBinding

    @Inject
    lateinit var deeplinkHandler : IDeeplinkHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        Timber.e("singleton userModel = ${splashViewModel.singletonUserModel}")
        splashViewModel.singletonUserModel.value += " => SplashActivity"

        splashViewModel.get("demo_key")
            .observe(this, Observer {
                Timber.e("demo_key = $it")
            })

        splashViewModel.put("demo_key", "demo_value")

        binding.buttonFeature.setOnClickListener {
            val clazz = Class.forName("com.viswa.feature.FeatureActivity")
            startActivity(Intent(this, clazz))
        }

        binding.chatFeature.setOnClickListener {
            val clazz = Class.forName("com.viswa.chatfeature.NavActivity")
            startActivity(Intent(this, clazz))
        }
        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent?.let { deeplinkHandler.process(it, this) }
    }
}