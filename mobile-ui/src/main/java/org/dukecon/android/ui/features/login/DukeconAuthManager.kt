package org.dukecon.android.ui.features.login

import android.app.Activity
import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import org.dukecon.android.ui.features.login.browser.CustomTabActivityHelper
import org.dukecon.android.ui.features.login.browser.WebviewFallback
import org.dukecon.data.service.OAuthService
import org.dukecon.data.source.OAuthConfiguration
import org.dukecon.domain.aspects.auth.AuthManager
import org.dukecon.domain.features.oauth.TokensStorage
import org.dukecon.remote.oauth.mapper.OAuthTokenMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DukeconAuthManager @Inject constructor(
    private val oAuthConfiguration: OAuthConfiguration,
    private val oAuthService: OAuthService,
    private val tokensStorage: TokensStorage,
    private val oAuthTokenMapper: OAuthTokenMapper
) : AuthManager {

    override fun exchangeToken(code: String) {
        val token = oAuthService.code2token(code)
        tokensStorage.setToken(oAuthTokenMapper.map(token))
    }

    override fun login(activity: Object) {
        val uri =
            "${oAuthConfiguration.baseUrl}auth?client_id=${oAuthConfiguration.clientId}&redirect_uri=appdoag://redirect2token&response_type=code&scope=openid%20offline_access".toUri()
        val customTabsIntent = CustomTabsIntent.Builder().build()
        CustomTabActivityHelper.openCustomTab(
            activity as Activity, customTabsIntent, uri, WebviewFallback()
        )
    }
}