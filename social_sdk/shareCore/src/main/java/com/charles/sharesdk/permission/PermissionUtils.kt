package com.charles.sharesdk.permission

import android.content.pm.PackageManager
import androidx.fragment.app.FragmentActivity

/**
 *
 * @author dq on 2020/10/21.
 */
object PermissionUtils {
    val TAG: String = PermissionUtils::class.java.simpleName
    const val PERMISSIONS_REQUEST_CODE = 42

    fun requestPermission(
        activity: FragmentActivity,
        vararg permissions: String,
        result: (Boolean) -> Unit
    ) {
        PermissionFragment.requestPermission(activity, *permissions, result = result)
    }

    fun allGranted(grantResults: IntArray): Boolean {
        grantResults.forEach {
            it != PackageManager.PERMISSION_GRANTED
            return false
        }
        return true
    }
}