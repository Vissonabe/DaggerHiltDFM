package com.viswa.core.utils

import android.net.Uri
import androidx.core.net.toUri
import com.viswa.core.utils.UriHelper.DeepLinkUtils.APP_SCHEME
import com.viswa.core.utils.UriHelper.DeepLinkUtils.APP_URL

class UriHelper(var deepLinkString: String) {
    val uri = generateUri()
    var host: String = generateHost()
    val query: String? = uri.query
    val pathSegments: MutableList<String> = uri.pathSegments.toMutableList()
    val path: String? = uri.encodedPath
    var appLink = false

    object DeepLinkUtils {

        const val APP_SCHEME = "ddfm://"
        const val APP_URL = "https://www.ddfm.com/"
    }

    fun getQueryParameter(queryParam: String): String? = uri.getQueryParameter(queryParam)

    private fun generateUri(): Uri {
        var uriString = deepLinkString
        if (uriString.startsWith(APP_SCHEME)) {
            appLink = true
            uriString = uriString.replace(APP_SCHEME, APP_URL)
        } else {
            appLink = false
        }
        return uriString.trim().toUri()
    }

    private fun generateHost(): String {
        return if (deepLinkString.startsWith(APP_SCHEME)) {
            getUriHost()
        } else {
            uri.host ?: ""
        }
    }

    private fun getUriHost(): String {
        val length = deepLinkString.length
        val ssi = deepLinkString.indexOf(':')
        // If "//" follows the scheme separator, we have an authority.
        if (length > ssi + 2
            && deepLinkString[ssi + 1] == '/'
            && deepLinkString[ssi + 2] == '/') {
            // We have an authority.
            // Look for the start of the path, query, or fragment, or the
            // end of the string.
            var end = ssi + 3
            LOOP@ while (end < length) {
                when (deepLinkString[end]) {
                    '/' // Start of path
                        , '\\'// Start of path
                        ,
                        // Per http://url.spec.whatwg.org/#host-state, the \ character
                        // is treated as if it were a / character when encountered in a
                        // host
                    '?' // Start of query
                        , '#' // Start of fragment
                    -> break@LOOP
                }
                end++
            }

            return deepLinkString.substring(ssi + 3, end)
        } else {
            return ""
        }
    }
}