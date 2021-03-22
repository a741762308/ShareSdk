package com.charles.sharesdk.core.util

import androidx.annotation.StringDef
import com.charles.sharesdk.core.util.PlatformType.Companion.ALIPAY
import com.charles.sharesdk.core.util.PlatformType.Companion.ALIPAY_MOMENTS
import com.charles.sharesdk.core.util.PlatformType.Companion.DINGTALK
import com.charles.sharesdk.core.util.PlatformType.Companion.FACEBOOK
import com.charles.sharesdk.core.util.PlatformType.Companion.GOOGLE
import com.charles.sharesdk.core.util.PlatformType.Companion.INSTAGRAM
import com.charles.sharesdk.core.util.PlatformType.Companion.MESSENGER
import com.charles.sharesdk.core.util.PlatformType.Companion.QQ
import com.charles.sharesdk.core.util.PlatformType.Companion.QQ_ZONE
import com.charles.sharesdk.core.util.PlatformType.Companion.SINA_WEIBO
import com.charles.sharesdk.core.util.PlatformType.Companion.TWITTER
import com.charles.sharesdk.core.util.PlatformType.Companion.WECHAT
import com.charles.sharesdk.core.util.PlatformType.Companion.WECHAT_MOMENTS
import com.charles.sharesdk.core.util.PlatformType.Companion.WHATSAPP

/**
 *
 * @author dq on 2020/8/31.
 */
@StringDef(
    WECHAT,
    WECHAT_MOMENTS,
    QQ,
    QQ_ZONE,
    ALIPAY,
    ALIPAY_MOMENTS,
    SINA_WEIBO,
    DINGTALK,
    GOOGLE,
    TWITTER,
    MESSENGER,
    WHATSAPP,
    FACEBOOK,
    INSTAGRAM
)
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER
)
@Retention(AnnotationRetention.SOURCE)
annotation class PlatformType {
    companion object {
        const val WECHAT = "WeChat"
        const val WECHAT_MOMENTS = "WeChat_Moments"
        const val QQ = "QQ"
        const val QQ_ZONE = "QQ_Zone"
        const val SINA_WEIBO = "SinaWeibo"
        const val ALIPAY = "Alipay"
        const val ALIPAY_MOMENTS = "Alipay_Moments"
        const val DINGTALK = "DingTalk"
        const val GOOGLE = "Google"
        const val TWITTER = "Twitter"
        const val MESSENGER = "Messenger"
        const val WHATSAPP = "WhatsApp"
        const val FACEBOOK = "FaceBook"
        const val INSTAGRAM = "Instagram"
    }
}