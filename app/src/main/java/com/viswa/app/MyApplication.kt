package com.viswa.app

import android.app.Application
import com.viswa.core.UserModel
import com.viswa.core.di.UserModelSingletonQualifier
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * @author kienht
 * @since 15/09/2020
 */
@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    @UserModelSingletonQualifier
    lateinit var singletonUserModel: UserModel

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        singletonUserModel.value += "MyApplication"

        println("xxx package name is $packageName")
    }
}
