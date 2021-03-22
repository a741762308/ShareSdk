package com.charles.sharesdk.alipay

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import com.alipay.sdk.app.OpenAuthTask
import com.alipay.share.sdk.openapi.*
import com.charles.sharesdk.core.SocialSdkApi
import com.charles.sharesdk.core.constant.SocialConstants
import com.charles.sharesdk.core.listener.IAuthListener
import com.charles.sharesdk.core.listener.IShareListener
import com.charles.sharesdk.core.media.IShareMedia
import com.charles.sharesdk.core.media.ShareImageMedia
import com.charles.sharesdk.core.media.ShareTextMedia
import com.charles.sharesdk.core.media.ShareWebPageMedia
import com.charles.sharesdk.core.model.PlatformConfig
import com.charles.sharesdk.core.platform.AbsSharePlatform
import com.charles.sharesdk.core.platform.IShareFactory
import com.charles.sharesdk.core.util.PlatformType
import com.charles.sharesdk.core.util.ShareImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import java.net.URLEncoder

/**
 *
 * @author dq on 2021/2/20.
 */
private const val THUMB_SIZE = 32 * 1024L
private const val IMAGE_SIZE = 10 * 1024 * 1024L

class PlatformAliPay private constructor(context: Context?, appId: String?, platform: String) : AbsSharePlatform(context, appId, platform) {

    private var iApApi: IAPApi? = null

    init {
        iApApi = APAPIFactory.createZFBApi(context, appId, false)
    }

    open class AliPayShareFactory : IShareFactory {
        override fun create(context: Context?, target: String): AbsSharePlatform {
            val configs: PlatformConfig = SocialSdkApi.get().opts().getPlatformConfig(target)
            return PlatformAliPay(context, configs.appId, target)
        }

        override fun getTargetPlatform(): String {
            return PlatformType.ALIPAY
        }
    }

    class AliPayMomentsShareFactory : AliPayShareFactory() {

        override fun getTargetPlatform(): String {
            return PlatformType.ALIPAY_MOMENTS
        }
    }

    /**
     * <p>
     * 使用的极简版SDK，文档地址 @see <a herf= "https://opendocs.alipay.com/open/218/sxc60m"/>
     * <p>
     * <p>
     * auth_code 获取refresh_token，需要使用rsa秘钥 建议后端使用，文档地址@see <a herf="https://opendocs.alipay.com/apis/api_9/alipay.system.oauth.token"/>
     * <p>
     */
    override fun authorize(activity: Activity?, authListener: IAuthListener?) {
        super.authorize(activity, authListener)
        runBlocking {
            val task = OpenAuthTask(activity)
            val map: MutableMap<String?, String> = HashMap()
            map["url"] = "https://authweb.alipay.com/auth?auth_type=PURE_OAUTH_SDK&app_id=${appId}&scope=auth_user&state=init"
            task.execute("alipay${appId}", OpenAuthTask.BizType.AccountAuth, map, { resultCode, memo, bundle ->
                when (resultCode) {
                    OpenAuthTask.OK -> {
                        authListener?.run {
                            map.clear()
                            map[SocialConstants.AUTH_CODE] = bundle.getString("auth_code", "")
                            onComplete(targetPlatform, map)
                        }
                    }
                    else -> {
                        authListener?.onError(targetPlatform, "alipay auth fail")
                    }
                }
            }, true)
        }
    }

    override fun share(activity: Activity?, shareMedia: IShareMedia?, shareListener: IShareListener?) {
        super.share(activity, shareMedia, shareListener)
        if (activity == null) {
            return
        }
        if (!isInstall(activity)) {
            Toast.makeText(activity, R.string.alipay_not_install, Toast.LENGTH_SHORT).show()
            return
        }
        share(shareMedia)
    }

    private fun share(shareMedia: IShareMedia?) = runBlocking {
        flow {
            val message = APMediaMessage()
            when (shareMedia) {
                is ShareWebPageMedia -> {
                    //网页分享
                    val pageObject = APWebPageObject()
                    var pageUrl = shareMedia.webPageUrl
                    //适配小程序
                    if (pageUrl != null && pageUrl.startsWith("alipays://platformapi/startapp")) {
                        pageUrl = "https://ds.alipay.com/?scheme=${URLEncoder.encode(pageUrl, "utf-8")}"
                    }
                    pageObject.webpageUrl = pageUrl
                    message.mediaObject = pageObject
                    message.title = handleString(shareMedia.title, 512)
                    message.description = handleString(shareMedia.description, 1024)
                    shareMedia.thumbPath?.run {
                        //缩略图32Kb
                        message.thumbData = ShareImageUtils.scaleThumb(this, THUMB_SIZE)
                    }
                }
                is ShareImageMedia -> {
                    //纯图片
                    if (!TextUtils.isEmpty(shareMedia.imagePath)) {
                        val imageObject = APImageObject(ShareImageUtils.scaleImage(shareMedia.imagePath!!, IMAGE_SIZE))
                        message.mediaObject = imageObject
                    } else if (shareMedia.imageBitmap != null) {
                        val imageObject = APImageObject(ShareImageUtils.scaleBitmap(shareMedia.imageBitmap, IMAGE_SIZE))
                        message.mediaObject = imageObject
                    } else {
                        throw Exception("WeChat share ImageMedia is null")
                    }
                }
                is ShareTextMedia -> {
                    //纯文本
                    val textObject = APTextObject()
                    textObject.text = handleString(shareMedia.text, 10240)
                    message.mediaObject = textObject
                }
                else -> {
                    throw Exception("AliPay is not support this shareMedia")
                }
            }
            val req = SendMessageToZFB.Req()
            req.message = message
            when (targetPlatform) {
                PlatformType.ALIPAY -> req.scene = SendMessageToZFB.Req.ZFBSceneSession
                else -> req.scene = SendMessageToZFB.Req.ZFBSceneTimeLine
            }
            emit(req)
        }.flowOn(Dispatchers.IO)
            .catch {
                shareListener?.onError(targetPlatform, errorMsg = it.message)
            }.collect {
                if (iApApi?.sendReq(it) == false) {
                    shareListener?.onError(targetPlatform, errorMsg = "AliPay share sendReq fail")
                }
            }
    }

    override fun isInstall(context: Context?): Boolean {
        return iApApi?.isZFBAppInstalled == true
    }

    override fun handleIntent(intent: Activity?) {
        super.handleIntent(intent)
        if (intent is IAPAPIEventHandler) {
            iApApi?.handleIntent(intent.intent, intent)
        }
    }

    override fun onResp(resp: Any?) {
        super.onResp(resp)
        if (resp !is SendMessageToZFB.Resp) {
            return
        }
        shareCallBack(resp)
    }

    private fun shareCallBack(resp: SendMessageToZFB.Resp) {
        when (resp.errCode) {
            BaseResp.ErrCode.ERR_OK -> {
                shareListener?.onComplete(targetPlatform)
            }
            BaseResp.ErrCode.ERR_USER_CANCEL -> {
                shareListener?.onCancel(targetPlatform)
            }
            else -> {
                shareListener?.onError(targetPlatform, resp.errCode, resp.errStr)
            }
        }
    }

    override fun destroy() {
        super.destroy()
        iApApi = null
    }
}