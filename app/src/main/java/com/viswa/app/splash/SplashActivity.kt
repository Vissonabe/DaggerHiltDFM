package com.viswa.app.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.viswa.deeplink.IDeeplinkHandler
import com.viswa.dfm.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * @author kienht
 * @since 15/09/2020
 */
@AndroidEntryPoint
class SplashActivity : AppCompatActivity(R.layout.splash_activity) {

    companion object {
        const val eventKey = "first_event_key"
    }

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    @Inject
    lateinit var deeplinkHandler: IDeeplinkHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        onNewIntent(intent)
    }

    override fun onStart() {
        super.onStart()

        firebaseAnalytics.logEvent(eventKey) {
            param(FirebaseAnalytics.Param.ITEM_ID, "id")
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent?.let { deeplinkHandler.process(it, this) }
    }
}
