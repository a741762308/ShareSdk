package com.charles.sharesdk.core.manager

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.charles.sharesdk.core.R
import com.charles.sharesdk.core.SocialSdkApi
import com.charles.sharesdk.core.listener.IAuthListener
import com.charles.sharesdk.core.listener.IShareListener
import com.charles.sharesdk.core.media.*
import com.charles.sharesdk.core.platform.AbsSharePlatform
import com.charles.sharesdk.core.ui.BaseActionActivity
import com.charles.sharesdk.core.util.PlatformType
import com.charles.sharesdk.core.util.ShareImageUtils
import com.charles.sharesdk.permission.PermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking

/**
 *
 * @author dq on 2020/8/31.
 */
object PlatformManager {
    const val INVALID_PARAM = -1
    private const val ACTION_TYPE_LOGIN = 0
    private const val ACTION_TYPE_SHARE = 1

    private const val KEY_SHARE_MEDIA_OBJ = "KEY_SHARE_MEDIA_OBJ"
    const val KEY_ACTION_TYPE = "KEY_ACTION_TYPE"

    private var sAuthListener: IAuthListener? = null
    private var sShareListener: IShareListener? = null

    /**
     * 登录
     */
    fun authorize(activity: Activity, @PlatformType platform: String, listener: IAuthListener?) {
        val share: AbsSharePlatform? = SocialSdkApi.get().makePlatform(activity, platform)
        if (activity is LifecycleOwner) {
            val lifecycle = (activity as LifecycleOwner).lifecycle
            lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onHostActivityDestroy() {
                    share?.destroy()
                    SocialSdkApi.get().destroy()
                }
            })
        }
        if (share == null) {
            return
        }
        if (share.getUICallBackClass() != null) {
            sAuthListener = listener
            val intent = Intent(activity, share.getUICallBackClass())
            intent.putExtra(KEY_ACTION_TYPE, ACTION_TYPE_LOGIN)
            activity.startActivity(intent)
            activity.overridePendingTransition(0, 0)
        } else {
            share.authorize(activity, listener)
        }
    }

    /**
     * 分享
     */
    fun share(activity: Activity, @PlatformType platform: String, title: String, titleUrl: String?, text: String, imagePath: String?, shareListener: IShareListener?) {
        val share: AbsSharePlatform? = SocialSdkApi.get().makePlatform(activity, platform)
        if (activity is LifecycleOwner) {
            val lifecycle = (activity as LifecycleOwner).lifecycle
            lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onHostActivityDestroy() {
                    share?.destroy()
                    SocialSdkApi.get().destroy()
                }
            })
        }
        if (share == null) {
            return
        }
        val shareMedia: IShareMedia
        if (!TextUtils.isEmpty(titleUrl)) {
            val media = ShareWebPageMedia()
            media.title = title
            media.webPageUrl = titleUrl
            media.thumbPath = imagePath
            media.description = text
            shareMedia = media
        } else {
            if (TextUtils.equals(platform, PlatformType.SINA_WEIBO)) {
                val media = ShareTextImageMedia()
                media.imagePath = imagePath
                media.text = text
                shareMedia = media
            } else {
                if (!TextUtils.isEmpty(imagePath)) {
                    val media = ShareImageMedia()
                    media.imagePath = imagePath
                    shareMedia = media
                } else {
                    val media = ShareTextMedia()
                    media.text = title
                    shareMedia = media
                }
            }
        }
        share(share, activity, shareMedia, shareListener)
    }

    /**
     * 分享本地图片
     */
    fun share(activity: Activity, @PlatformType platform: String, imageBitmap: Bitmap? = null, imagePath: String? = null, shareListener: IShareListener?) {
        val share: AbsSharePlatform? = SocialSdkApi.get().makePlatform(activity, platform)
        if (activity is LifecycleOwner) {
            val lifecycle = (activity as LifecycleOwner).lifecycle
            lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onHostActivityDestroy() {
                    share?.destroy()
                    SocialSdkApi.get().destroy()
                }
            })
        }
        if (share == null) {
            return
        }
        val shareMedia: ShareImageMedia = if (TextUtils.equals(platform, PlatformType.SINA_WEIBO)) {
            //微博
            ShareTextImageMedia()
        } else {
            //其他
            ShareImageMedia()
        }
        if (!TextUtils.isEmpty(imagePath)) {
            shareMedia.imagePath = imagePath
        } else if (imageBitmap != null) {
            shareMedia.imageBitmap = imageBitmap
        }
        share(share, activity, shareMedia, shareListener)
    }

    /**
     * 小程序分享
     */
    fun shareProgram(activity: Activity,  @PlatformType platform: String, pageUrl: String, userName: String, path: String, withShareTicket: Boolean = false, miniProgramType: Int = 0, title: String, description: String, imagePath: String, shareListener: IShareListener?) {
        val share: AbsSharePlatform? = SocialSdkApi.get().makePlatform(activity, platform)
        if (activity is LifecycleOwner) {
            val lifecycle = (activity as LifecycleOwner).lifecycle
            lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onHostActivityDestroy() {
                    share?.destroy()
                    SocialSdkApi.get().destroy()
                }
            })
        }
        if (share == null) {
            return
        }
        val shareMedia = ShareMiniProgramMedia()
        shareMedia.webPageUrl = pageUrl
        shareMedia.userName = userName
        shareMedia.path = path
        shareMedia.withShareTicket = withShareTicket
        shareMedia.miniProgramType = miniProgramType
        shareMedia.title = title
        shareMedia.description = description
        shareMedia.thumbPath = imagePath
        share(share, activity, shareMedia, shareListener)
    }

    private fun share(share: AbsSharePlatform, activity: Activity, shareMedia: IShareMedia, shareListener: IShareListener?) {
        if (share.getUICallBackClass() != null) {
            if (shareMedia is ShareImageMedia && shareMedia.imageBitmap != null) {
                if (activity is FragmentActivity) {
                    PermissionUtils.requestPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                        if (it) {
                            runBlocking {
                                flow<IShareMedia> {
                                    shareMedia.imagePath = ShareImageUtils.getLocalImagePath(activity, shareMedia.imageBitmap)
                                    shareMedia.imageBitmap = null
                                    emit(shareMedia)
                                }
                                    .flowOn(Dispatchers.IO)
                                    .collect {
                                        sShareListener = shareListener
                                        startShareActivity(activity, share.getUICallBackClass(), shareMedia)
                                    }
                            }
                        } else {
                            shareListener?.onError(share.targetPlatform, -1, activity.getString(R.string.no_authorize_share))
                        }
                    }
                }
            } else {
                sShareListener = shareListener
                startShareActivity(activity, share.getUICallBackClass(), shareMedia)
            }
        } else {
            share.share(activity, shareMedia, shareListener)
        }
    }


    private fun startShareActivity(
        activity: Activity,
        className: Class<*>?,
        shareMedia: IShareMedia
    ) {
        val intent = Intent(activity, className)
        intent.putExtra(KEY_SHARE_MEDIA_OBJ, shareMedia)
        intent.putExtra(KEY_ACTION_TYPE, ACTION_TYPE_SHARE)
        activity.startActivity(intent)
        activity.overridePendingTransition(0, 0)
    }

    fun action(activity: BaseActionActivity, actionType: Int) {
        if (actionType == INVALID_PARAM) {
            return
        }
        when (actionType) {
            ACTION_TYPE_LOGIN -> actionAuthorize(activity)
            ACTION_TYPE_SHARE -> actionShare(activity)
        }
    }

    private fun actionAuthorize(activity: BaseActionActivity) {
        if (SocialSdkApi.get().getPlatform() == null) {
            activity.checkFinish()
            return
        }
        val intent: Intent = activity.intent
        if (intent.extras == null) {
            activity.checkFinish()
            return
        }
        val actionType = intent.getIntExtra(KEY_ACTION_TYPE, INVALID_PARAM)
        if (actionType != ACTION_TYPE_LOGIN) {
            activity.checkFinish()
            return
        }
        SocialSdkApi.get().getPlatform()?.authorize(activity, object : IAuthListener {
            override fun onComplete(platformType: String?, map: Map<String?, String?>?) {
                sAuthListener?.onComplete(platformType, map)
                sAuthListener = null
                activity.checkFinish()
            }

            override fun onCancel(platformType: String?) {
                sAuthListener?.onCancel(platformType)
                sAuthListener = null
                activity.checkFinish()
            }

            override fun onError(platformType: String?, msg: String?) {
                sAuthListener?.onError(platformType, msg)
                sAuthListener = null
                activity.checkFinish()
            }
        })
    }

    private fun actionShare(activity: BaseActionActivity) {
        if (SocialSdkApi.get().getPlatform() == null) {
            activity.checkFinish()
            return
        }
        val intent = activity.intent
        if (intent.extras == null) {
            activity.checkFinish()
            return
        }
        val actionType = intent.getIntExtra(KEY_ACTION_TYPE, INVALID_PARAM)
        if (actionType != ACTION_TYPE_SHARE) {
            activity.checkFinish()
            return
        }
        val shareMedia = activity.intent.getSerializableExtra(KEY_SHARE_MEDIA_OBJ) as IShareMedia
        SocialSdkApi.get().getPlatform()?.share(activity, shareMedia, object : IShareListener {
            override fun onComplete(formType: String) {
                super.onComplete(formType)
                sShareListener?.onComplete(formType)
                sShareListener = null
                activity.checkFinish()
            }


            override fun onCancel(formType: String) {
                super.onCancel(formType)
                sShareListener?.onCancel(formType)
                sShareListener = null
                activity.checkFinish()
            }

            override fun onError(formType: String, errorCode: Int?, errorMsg: String?) {
                super.onError(formType, errorCode, errorMsg)
                sShareListener?.onError(formType, errorCode, errorMsg)
                sShareListener = null
                activity.checkFinish()
            }
        })
    }
}