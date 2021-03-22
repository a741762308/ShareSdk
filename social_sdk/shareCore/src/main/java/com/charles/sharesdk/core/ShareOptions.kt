package com.charles.sharesdk.core

import android.content.Context
import android.util.Log
import com.charles.sharesdk.core.model.PlatformConfig
import com.charles.sharesdk.core.platform.IShareFactory
import com.charles.sharesdk.core.util.PlatformType
import java.util.*
import kotlin.collections.HashMap

/**
 *
 * @author dq on 2020/8/31.
 */

private const val TAG = "ShareOptions"

class ShareOptions
    (builder: Builder) {

    val factories: MutableMap<String, IShareFactory>
    val factoryClassList: MutableSet<String>
    private val qqConfig: PlatformConfig
    private val weChatConfig: PlatformConfig
    private val weiBoConfig: PlatformConfig
    private val aliPayConfig: PlatformConfig
    private val dingTalkConfig: PlatformConfig
    private val twitterConfig: PlatformConfig
    private val faceBookConfig: PlatformConfig
    private val whatsAppConfig: PlatformConfig

    init {
        Log.d(TAG, "builder=\nqqAppId:${builder.qqAppId}")
        factoryClassList = builder.factoryClassList
        factories = builder.factories
        qqConfig = PlatformConfig()
        qqConfig.enable = builder.qqEnable
        qqConfig.appId = builder.qqAppId
        if (qqConfig.enable) {
            //QQ
            factoryClassList.add("com.charles.sharesdk.qq.PlatformQQ\$QQShareFactory")
            //QQ_Zone
            factoryClassList.add("com.charles.sharesdk.qq.PlatformQQ\$QQZoneShareFactory")
        }

        weChatConfig = PlatformConfig()
        weChatConfig.enable = builder.wechatEnable
        weChatConfig.appId = builder.wechatAppId
        weChatConfig.appSecret = builder.wechatAppSecret
        weChatConfig.onlyAuthCode = builder.wechatOnlyAuthCode == true
        if (weChatConfig.enable) {
            //WeChat
            factoryClassList.add("com.charles.sharesdk.wechat.PlatformWeChat\$WeChatShareFactory")
            //WeChat_Moments
            factoryClassList.add("com.charles.sharesdk.wechat.PlatformWeChat\$WeChatMomentsShareFactory")
        }

        weiBoConfig = PlatformConfig()
        weiBoConfig.enable = builder.weiboEnable
        weiBoConfig.appId = builder.weiboAppId
        weiBoConfig.redirectUrl = builder.weiboRedirectUrl
        if (weiBoConfig.enable) {
            factoryClassList.add("com.charles.sharesdk.weibo.PlatformWeiBo\$WeiBoShareFactory")
        }

        aliPayConfig = PlatformConfig()
        aliPayConfig.enable = builder.alipayEnable
        aliPayConfig.appId = builder.alipayAppId
        aliPayConfig.appSecret = builder.alipayAppSecret
        aliPayConfig.onlyAuthCode = builder.alipayOnlyAuthCode == true
        if (aliPayConfig.enable) {
            //AliPay
            factoryClassList.add("com.charles.sharesdk.alipay.PlatformAliPay\$AliPayShareFactory")
            //AliPay_Moments
            factoryClassList.add("com.charles.sharesdk.alipay.PlatformAliPay\$AliPayMomentsShareFactory")
        }

        dingTalkConfig = PlatformConfig()
        dingTalkConfig.enable = builder.dingtalkEnable
        dingTalkConfig.appId = builder.dingtalkAppId
        dingTalkConfig.appSecret = builder.dingtalkAppSecret
        dingTalkConfig.onlyAuthCode = builder.dingtalkOnlyAuthCode == true
        if (dingTalkConfig.enable) {
            factoryClassList.add("com.charles.sharesdk.dingtalk.PlatformDingTalk\$DingTalkShareFactory")
        }

        twitterConfig = PlatformConfig()
        twitterConfig.enable = builder.twitterEnable
        twitterConfig.appId = builder.twitterAppId
        twitterConfig.appSecret = builder.twitterAppSecret
        twitterConfig.redirectUrl = builder.twitterRedirectUrl
        if (twitterConfig.enable) {
            factoryClassList.add("com.charles.sharesdk.twitter.PlatformTwitter\$TwitterShareFactory")
        }

        faceBookConfig = PlatformConfig()
        faceBookConfig.enable = builder.facebookEnable
        faceBookConfig.appId = builder.facebookAppId
        faceBookConfig.appSecret = builder.facebookAppSecret
        faceBookConfig.redirectUrl = builder.facebookRedirectUrl
        if (faceBookConfig.enable) {
            //FaceBook
            factoryClassList.add("com.charles.sharesdk.facebook.PlatformFaceBook\$FaceBookShareFactory")
            //FaceBook Messenger
            factoryClassList.add("com.charles.sharesdk.facebook.PlatformFaceBook\$FaceBookMessengerShareFactory")
            //Instagram
            factoryClassList.add("com.charles.sharesdk.facebook.PlatformFaceBook\$FaceBookInstagramShareFactory")
        }

        whatsAppConfig = PlatformConfig()
        whatsAppConfig.enable = builder.whatsAppEnable
        if (whatsAppConfig.enable) {
            factoryClassList.add("com.charles.sharesdk.whatsapp.PlatformWhatsApp\$WhatsAppShareFactory")
        }
    }

    fun getQQConfig(): PlatformConfig {
        return qqConfig
    }

    fun getWeChatConfig(): PlatformConfig {
        return weChatConfig
    }

    fun getWeiBoConfig(): PlatformConfig {
        return weiBoConfig
    }

    fun getAliPayConfig(): PlatformConfig {
        return aliPayConfig
    }

    fun getDingTalkConfig(): PlatformConfig {
        return dingTalkConfig
    }

    fun getTwitterConfig(): PlatformConfig {
        return twitterConfig
    }

    fun getFaceBookConfig(): PlatformConfig {
        return faceBookConfig
    }

    fun getWhatsAppConfig(): PlatformConfig {
        return whatsAppConfig
    }

    fun getPlatformConfig(@PlatformType platformType: String): PlatformConfig {
        return when (platformType) {
            PlatformType.QQ, PlatformType.QQ_ZONE -> getQQConfig()
            PlatformType.WECHAT, PlatformType.WECHAT_MOMENTS -> getWeChatConfig()
            PlatformType.SINA_WEIBO -> getWeiBoConfig()
            PlatformType.ALIPAY, PlatformType.ALIPAY_MOMENTS -> getAliPayConfig()
            PlatformType.DINGTALK -> getDingTalkConfig()
            PlatformType.TWITTER -> getTwitterConfig()
            PlatformType.FACEBOOK, PlatformType.MESSENGER, PlatformType.INSTAGRAM -> getFaceBookConfig()
            PlatformType.WHATSAPP -> getWhatsAppConfig()
            else -> getQQConfig()
        }
    }


    class Builder(context: Context) {
        val factories: MutableMap<String, IShareFactory>
        val factoryClassList: MutableSet<String>

        //QQ
        var qqAppId: String? = null
        var qqEnable = false

        //微信
        var wechatAppId: String? = null
        var wechatAppSecret: String? = null
        var wechatEnable = false
        var wechatOnlyAuthCode: Boolean? = false

        //微博
        var weiboAppId: String? = null
        var weiboEnable = false
        var weiboRedirectUrl: String? = null

        //支付宝
        var alipayAppId: String? = null
        var alipayAppSecret: String? = null
        var alipayEnable = false
        var alipayOnlyAuthCode: Boolean? = false

        //钉钉
        var dingtalkAppId: String? = null
        var dingtalkAppSecret: String? = null
        var dingtalkEnable = false
        var dingtalkOnlyAuthCode: Boolean? = false

        //facebook
        var facebookAppId: String? = null
        var facebookAppSecret: String? = null
        var facebookEnable = false
        var facebookRedirectUrl: String? = null

        //twitter
        var twitterAppId: String? = null
        var twitterAppSecret: String? = null
        var twitterEnable = false
        var twitterRedirectUrl: String? = null

        //whatsApp
        var whatsAppEnable = false

        init {
            factories = HashMap()
            this.factoryClassList = HashSet()
            initConfigByAsm()
        }

        private fun initConfigByAsm() {

        }

        fun qq(appId: String) = apply {
            qqAppId = appId
            qqEnable = true
        }

        fun weChat(appId: String, appSecret: String, onlyAuthCode: Boolean? = false) = apply {
            wechatAppId = appId
            wechatAppSecret = appSecret
            wechatOnlyAuthCode = onlyAuthCode
            wechatEnable = true
        }

        fun weiBo(appId: String, redirectUrl: String) = apply {
            weiboAppId = appId
            weiboRedirectUrl = redirectUrl
            weiboEnable = true
        }

        fun aliPay(appId: String, appSecret: String) = apply {
            alipayAppId = appId
            alipayAppSecret = appSecret
            alipayEnable = true
        }

        fun dingTalk(appId: String, appSecret: String) = apply {
            dingtalkAppId = appId
            dingtalkAppSecret = appSecret
            dingtalkEnable = true
        }

        fun faceBook(appId: String, appSecret: String, redirectUrl: String) = apply {
            facebookAppId = appId
            facebookAppSecret = appSecret
            facebookRedirectUrl = redirectUrl
            facebookEnable = true
        }

        fun twitter(appId: String, appSecret: String, redirectUrl: String) = apply {
            twitterAppId = appId
            twitterAppSecret = appSecret
            twitterAppSecret = redirectUrl
            twitterEnable = true
        }

        fun whatsApp() = apply {
            whatsAppEnable = true
        }


        fun build(): ShareOptions {
            return ShareOptions(this)
        }
    }
}