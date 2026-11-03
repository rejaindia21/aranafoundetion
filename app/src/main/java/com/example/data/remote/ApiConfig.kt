package com.arnafoundation.app.data.remote

/**
 * ============================================================
 * ARNA GRAM UNNAYA FOUNDATION
 * API CONFIGURATION
 * ============================================================
 *
 * Production configuration for the ARNA Android application.
 *
 * Android communicates only with the ARNA PHP REST API.
 * Android must NOT connect directly to MySQL.
 * ============================================================
 */

data class ApiConfig(

    /**
     * ARNA PHP REST API base URL.
     *
     * Keep the trailing slash because Retrofit requires
     * the base URL to end with "/".
     */
    val baseUrl: String =
        "https://arnafoundation.com/api/app/",

    /**
     * API authentication key.
     *
     * This value is sent by ApiClient through the
     * X-API-Key request header.
     */
    val apiKey: String =
        "ARNA_SECURE_TOKEN_2026_PROD",

    /**
     * Mock/demo mode.
     *
     * FALSE = use the live ARNA PHP backend.
     *
     * This application must remain on live backend mode
     * for production use.
     */
    val isMockMode: Boolean = false,

    /**
     * Network timeout in seconds.
     *
     * ApiClient currently uses its own OkHttp timeout values.
     * This value is retained as part of the existing configuration
     * model so existing code does not break.
     */
    val timeoutSeconds: Int = 30
)


/**
 * ============================================================
 * API CONFIGURATION MANAGER
 * ============================================================
 *
 * Holds the active ARNA API configuration and authenticated
 * session token used by ApiClient.
 *
 * This object is process-local. It does not replace secure
 * persistent session storage.
 * ============================================================
 */
object ApiConfigManager {

    /**
     * Current API configuration.
     *
     * private set prevents other classes from replacing the
     * entire configuration object directly.
     */
    var currentConfig = ApiConfig()
        private set


    /**
     * Current authenticated session token.
     *
     * Null means no authenticated API session token is active.
     */
    var currentSessionToken: String? = null


    /**
     * Update the active API configuration.
     *
     * Existing timeoutSeconds value is preserved.
     */
    fun updateConfig(
        baseUrl: String,
        apiKey: String,
        isMockMode: Boolean
    ) {
        currentConfig = currentConfig.copy(
            baseUrl = baseUrl,
            apiKey = apiKey,
            isMockMode = isMockMode
        )
    }


    /**
     * Set the authenticated API session token.
     *
     * Passing null clears the current token.
     */
    fun setToken(token: String?) {
        currentSessionToken = token
    }


    /**
     * Clear the authenticated API session token.
     */
    fun clearToken() {
        currentSessionToken = null
    }
}