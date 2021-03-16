package com.charles.sharesdk.core.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.charles.sharesdk.core.SocialSdkApi
import com.charles.sharesdk.core.manager.PlatformManager
import com.charles.sharesdk.core.manager.PlatformManager.INVALID_PARAM
import com.charles.sharesdk.core.manager.PlatformManager.KEY_ACTION_TYPE
import com.charles.sharesdk.core.platform.AbsSharePlatform

/**
 *
 * @author dq on 2020/8/31.
 */
open class BaseActionActivity : FragmentActivity() {
    
    protected fun handResp(resp: Any?) {
        getPlatFrom()?.onResp(resp)
        checkFinish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPlatFrom()?.handleIntent(this)
        PlatformManager.action(this, intent.getIntExtra(KEY_ACTION_TYPE, INVALID_PARAM))
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        getPlatFrom()?.handleIntent(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getPlatFrom()?.onActivityResult(this, requestCode, resultCode, data)
    }

    private fun getPlatFrom(): AbsSharePlatform? {
        val platform: AbsSharePlatform? = SocialSdkApi.get().getPlatform()
        return if (platform == null) {
            checkFinish()
            null
        } else {
            platform
        }
    }

    open fun checkFinish() {
        if (!isFinishing && !isDestroyed) {
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}