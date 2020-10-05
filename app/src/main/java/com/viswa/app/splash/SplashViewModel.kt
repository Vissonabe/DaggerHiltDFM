package com.viswa.app.splash

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.viswa.network.NetworkUtils
import com.viswa.core.UserModel
import com.viswa.core.di.UserModelSingletonQualifier

/**
 * @author kienht
 * @since 13/09/2020
 */
class SplashViewModel @ViewModelInject constructor(
    @UserModelSingletonQualifier val singletonUserModel: UserModel,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    fun get(key: String) = savedStateHandle.getLiveData<String>(key)

    fun put(key: String, value: String) = savedStateHandle.set(key, value)

    init {
        println(NetworkUtils.isNetworkAvailable())
    }

    fun getTempString() = "sfsdfsdfs"

}