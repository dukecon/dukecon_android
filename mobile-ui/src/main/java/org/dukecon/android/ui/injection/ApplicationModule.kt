package org.dukecon.android.ui.injection

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import com.fatboyindustrial.gsonjavatime.Converters
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import mu.KotlinLogging
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.dukecon.android.api.ConferencesApi
import org.dukecon.android.cache.ConferenceDataCacheImpl
import org.dukecon.android.cache.PreferencesHelper
import org.dukecon.android.cache.SharedPreferencesTokenStorage
import org.dukecon.android.cache.persistance.ConferenceCacheGsonSerializer
import org.dukecon.android.ui.BuildConfig
import org.dukecon.android.ui.features.login.DukeconAuthManager
import org.dukecon.android.ui.features.networking.AndroidNetworkUtils
import org.dukecon.android.ui.features.networking.ConnectionStateMonitor
import org.dukecon.android.ui.features.networking.LolipopConnectionStateMonitor
import org.dukecon.android.ui.features.networking.NetworkOfflineChecker
import org.dukecon.android.ui.features.notification.LocalNotificationProvider
import org.dukecon.data.repository.ConferenceDataCache
import org.dukecon.data.repository.EventRemote
import org.dukecon.data.service.OAuthService
import org.dukecon.data.source.ConferenceConfiguration
import org.dukecon.data.source.OAuthConfiguration
import org.dukecon.domain.aspects.auth.AuthManager
import org.dukecon.domain.aspects.twitter.TwitterLinks
import org.dukecon.domain.features.networking.NetworkUtils
import org.dukecon.domain.features.notifications.NotificationProvider
import org.dukecon.domain.features.oauth.TokensStorage
import org.dukecon.oauth.api.code.OauthApi
import org.dukecon.oauth.api.refresh.RefreshOauthApi
import org.dukecon.remote.conference.EventRemoteImpl
import org.dukecon.remote.conference.mapper.*
import org.dukecon.remote.oauth.RetrofitOAuthService
import org.dukecon.remote.oauth.interceptor.OauthAuthorizationInterceptor
import org.dukecon.remote.oauth.interceptor.TokenRefreshAfterFailInterceptor
import org.dukecon.remote.oauth.mapper.OAuthTokenMapper
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private val logger = KotlinLogging.logger {}

/**
 * Module used to provide dependencies at an application-level.
 */
@Module
open class ApplicationModule {

    @Provides
    fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    internal fun providePreferencesHelper(context: Context): PreferencesHelper {
        return PreferencesHelper(context)
    }

    @Singleton
    @Provides
    fun provideNetworkOfflineChecker(context: Context, networkUtils: NetworkUtils): NetworkOfflineChecker {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LolipopConnectionStateMonitor(context, networkUtils)
        } else {
            ConnectionStateMonitor(context, networkUtils)
        }
    }

    @Provides
    @Singleton
    internal fun provideFestivalNetworkUtils(context: Context): NetworkUtils {
        return AndroidNetworkUtils(context)
    }

    @Provides
    @Singleton
    internal fun provideEventCache(
            application: Application,
            gson: Gson,
            preferencesHelper: PreferencesHelper
    ): ConferenceDataCache {
        val baseCacheFolder = application.filesDir.absolutePath
        return ConferenceDataCacheImpl(ConferenceCacheGsonSerializer(baseCacheFolder, gson), preferencesHelper)
    }

    @Provides
    @Singleton
    internal fun providetwitterLinkMapper(): TwitterLinks {
        return TwitterLinks()
    }

    @Provides
    internal fun provideEventRemote(
            service: ConferencesApi,
            factory: EventEntityMapper,
            speakerMapper: SpeakerEntityMapper,
            roomEntityMapper: RoomEntityMapper,
            feedbackEntityMapper: FeedbackEntityMapper,
            conferenceConfiguration: ConferenceConfiguration,
            keycloakEntityMapper: KeycloakEntityMapper,
            metaDataEntityMapper: MetaDataEntityMapper,
            favoritesEntityMapper:FavoritesEntityMapper
    ): EventRemote {
        return EventRemoteImpl(
                service, conferenceConfiguration.conferenceId, factory, feedbackEntityMapper,
                speakerMapper, roomEntityMapper, keycloakEntityMapper, metaDataEntityMapper,
                favoritesEntityMapper
        )
    }

    class DO_NOT_VERIFY_IMP : javax.net.ssl.HostnameVerifier {
        override fun verify(p0: String?, p1: javax.net.ssl.SSLSession?): Boolean {
            return true
        }
    }

    class XtmImp : javax.net.ssl.X509TrustManager {
        override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
        }

        override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            val x509Certificates: Array<X509Certificate> = arrayOf()
            return x509Certificates
        }
    }

    @Singleton
    @Provides
    fun provideNonCachedOkHttpClient(application: Application): OkHttpClient {

        val cacheSize = 10 * 1024 * 1024L // 10 MB
        val cache = Cache(application.getCacheDir(), cacheSize)

        val xtm = XtmImp()
        val sslContext = javax.net.ssl.SSLContext.getInstance("SSL")
        try {
            sslContext.init(null, arrayOf<javax.net.ssl.TrustManager>(xtm), java.security.SecureRandom())
        } catch (e: java.security.NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: java.security.KeyManagementException) {
            e.printStackTrace()
        }

        val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { it ->
            if (BuildConfig.DEBUG) {
                Log.d("LOGGER", it)
            }
        })
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .cache(cache)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .sslSocketFactory(sslContext.getSocketFactory(), xtm)
                .hostnameVerifier(DO_NOT_VERIFY_IMP())
                .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return  Converters.registerOffsetDateTime(gsonBuilder).create()
    }

    @Provides
    @Singleton
    fun provideTokensStorage(application: Application): TokensStorage {
        return SharedPreferencesTokenStorage(application)
    }

    @Provides
    internal fun provideConferenceService(
            client: OkHttpClient,
            gson: Gson,
            conferenceConfiguration: ConferenceConfiguration,
            tokensStorage: TokensStorage,
            oAuthService: OAuthService,
            oAuthTokenMapper: OAuthTokenMapper
    ): ConferencesApi {
        val authorizingOkHttpClient = client.newBuilder()
                .addInterceptor(OauthAuthorizationInterceptor(tokensStorage))
                .addInterceptor(TokenRefreshAfterFailInterceptor(tokensStorage, oAuthService, oAuthTokenMapper))
                .build()
        val restAdapter = Retrofit.Builder()
                .baseUrl(conferenceConfiguration.baseUrl)
                .client(authorizingOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        return restAdapter.create(ConferencesApi::class.java)
    }

    @Provides
    internal fun provideOAuthApi(
            client: OkHttpClient,
            gson: Gson,
            oAuthConfiguration: OAuthConfiguration
    ): OauthApi {
        val restAdapter = Retrofit.Builder()
                .baseUrl(oAuthConfiguration.baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        return restAdapter.create(OauthApi::class.java)
    }

    @Provides
    internal fun provideRefreshOAuthApi(
            client: OkHttpClient,
            gson: Gson,
            oAuthConfiguration: OAuthConfiguration
    ): RefreshOauthApi {
        val restAdapter = Retrofit.Builder()
                .baseUrl(oAuthConfiguration.baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        return restAdapter.create(RefreshOauthApi::class.java)
    }


    @Provides
    internal fun provideOAuthService(retrofitOauthService: RetrofitOAuthService): OAuthService {
        return retrofitOauthService
    }

    @Provides
    fun provideAuthManager(dukeconAuthManager: DukeconAuthManager): AuthManager {
        return dukeconAuthManager
    }

    @Provides
    @Singleton fun provideNotificationProvider(application: Application): NotificationProvider {
        return LocalNotificationProvider(application)
    }
}

