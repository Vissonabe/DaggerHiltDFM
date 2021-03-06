package com.viswa.core.di

import android.app.Application
import com.viswa.core.UserModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

/**
 * @author kienht
 * @since 15/09/2020
 */
@EntryPoint
@InstallIn(ApplicationComponent::class)
interface CoreModuleDependencies {

    fun exposeApplication(): Application

    @UserModelSingletonQualifier
    fun exposeUserManager(): UserModel
}
