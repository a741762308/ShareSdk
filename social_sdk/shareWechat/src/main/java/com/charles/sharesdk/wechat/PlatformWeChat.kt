package com.charles.sharesdk.wechat

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import com.charles.sharesdk.core.SocialSdkApi
import com.charles.sharesdk.core.constant.SocialConstants
import com.charles.sharesdk.core.listener.IAuthListener
import com.charles.sharesdk.core.listener.IShareListener
import com.charles.sharesdk.core.media.*
import com.charles.sharesdk.core.model.PlatformConfig
import com.charles.sharesdk.core.platform.AbsSharePlatform
import com.charles.sharesdk.core.platform.IShareFactory
import com.charles.sharesdk.core.util.PlatformType
import com.charles.sharesdk.core.util.ShareImageUtils
import com.charles.sharesdk.wechat.http.WxHttpResponse
import com.google.gson.Gson
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.*
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**
 *
 * @author dq on 2021/2/19.
 */
private const val THUMB_SIZE = 32 * 1024L
private const val MIN_PROGRAM_THUMB_SIZE = 128 * 1024L
private const val IMAGE_SIZE = 10 * 1024 * 1024L

class PlatformWeChat private constructor(context: Context?, appId: String?, appSecret: String?, onlyAuthCode: Boolean?, platform: String) :
    AbsSharePlatform(context, appId, platform) {
    private var wxApi: IWXAPI? = null

    init {
        this.appSecret = appSecret
        wxApi = WXAPIFactory.createWXAPI(context, appId)
        wxApi?.registerApp(appId)
    }

    open class WeChatShareFactory : IShareFactory {
        override fun create(context: Context?, target: String): AbsSharePlatform {
            val configs: PlatformConfig = SocialSdkApi.get().opts().getPlatformConfig(target)
            return PlatformWeChat(context, configs.appId, configs.appSecret, configs.onlyAuthCode, target)
        }

        override fun getTargetPlatform(): String {
            return PlatformType.WECHAT
        }
    }

    class WeChatMomentsShareFactory : WeChatShareFactory() {

        override fun getTargetPlatform(): String {
            return PlatformType.WECHAT_MOMENTS
        }
    }

    override fun authorize(activity: Activity?, authListener: IAuthListener?) {
        super.authorize(activity, authListener)
        if (activity == null) {
            return
        }
        if (!isInstall(activity)) {
            Toast.makeText(activity, R.string.wx_not_install, Toast.LENGTH_SHORT).show()
            return
        }
        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo,snsapi_friend,snsapi_message"
        req.state = "none"
        if (wxApi?.sendReq(req) == false) {
            authListener?.onError(targetPlatform, "WeChat login sendReq fail")
        }
    }

    override fun share(activity: Activity?, shareMedia: IShareMedia?, shareListener: IShareListener?) {
        super.share(activity, shareMedia, shareListener)
        if (activity == null) {
            return
        }
        if (!isInstall(activity)) {
            Toast.makeText(activity, R.string.wx_not_install, Toast.LENGTH_SHORT).show()
            return
        }
        share(shareMedia)
    }

    private fun share(shareMedia: IShareMedia?) = runBlocking {
        flow {
            val message = WXMediaMessage()
            when (shareMedia) {
                is ShareMiniProgramMedia -> {
                    //微信小程序
                    val miniProgramObject = WXMiniProgramObject()
                    miniProgramObject.webpageUrl = shareMedia.webPageUrl
                    miniProgramObject.userName = shareMedia.userName
                    miniProgramObject.path = shareMedia.path
                    miniProgramObject.withShareTicket = shareMedia.withShareTicket
                    miniProgramObject.miniprogramType = shareMedia.miniProgramType
                    message.mediaObject = miniProgramObject
                    message.title = handleString(shareMedia.title, WXMediaMessage.TITLE_LENGTH_LIMIT)
                    message.description = handleString(shareMedia.description, WXMediaMessage.DESCRIPTION_LENGTH_LIMIT)
                    shareMedia.thumbPath?.run {
                        //小程序消息封面图片,小于128k
                        message.thumbData = ShareImageUtils.scaleThumb(this, MIN_PROGRAM_THUMB_SIZE)
                    }
                }
                is ShareWebPageMedia -> {
                    //网页分享
                    val webPageObject = WXWebpageObject()
                    webPageObject.webpageUrl = shareMedia.webPageUrl
                    message.mediaObject = webPageObject
                    message.title = handleString(shareMedia.title, WXMediaMessage.TITLE_LENGTH_LIMIT)
                    message.description = handleString(shareMedia.description, WXMediaMessage.DESCRIPTION_LENGTH_LIMIT)
                    shareMedia.thumbPath?.run {
                        //缩略图32Kb
                        message.thumbData = ShareImageUtils.scaleThumb(this, THUMB_SIZE)
                    }
                }
                is ShareImageMedia -> {
                    //纯图片
                    if (!TextUtils.isEmpty(shareMedia.imagePath)) {
                        val imageObject = WXImageObject(ShareImageUtils.scaleImage(shareMedia.imagePath!!, IMAGE_SIZE))
                        message.mediaObject = imageObject
                    } else if (shareMedia.imageBitmap != null) {
                        val imageObject = WXImageObject(ShareImageUtils.scaleBitmap(shareMedia.imageBitmap, IMAGE_SIZE))
                        message.mediaObject = imageObject
                    } else {
                        throw Exception("WeChat share ImageMedia is null")
                    }
                }
                is ShareTextMedia -> {
                    //纯文本
                    val textObject = WXTextObject()
                    textObject.text = handleString(shareMedia.text, 10240)
                    message.mediaObject = textObject
                }
                else -> {
                    throw Exception("WeChat is not support this shareMedia")
                }
            }
            val req = SendMessageToWX.Req()
            req.message = message
            when (targetPlatform) {
                PlatformType.WECHAT -> req.scene = SendMessageToWX.Req.WXSceneSession
                else -> req.scene = SendMessageToWX.Req.WXSceneTimeline
            }
            emit(req)
        }.flowOn(Dispatchers.IO)
            .catch {
                shareListener?.onError(targetPlatform, errorMsg = it.message)
            }.collect {
                if (wxApi?.sendReq(it) == false) {
                    shareListener?.onError(targetPlatform, errorMsg = "WeChat share sendReq fail")
                }
            }
    }

    override fun isInstall(context: Context?): Boolean {
        return wxApi?.isWXAppInstalled == true
    }

    override fun handleIntent(intent: Activity?) {
        super.handleIntent(intent)
        if (intent is IWXAPIEventHandler) {
            wxApi?.handleIntent(intent.intent, intent)
        }
    }

    override fun onResp(resp: Any?) {
        super.onResp(resp)
        if (resp !is BaseResp) {
            return
        }
        when (resp.type) {
            ConstantsAPI.COMMAND_SENDAUTH -> {
                authCallBack(resp as SendAuth.Resp)
            }
            ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX -> {
                shareCallBack(resp as SendMessageToWX.Resp)
            }
        }
    }

    private fun authCallBack(resp: SendAuth.Resp) {
        when (resp.errCode) {
            BaseResp.ErrCode.ERR_OK -> {
                authListener?.run {
                    if (SocialSdkApi.get().opts().getWeChatConfig().onlyAuthCode) {
                        val map: MutableMap<String?, String?> = HashMap()
                        map[SocialConstants.AUTH_CODE] = resp.code
                        onComplete(targetPlatform, map)
                    } else {
                        CoroutineScope(Dispatchers.Main).launch {
                            val response = async(Dispatchers.IO) {
                                val url = URL("https://api.weixin.qq.com/sns/oauth2/access_token?appid=${appId}&secret=${appSecret}&code=${resp.code}&grant_type=authorization_code")
                                val connect = url.openConnection() as HttpURLConnection
                                connect.doInput = true
                                connect.doOutput = true
                                connect.requestMethod = "GET"
                                connect.connectTimeout = 5000
                                connect.readTimeout = 5000
                                connect.connect()
                                val br = BufferedReader(InputStreamReader(connect.inputStream))
                                Gson().fromJson(br, WxHttpResponse::class.java)
                            }
                            val wxHttpResponse = response.await()
                            val map: MutableMap<String?, String?> = HashMap()
                            map[SocialConstants.U_ID] = wxHttpResponse.openid
                            map[SocialConstants.ACCESS_TOKEN] = wxHttpResponse.access_token
                            map[SocialConstants.REFRESH_TOKEN] = wxHttpResponse.refresh_token
                            onComplete(targetPlatform, map)
                        }
                    }
                }
            }
            BaseResp.ErrCode.ERR_USER_CANCEL -> {
                authListener?.onCancel(targetPlatform)
            }
            else -> {
                authListener?.onError(targetPlatform, "${resp.errCode}:${resp.errStr}")
            }
        }
    }

    private fun shareCallBack(resp: SendMessageToWX.Resp) {
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
        wxApi?.detach()
        wxApi = null
    }
}