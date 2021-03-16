package com.charles.sharesdk.core

import android.app.Activity
import android.content.Context
import com.charles.sharesdk.core.listener.IAuthListener
import com.charles.sharesdk.core.manager.PlatformManager
import com.charles.sharesdk.core.platform.AbsSharePlatform
import com.charles.sharesdk.core.util.PlatformType

/**
 *
 * @author dq on 2020/8/31.
 */
class SocialSdkApi() : IDestroyable {


    companion object {
        private var sInstance: SocialSdkApi? = null

        @JvmStatic
        fun get(): SocialSdkApi {
            if (sInstance == null) {
                sInstance = SocialSdkApi()
            }
            return sInstance!!
        }
    }


    private var shareSdk: ShareSdk = ShareSdk()
    private lateinit var context: Context

    fun init(context: Context, options: ShareOptions?) {
        this.context = context
        shareSdk.init(options)
    }

    fun authorize(activity: Activity, @PlatformType platform: String, authListener: IAuthListener) {
        PlatformManager.authorize(activity, platform, authListener)
    }

    fun opts(): ShareOptions {
        return shareSdk.opts()
    }

    fun makePlatform(context: Context, @PlatformType platform: String): AbsSharePlatform? {
        return shareSdk.makePlatform(context, platform)
    }

    fun getPlatform(): AbsSharePlatform? {
        return shareSdk.getPlatform()
    }

    fun getContext(): Context {
        if (::context.isInitialized) {
            return context
        }
        throw Exception("SocialSdkApi must be init first")
    }

    override fun destroy() {
        super.destroy()
        shareSdk.release()
    }
}