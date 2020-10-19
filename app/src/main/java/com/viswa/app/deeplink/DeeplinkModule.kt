package com.viswa.app.deeplink

import com.viswa.deeplink.DeeplinkHandler
import com.viswa.deeplink.IDeeplinkHandler
import com.viswa.deeplink.IDeeplinkProcessor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module(includes = [DeeplinkProcessorsDependency::class, DeeplinkResolverDependency::class])
@InstallIn(ApplicationComponent::class)
abstract class DeeplinkModule {

    companion object {
        @Provides @Singleton
        fun provideDeeplinkHandler(processors: @JvmSuppressWildcards Set<IDeeplinkProcessor>): IDeeplinkHandler {
            return DeeplinkHandler(processors)
        }
    }
}

@Module
@InstallIn(ApplicationComponent::class)
abstract class DeeplinkProcessorsDependency {
    @Binds @IntoSet
    abstract fun chatLinkProcessor(processor: ChatProcessor): IDeeplinkProcessor
}

@Module
@InstallIn(ApplicationComponent::class)
abstract class DeeplinkResolverDependency {
    @Binds @Singleton
    abstract fun chatLinkResolver(processor: ChatResolver): IChatLinkResolver
}
