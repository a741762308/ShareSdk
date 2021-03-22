package com.charles.sharesdk.core.util

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

/**
 *
 * @author dq on 2020/10/16.
 */
object ShareUtils {

    fun isAppInstall(
        context: Context?,
        packageName: String,
        vararg requireSignature: String?
    ): Boolean {
        try {
            val packageInfo = context?.packageManager?.getPackageInfo(
                packageName,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) PackageManager.GET_SIGNING_CERTIFICATES
                else PackageManager.GET_SIGNATURES
            )
                ?: return false
            if (requireSignature.isNotEmpty()) {
                val appSignatures = HashSet(listOf(requireSignature))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.signingInfo.apkContentsSigners
                        .forEach { signature ->
                            if (!appSignatures.contains(signature)) {
                                return false
                            }
                        }
                } else {
                    packageInfo.signatures
                        .forEach { signature ->
                            if (!appSignatures.contains(signature)) {
                                return false
                            }
                        }
                }
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun getUriForFile(file: File, context: Context): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context.applicationContext, context.packageName + ".provider", file)
        } else {
            Uri.fromFile(file)
        }
    }
}