package com.charles.sharesdk.core.platform

import android.content.Context
import androidx.annotation.NonNull

/**
 *
 * @author dq on 2020/8/31.
 */
interface IShareFactory {
    @NonNull
    fun create(context: Context?, target: String): AbsSharePlatform

    @NonNull
    fun getTargetPlatform(): String
}