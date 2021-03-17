package com.charles.sharesdk.dingtalk

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import com.android.dingtalk.share.ddsharemodule.DDShareApiFactory
import com.android.dingtalk.share.ddsharemodule.IDDAPIEventHandler
import com.android.dingtalk.share.ddsharemodule.IDDShareApi
import com.android.dingtalk.share.ddsharemodule.message.*
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

/**
 *
 * @author dq on 2021/2/20.
 */
private const val THUMB_SIZE = 32 * 1024L
private const val IMAGE_SIZE = 10 * 1024 * 1024L

class PlatformDingTalk private constructor(context: Context?, appId: String?, platform: String) : AbsSharePlatform(context, appId, platform) {

    private var iddShareApi: IDDShareApi? = null

    init {
        iddShareApi = DDShareApiFactory.createDDShareApi(context, appId, false)
        iddShareApi?.registerApp(appId)
    }

    class DingTalkShareFactory : IShareFactory {
        override fun create(context: Context?, target: String): AbsSharePlatform {
            val configs: PlatformConfig = SocialSdkApi.get().opts().getPlatformConfig(target)
            return PlatformDingTalk(context, configs.appId, target)
        }

        override fun getTargetPlatform(): String {
            return PlatformType.DINGTALK
        }
    }

    override fun authorize(activity: Activity?, authListener: IAuthListener?) {
        super.authorize(activity, authListener)
        if (activity == null) {
            return
        }
        if (!isInstall(activity)) {
            Toast.makeText(activity, R.string.dingding_not_install, Toast.LENGTH_SHORT).show()
            return
        }
        iddShareApi?.run {
            val req = SendAuth.Req()
            if (req.supportVersion > this.ddSupportAPI) {
                authListener?.onError(targetPlatform, "DingTalk version too low auth fail")
                return
            }
            req.scope = SendAuth.Req.SNS_LOGIN
            req.state = "none"
            if (!this.sendReq(req)) {
                authListener?.onError(targetPlatform, "DingTalk login send fail")
            }
        }
    }

    override fun share(activity: Activity?, shareMedia: IShareMedia?, shareListener: IShareListener?) {
        super.share(activity, shareMedia, shareListener)
        if (activity == null) {
            return
        }
        if (!isInstall(activity)) {
            Toast.makeText(activity, R.string.dingding_not_install, Toast.LENGTH_SHORT).show()
            return
        }
        share(shareMedia)
    }

    private fun share(shareMedia: IShareMedia?) = runBlocking {
        flow {
            val message = DDMediaMessage()
            when (shareMedia) {
                is ShareWebPageMedia -> {
                    //网页分享
                    val webPageObject = DDWebpageMessage()
                    webPageObject.mUrl = shareMedia.webPageUrl
                    message.mMediaObject = webPageObject
                    message.mTitle = handleString(shareMedia.title, 512)
                    message.mContent = handleString(shareMedia.description, 1024)
                    shareMedia.thumbPath?.run {
                        //缩略图32Kb
                        message.mThumbData = ShareImageUtils.scaleThumb(this, THUMB_SIZE)
                    }
                }
                is ShareImageMedia -> {
                    //纯图片
                    if (!TextUtils.isEmpty(shareMedia.imagePath)) {
                        val imageObject = DDImageMessage(ShareImageUtils.scaleImage(shareMedia.imagePath!!, IMAGE_SIZE))
                        message.mMediaObject = imageObject
                    } else if (shareMedia.imageBitmap != null) {
                        val imageObject = DDImageMessage(ShareImageUtils.scaleBitmap(shareMedia.imageBitmap, IMAGE_SIZE))
                        message.mMediaObject = imageObject
                    } else {
                        throw Exception("WeChat share ImageMedia is null")
                    }
                }
                is ShareTextMedia -> {
                    //纯文本
                    val textObject = DDTextMessage()
                    textObject.mText = handleString(shareMedia.text, 10240)
                    message.mMediaObject = textObject
                }
                else -> {
                    throw Exception("DingTalk is not support this shareMedia")
                }
            }
            val req = SendMessageToDD.Req()
            req.mMediaMessage = message
            emit(req)
        }.flowOn(Dispatchers.IO)
            .catch {
                shareListener?.onError(targetPlatform, errorMsg = it.message)
            }.collect {
                if (iddShareApi?.sendReq(it) == false) {
                    shareListener?.onError(targetPlatform, errorMsg = "DingTalk share sendReq fail")
                }
            }
    }

    override fun isInstall(context: Context?): Boolean {
        return iddShareApi?.isDDAppInstalled == true
    }

    override fun handleIntent(intent: Activity?) {
        super.handleIntent(intent)
        if (intent is IDDAPIEventHandler) {
            iddShareApi?.handleIntent(intent.intent, intent)
        }
    }

    override fun onResp(resp: Any?) {
        super.onResp(resp)
        if (resp !is BaseResp) {
            return
        }
        when (resp) {
            is SendAuth.Resp -> {
                authCallBack(resp)
            }
            is SendMessageToDD.Resp -> {
                shareCallBack(resp)
            }
        }
    }

    private fun authCallBack(resp: SendAuth.Resp) {
        when (resp.mErrCode) {
            BaseResp.ErrCode.ERR_OK -> {
                authListener?.run {
                    /**
                     * auth_code 获取refresh_token 需要签名校验，建议后端使用
                     * @see <a herf="https://developers.dingtalk.com/document/app/obtain-the-user-information-based-on-the-sns-temporary-authorization?spm=ding_open_doc.document.0.0.4431d394uC5iKS#topic-1995619"/>
                     */
                    val map: MutableMap<String?, String> = HashMap()
                    map[SocialConstants.AUTH_CODE] = resp.code
                    onComplete(targetPlatform, map)
                }
            }
            BaseResp.ErrCode.ERR_USER_CANCEL -> {
                authListener?.onCancel(targetPlatform)
            }
            else -> {
                authListener?.onError(targetPlatform, "${resp.mErrCode}:${resp.mErrStr}")
            }
        }
    }

    private fun shareCallBack(resp: SendMessageToDD.Resp) {
        when (resp.mErrCode) {
            BaseResp.ErrCode.ERR_OK -> {
                shareListener?.onComplete(targetPlatform)
            }
            BaseResp.ErrCode.ERR_USER_CANCEL -> {
                shareListener?.onCancel(targetPlatform)
            }
            else -> {
                shareListener?.onError(targetPlatform, resp.mErrCode, resp.mErrStr)
            }
        }
    }

    override fun destroy() {
        super.destroy()
        iddShareApi = null
    }
}