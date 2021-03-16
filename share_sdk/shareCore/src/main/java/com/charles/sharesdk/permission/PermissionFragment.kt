package com.charles.sharesdk.permission

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 *
 * @author dq on 2020/10/21.
 */
class PermissionFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    companion object {
        private var onPermissionResult: ((Boolean) -> Unit?)? = null

        fun requestPermission(
            activity: FragmentActivity,
            vararg permissions: String,
            result: (Boolean) -> Unit
        ) {
            this.onPermissionResult = result
            var fragment = activity.supportFragmentManager.findFragmentByTag(PermissionUtils.TAG)
            if (fragment == null) {
                fragment = PermissionFragment()
                activity.supportFragmentManager
                    .beginTransaction()
                    .add(fragment, PermissionUtils.TAG)
                    .commitNowAllowingStateLoss()
            }
            val permission = (permissions.filter {
                ContextCompat.checkSelfPermission(
                    activity,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            } as ArrayList<String>).toArray(arrayOf<String>())
            if (permission.isEmpty()) {
                onPermissionResult?.invoke(true)
            } else {
                fragment.requestPermissions(permission, PermissionUtils.PERMISSIONS_REQUEST_CODE)
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PermissionUtils.PERMISSIONS_REQUEST_CODE) {
            return
        }

        if (PermissionUtils.allGranted(grantResults)) {
            onPermissionResult?.invoke(true)
        } else {
            onPermissionResult?.invoke(false)
        }
    }
}