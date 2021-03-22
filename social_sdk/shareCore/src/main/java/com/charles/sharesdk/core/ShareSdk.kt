package com.charles.sharesdk.core

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.charles.sharesdk.core.platform.AbsSharePlatform
import com.charles.sharesdk.core.platform.IShareFactory
import com.charles.sharesdk.core.util.PlatformType
import java.util.concurrent.Executors

/**
 *
 * @author dq on 2020/8/31.
 */
class ShareSdk {

    private var opts: ShareOptions? = null
    private var sharePlatform: AbsSharePlatform? = null

    fun init(options: ShareOptions?) {
        this.opts = options
        Executors.newSingleThreadExecutor().execute {
            opts?.factoryClassList?.forEach {
                registerSdk(it)
            }
        }
    }

    fun registerSdk(factoryClazz: String) {
        try {
            val instance = Class.forName(factoryClazz).newInstance()
            if (instance is IShareFactory) {
                registerSdk(instance)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun registerSdk(factory: IShareFactory) {
        opts?.factories?.run {
            this[factory.getTargetPlatform()]?.let {
                this.remove(it)
            }
            this.put(factory.getTargetPlatform(), factory)
        }
    }

    fun makePlatform(context: Context, @PlatformType platform: String): AbsSharePlatform? {
        sharePlatform?.let {
            if (TextUtils.equals(platform, it.targetPlatform)) {
                return it
            }
        }
        sharePlatform?.destroy()
        val share = getPlatform(context, platform)
        if (share == null) {
            Log.e("ShareSdk", "找不到对应平台的实例，请检查配置 $platform")
        }
        sharePlatform = share
        return sharePlatform
    }

    fun getPlatform(): AbsSharePlatform? {
        return sharePlatform
    }

    private fun getPlatform(context: Context,@PlatformType platform: String): AbsSharePlatform? {
        sdkFactories()[platform]?.let {
            return it.create(context, platform)
        }
        return null
    }

    fun opts(): ShareOptions {
        if (opts == null) {
            throw RuntimeException("ShareSdk 初始化错误")
        }
        return opts!!
    }

    fun sdkFactories(): Map<String, IShareFactory> {
        return opts().factories
    }

    fun release() {
        sharePlatform?.destroy()
        sharePlatform = null
    }
}