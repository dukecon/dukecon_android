package org.dukecon.android.ui.injection

import android.app.Application
import dagger.Module
import dagger.Provides
import org.dukecon.android.ui.configuration.ApacheConConfiguration
import org.dukecon.android.ui.configuration.ApacheOAuthConfiguration
import org.dukecon.data.source.ConferenceConfiguration
import org.dukecon.data.source.OAuthConfiguration

@Module
open class ConferenceModule {

    @Provides
    fun provideConfiguration(application: Application): ConferenceConfiguration {
        return ApacheConConfiguration(application)
    }

    @Provides
    fun provideOAuthConfiguration(application: Application): OAuthConfiguration {
        return ApacheOAuthConfiguration(application)
    }
}
