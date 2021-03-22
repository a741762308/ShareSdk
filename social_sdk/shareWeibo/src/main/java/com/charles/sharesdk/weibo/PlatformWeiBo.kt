package com.charles.sharesdk.weibo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.widget.Toast
import com.charles.sharesdk.core.SocialSdkApi
import com.charles.sharesdk.core.constant.SocialConstants
import com.charles.sharesdk.core.listener.IAuthListener
import com.charles.sharesdk.core.listener.IShareListener
import com.charles.sharesdk.core.media.IShareMedia
import com.charles.sharesdk.core.media.ShareTextImageMedia
import com.charles.sharesdk.core.media.ShareWebPageMedia
import com.charles.sharesdk.core.model.PlatformConfig
import com.charles.sharesdk.core.platform.AbsSharePlatform
import com.charles.sharesdk.core.platform.IShareFactory
import com.charles.sharesdk.core.util.PlatformType
import com.charles.sharesdk.core.util.ShareImageUtils
import com.sina.weibo.sdk.WbSdk
import com.sina.weibo.sdk.api.ImageObject
import com.sina.weibo.sdk.api.TextObject
import com.sina.weibo.sdk.api.WebpageObject
import com.sina.weibo.sdk.api.WeiboMultiMessage
import com.sina.weibo.sdk.auth.AuthInfo
import com.sina.weibo.sdk.auth.Oauth2AccessToken
import com.sina.weibo.sdk.auth.WbAuthListener
import com.sina.weibo.sdk.auth.WbConnectErrorMessage
import com.sina.weibo.sdk.auth.sso.SsoHandler
import com.sina.weibo.sdk.constant.WBConstants
import com.sina.weibo.sdk.share.WbShareCallback
import com.sina.weibo.sdk.share.WbShareHandler
import com.sina.weibo.sdk.utils.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 *
 * @author dq on 2021/2/18.
 */
private const val THUMB_SIZE = 32 * 1024L
private const val IMAGE_SIZE = 2 * 1024 * 1024L

class PlatformWeiBo private constructor(context: Context?, appId: String?, redirectUrl: String?, platform: String) : AbsSharePlatform(context, appId, platform) {
    private var ssoHandler: SsoHandler? = null
    private var shareHandler: WbShareHandler? = null

    init {
        val authInfo = AuthInfo(context, appId, redirectUrl, "all")
        WbSdk.install(context, authInfo)
    }

    class WeiBoShareFactory : IShareFactory {
        override fun create(context: Context?, target: String): AbsSharePlatform {
            val configs: PlatformConfig = SocialSdkApi.get().opts().getPlatformConfig(target)
            return PlatformWeiBo(context, configs.appId, configs.redirectUrl, target)
        }

        override fun getTargetPlatform(): String {
            return PlatformType.SINA_WEIBO
        }
    }

    override fun authorize(activity: Activity?, authListener: IAuthListener?) {
        super.authorize(activity, authListener)
        ssoHandler = SsoHandler(activity)
        ssoHandler?.authorize(object : WbAuthListener {
            override fun onSuccess(oauth2AccessToken: Oauth2AccessToken?) {
                oauth2AccessToken?.run {
                    authListener?.run {
                        val map: MutableMap<String?, String?> = HashMap()
                        map[SocialConstants.U_ID] = oauth2AccessToken.uid
                        map[SocialConstants.ACCESS_TOKEN] = oauth2AccessToken.token
                        map[SocialConstants.REFRESH_TOKEN] = oauth2AccessToken.refreshToken
                        onComplete(targetPlatform, map)
                    }
                }
            }

            override fun cancel() {
                authListener?.onCancel(targetPlatform)
            }

            override fun onFailure(wbConnectErrorMessage: WbConnectErrorMessage?) {
                authListener?.onError(targetPlatform, wbConnectErrorMessage?.errorMessage)
            }
        })
    }

    override fun share(activity: Activity?, shareMedia: IShareMedia?, shareListener: IShareListener?) {
        super.share(activity, shareMedia, shareListener)
        if (activity == null) {
            return
        }
        if (!isInstall(activity)) {
            Toast.makeText(activity, R.string.weibo_not_install, Toast.LENGTH_SHORT).show()
            return
        }
        shareHandler = WbShareHandler(activity)
        shareHandler?.registerApp()
        share(shareMedia)
    }

    private fun share(shareMedia: IShareMedia?) = runBlocking {
        flow {
            val message = WeiboMultiMessage()
            when (shareMedia) {
                is ShareWebPageMedia -> {
                    //网页分享
                    val webPageObject = WebpageObject()
                    webPageObject.identify = Utility.generateGUID()
                    webPageObject.actionUrl = shareMedia.webPageUrl
                    webPageObject.title = handleString(shareMedia.title, 512)
                    webPageObject.description = handleString(shareMedia.description, 1024)
                    shareMedia.thumbPath?.run {
                        //缩略图32Kb
                        webPageObject.thumbData = ShareImageUtils.scaleThumb(this, THUMB_SIZE)
                    }
                    shareMedia.title?.run {
                        val textObject = TextObject()
                        textObject.text = handleString(this, 1024)
                        message.textObject = textObject
                    }
                    message.mediaObject = webPageObject
                }
                is ShareTextImageMedia -> {
                    //文字图片分享
                    shareMedia.text?.run {
                        val textObject = TextObject()
                        textObject.text = handleString(this, 1024)
                        message.textObject = textObject
                    }
                    if (!TextUtils.isEmpty(shareMedia.imagePath)) {
                        val imageObject = ImageObject()
                        imageObject.imageData = ShareImageUtils.scaleImage(shareMedia.imagePath!!, IMAGE_SIZE)
                        message.imageObject = imageObject
                    } else if (shareMedia.imageBitmap != null) {
                        val imageObject = ImageObject()
                        imageObject.setImageObject(ShareImageUtils.scaleBitmap(shareMedia.imageBitmap, IMAGE_SIZE))
                        message.imageObject = imageObject
                    }
                }
                else -> {
                    throw Exception("WeiBo is not support this shareMedia")
                }
            }
            emit(message)
        }.flowOn(Dispatchers.IO)
            .catch {
                shareListener?.onError(targetPlatform, errorMsg = it.message)
            }.collect {
                shareHandler?.shareMessage(it, false)
            }
    }

    override fun isInstall(context: Context?): Boolean {
        return WbSdk.isWbInstall(context)
    }

    override fun handleIntent(intent: Activity?) {
        super.handleIntent(intent)
        if (shareListener != null && intent is WbShareCallback) {
            shareHandler?.doResultIntent(intent.intent, intent)
        }
    }

    override fun onActivityResult(activity: Activity?, requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(activity, requestCode, resultCode, data)
        ssoHandler?.authorizeCallBack(requestCode, resultCode, data)
        if (shareListener != null && activity is WbShareCallback) {
            shareHandler?.doResultIntent(data, activity)
        }
    }

    override fun onResp(resp: Any?) {
        super.onResp(resp)
        if (resp is Int) {
            when (resp) {
                WBConstants.ErrorCode.ERR_OK -> shareListener?.onComplete(targetPlatform)
                WBConstants.ErrorCode.ERR_CANCEL -> shareListener?.onCancel(targetPlatform)
                WBConstants.ErrorCode.ERR_FAIL -> shareListener?.onError(targetPlatform, errorMsg = "WeiBo share fail")
            }
        }
    }

    override fun getUICallBackClass(): Class<*>? {
        return WeiboCallBackActivity::class.java
    }

    override fun destroy() {
        super.destroy()
        ssoHandler = null
        shareHandler = null
    }
}