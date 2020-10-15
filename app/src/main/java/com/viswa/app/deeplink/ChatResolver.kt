package com.viswa.app.deeplink

import com.viswa.core.constants.SCREEN_NAME_CHAT
import com.viswa.core.utils.UriHelper
import javax.inject.Inject

class ChatResolver @Inject constructor() : IChatLinkResolver {
    override fun resolve(deepLinkString: String?): Boolean {
        deepLinkString?.let {
            val uri = UriHelper(it)
            val host = uri.host
            val pathSegments = uri.pathSegments
            return (host.isNotEmpty() && host == SCREEN_NAME_CHAT) ||
                (pathSegments.size >= 1 && pathSegments[0].contains(SCREEN_NAME_CHAT))
        }
        return false
    }
}
