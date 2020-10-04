package com.viswa.core.di

import com.viswa.core.UserModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * @author kienht
 * @since 15/09/2020
 */
@Module
@InstallIn(ApplicationComponent::class)
object CoreModule {

    @Provides
    @Singleton
    @UserModelSingletonQualifier
    fun provideUserModel() = UserModel(value = "Singleton")
}