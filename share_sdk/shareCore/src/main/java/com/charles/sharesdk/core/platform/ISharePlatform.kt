package com.charles.sharesdk.core.platform

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.charles.sharesdk.core.IDestroyable
import com.charles.sharesdk.core.listener.IAuthListener
import com.charles.sharesdk.core.listener.IShareListener
import com.charles.sharesdk.core.media.IShareMedia

/**
 *
 * @author dq on 2020/8/31.
 */
interface ISharePlatform : IDestroyable {

    fun authorize(activity: Activity?, authListener: IAuthListener?)

    fun share(activity: Activity?, shareMedia: IShareMedia?, shareListener: IShareListener?)

    fun onActivityResult(activity: Activity?, requestCode: Int, resultCode: Int, data: Intent?)

    fun handleIntent(intent: Activity?)

    fun isInstall(context: Context?): Boolean

    fun onReq(req: Any?)

    fun onResp(resp: Any?)

    fun getUICallBackClass(): Class<*>?
}