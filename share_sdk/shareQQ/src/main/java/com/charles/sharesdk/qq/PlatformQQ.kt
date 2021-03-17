package com.charles.sharesdk.qq

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.charles.sharesdk.core.SocialSdkApi
import com.charles.sharesdk.core.constant.SocialConstants
import com.charles.sharesdk.core.listener.IAuthListener
import com.charles.sharesdk.core.listener.IShareListener
import com.charles.sharesdk.core.media.IShareMedia
import com.charles.sharesdk.core.media.ShareImageMedia
import com.charles.sharesdk.core.media.ShareWebPageMedia
import com.charles.sharesdk.core.model.PlatformConfig
import com.charles.sharesdk.core.platform.AbsSharePlatform
import com.charles.sharesdk.core.platform.IShareFactory
import com.charles.sharesdk.core.util.PlatformType
import com.charles.sharesdk.core.util.ShareImageUtils
import com.charles.sharesdk.core.util.ShareUtils
import com.charles.sharesdk.permission.PermissionUtils
import com.tencent.connect.common.Constants
import com.tencent.connect.share.QQShare
import com.tencent.connect.share.QzoneShare
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.util.*

/**
 *
 * @author dq on 2020/8/31.
 */
private const val QQ_PACKAGE = "com.tencent.mobileqq"
private const val QQ_LITE_PACKAGE = "com.tencent.qqlite"
private const val TIM_PACKAGE = "com.tencent.tim"
private const val IMAGE_SIZE = 5 * 1024 * 1024L

class PlatformQQ private constructor(context: Context?, appId: String?, platform: String) : AbsSharePlatform(context, appId, platform) {
    private var tencent: Tencent?

    init {
        tencent = Tencent.createInstance(appId, context)
    }

    open class QQShareFactory : IShareFactory {
        override fun create(context: Context?, target: String): AbsSharePlatform {
            val configs: PlatformConfig = SocialSdkApi.get().opts().getPlatformConfig(target)
            return PlatformQQ(context, configs.appId, target)
        }

        override fun getTargetPlatform(): String {
            return PlatformType.QQ
        }
    }

    class QQZoneShareFactory : QQShareFactory() {

        override fun getTargetPlatform(): String {
            return PlatformType.QQ_ZONE
        }
    }

    override fun authorize(activity: Activity?, authListener: IAuthListener?) {
        super.authorize(activity, authListener)
//        if (tencent?.isSessionValid == false) {
        tencent?.login(activity, "all", loginUiListener)
//            return
//        }
//        if (authListener != null) {
//            val map: MutableMap<String?, String?> = HashMap()
//            map["uid"] = tencent?.openId
//            map["access_token"] = tencent?.accessToken
//            authListener.onComplete(targetPlatform, map)
//        }
    }

    private val loginUiListener = object : IUiListener {
        override fun onComplete(p0: Any?) {
            if (p0 !is JSONObject) {
                authListener?.onError(targetPlatform, "QQ auth response null")
                return
            }
            try {
                val token = p0.getString(Constants.PARAM_ACCESS_TOKEN)
                val expires = p0.getString(Constants.PARAM_EXPIRES_IN)
                val openId = p0.getString(Constants.PARAM_OPEN_ID)
                if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
                    tencent?.setAccessToken(token, expires)
                    tencent?.openId = openId
                }
                authListener?.run {
                    val map: MutableMap<String?, String> = HashMap()
                    map[SocialConstants.U_ID] = openId
                    map[SocialConstants.ACCESS_TOKEN] = token
                    onComplete(targetPlatform, map)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                authListener?.onError(targetPlatform, "QQ auth auth json error")
            }
        }

        override fun onError(p0: UiError?) {
            authListener?.onError(targetPlatform, "${p0?.errorCode}:${p0?.errorMessage}")
        }

        override fun onCancel() {
            authListener?.onCancel(targetPlatform)
        }

    }

    override fun share(activity: Activity?, shareMedia: IShareMedia?, shareListener: IShareListener?) {
        super.share(activity, shareMedia, shareListener)
        if (activity == null) {
            return
        }
        if (!isInstall(activity)) {
            Toast.makeText(activity, R.string.qq_not_install, Toast.LENGTH_SHORT).show()
            return
        }
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (activity is FragmentActivity) {
                PermissionUtils.requestPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    if (it) {
                        share(activity, shareMedia)
                    } else {
                        Toast.makeText(activity, R.string.no_authorize_share_qq, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(activity, R.string.no_authorize_share_qq, Toast.LENGTH_SHORT).show()
            }
        } else {
            share(activity, shareMedia)
        }
    }

    private fun share(activity: Activity, shareMedia: IShareMedia?) = runBlocking {
        flow {
            val params = Bundle()
            if (targetPlatform == PlatformType.QQ) {
                //分享到QQ
                when (shareMedia) {
                    is ShareWebPageMedia -> {
                        //网页分享
                        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT)
                        params.putString(QQShare.SHARE_TO_QQ_TITLE, handleString(shareMedia.title, QQShare.QQ_SHARE_TITLE_MAX_LENGTH))
                        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, handleString(shareMedia.description, QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH))
                        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareMedia.webPageUrl)
                        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareMedia.thumbPath)
                    }
                    is ShareImageMedia -> {
                        //纯图片分享,只支持本地图片，最大5M
                        if (!TextUtils.isEmpty(shareMedia.imagePath)) {
                            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, ShareImageUtils.getLocalImagePath(null, shareMedia.imagePath, IMAGE_SIZE))
                        }
                        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE)
                    }
                    else -> {
                        throw Exception("QQ is not support this shareMedia")
                    }
                }
            } else if (targetPlatform == PlatformType.QQ_ZONE) {
                //分享到QQ空间
                when (shareMedia) {
                    is ShareWebPageMedia -> {
                        //网页分享
                        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT)
                        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareMedia.title)
                        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareMedia.description)
                        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareMedia.webPageUrl)
                        val imageList = arrayListOf<String?>()
                        imageList.add(shareMedia.thumbPath)
                        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageList)
                    }
                    else -> {
                        throw Exception("QQ_Zone is not support this shareMedia")
                    }
                }
            }
            emit(params)
        }
            .flowOn(Dispatchers.IO)
            .catch {
                shareListener?.onError(targetPlatform, errorMsg = it.message)
            }.collect {
                tencent?.run {
                    if (targetPlatform == PlatformType.QQ) {
                        shareToQQ(activity, it, shareUiListener)
                    } else if (targetPlatform == PlatformType.QQ_ZONE) {
                        shareToQzone(activity, it, shareUiListener)
                    }
                }
            }
    }

    private val shareUiListener = object : IUiListener {
        override fun onComplete(p0: Any?) {
            shareListener?.onComplete(targetPlatform)
        }

        override fun onError(uiError: UiError?) {
            shareListener?.onError(targetPlatform, uiError?.errorCode, uiError?.errorMessage)
        }

        override fun onCancel() {
            shareListener?.onCancel(targetPlatform)
        }

    }

    override fun onActivityResult(
        activity: Activity?,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(activity, requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_QQ_SHARE || requestCode == Constants.REQUEST_QZONE_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, shareUiListener)
        } else if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.handleResultData(data, loginUiListener)
        }
    }

    override fun isInstall(context: Context?): Boolean {
        return ShareUtils.isAppInstall(context, QQ_PACKAGE)
                || ShareUtils.isAppInstall(context, QQ_LITE_PACKAGE)
                || ShareUtils.isAppInstall(context, TIM_PACKAGE)
    }

    override fun getUICallBackClass(): Class<*>? {
        return QQCallBackActivity::class.java
    }

    override fun destroy() {
        super.destroy()
        tencent?.releaseResource()
        tencent = null
    }
}