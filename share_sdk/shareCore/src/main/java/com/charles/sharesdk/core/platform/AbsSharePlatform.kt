package com.charles.sharesdk.core.platform

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.charles.sharesdk.core.listener.IAuthListener
import com.charles.sharesdk.core.listener.IShareListener
import com.charles.sharesdk.core.media.IShareMedia

/**
 *
 * @author dq on 2020/8/31.
 */
abstract class AbsSharePlatform(context: Context?, appId: String?, platform: String) :
    ISharePlatform {
    protected var authListener: IAuthListener? = null
    protected var shareListener: IShareListener? = null
    val targetPlatform: String = platform
    protected val appId: String? = appId
    protected var appSecret: String? = null

    override fun authorize(activity: Activity?, authListener: IAuthListener?) {
        this.authListener = authListener
    }

    override fun share(activity: Activity?, shareMedia: IShareMedia?, shareListener: IShareListener?) {
        this.shareListener = shareListener
    }

    override fun onActivityResult(activity: Activity?, requestCode: Int, resultCode: Int, data: Intent?) {

    }

    override fun handleIntent(intent: Activity?) {

    }

    override fun isInstall(context: Context?): Boolean {
        return false
    }

    override fun onReq(req: Any?) {

    }

    override fun onResp(resp: Any?) {

    }

    override fun getUICallBackClass(): Class<*>? {
        return null
    }

    protected fun handleString(originStr: String?, limit: Int): String? {
        if (originStr != null && limit > 0 && originStr.length > limit) {
            return originStr.substring(0, limit)
        }
        return originStr
    }
}