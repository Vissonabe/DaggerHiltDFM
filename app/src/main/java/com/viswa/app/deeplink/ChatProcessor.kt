package com.viswa.app.deeplink

import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.viswa.app.splash.SplashActivity
import com.viswa.core.constants.LAUNCH_SOURCE_DEEPLINK
import com.viswa.core.utils.UriHelper
import com.viswa.deeplink.IDeeplinkProcessor
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatProcessor @Inject constructor(
    val chatResolver: IChatLinkResolver,
    @ApplicationContext val appContext: Context
) : IDeeplinkProcessor {
    override fun matches(intent: Intent): Boolean {
        return chatResolver.resolve(intent.dataString)
    }

    override fun process(intent: Intent): Intent {
        val clazz = Class.forName("com.viswa.chatfeature.NavActivity")
        val mIntent = Intent(appContext, clazz)
        mIntent.action = intent.action
        mIntent.data = processIntentToUri(intent)
        return mIntent
    }

    private fun processIntentToUri(intent: Intent): Uri? {
        val dataString = intent.dataString
        var processedUri: Uri? = null
        if (!dataString.isNullOrEmpty()) {
            val uri = UriHelper(dataString)
            if (uri.pathSegments.size >= 2 && uri.pathSegments[1].contains("")) {
                processedUri = uri.uri
            }
        } else {
            processedUri = intent.data
        }
        return processedUri
    }

    override fun renderTarget(childIntent: Intent, activity: AppCompatActivity) {
        if (activity is SplashActivity) {
            val taskStackBuilder = TaskStackBuilder.create(appContext)
            val parentIntent = Intent(appContext, SplashActivity::class.java).apply {
                putExtra(LAUNCH_SOURCE_DEEPLINK, true)
                flags = getIntentFlags()
            }
            childIntent.flags = getIntentFlags()
            println(
                "xxx childIntent: ${childIntent.component?.className} " +
                    "parentIntent: ${parentIntent.component?.className}"
            )
            if (childIntent.component?.className != SplashActivity::class.java.canonicalName) {
                taskStackBuilder.addNextIntent(parentIntent)
            }
            taskStackBuilder.addNextIntent(childIntent)
            taskStackBuilder.startActivities()
        } else {
            activity.startActivity(childIntent)
        }
    }
}
