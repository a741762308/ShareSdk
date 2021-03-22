package com.charles.sharesdk.core.listener

import com.charles.sharesdk.core.util.PlatformType

/**
 *
 * @author dq on 2020/8/31.
 */
interface IAuthListener {
    fun onComplete(@PlatformType platformType: String?, map: Map<String?, String?>?)

    fun onError(@PlatformType platformType: String?, msg: String?) {}

    fun onCancel(@PlatformType platformType: String?) {}
}