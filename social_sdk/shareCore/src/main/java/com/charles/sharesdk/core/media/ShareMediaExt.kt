package com.charles.sharesdk.core.media

import android.graphics.Bitmap

/**
 *
 * @author dq on 2021/2/19.
 */
data class ShareTextMedia(var text: String? = null) : IShareMedia

open class ShareImageMedia(
    open var imagePath: String? = null,
    open var imageBitmap: Bitmap? = null
) : IShareMedia

data class ShareTextImageMedia(
    var text: String? = null,
) : ShareImageMedia()

data class ShareWebPageMedia(
    var title: String? = null,
    var webPageUrl: String? = null,
    var description: String? = null,
    var thumbPath: String? = null
) : IShareMedia

data class ShareMiniProgramMedia(
    var title: String? = null,
    var webPageUrl: String? = null,
    var description: String? = null,
    var thumbPath: String? = null,
    var userName: String? = null,
    var path: String? = null,
    var withShareTicket: Boolean = false,
    var miniProgramType: Int = 0
) : IShareMedia