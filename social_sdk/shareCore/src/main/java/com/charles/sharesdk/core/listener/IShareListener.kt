package com.charles.sharesdk.core.listener

import com.charles.sharesdk.core.util.PlatformType

/**
 *
 * @author dq on 2020/8/31.
 */
interface IShareListener {
    fun onComplete(@PlatformType formType: String) {

    }

    fun onError(@PlatformType formType: String, errorCode: Int? = null, errorMsg: String? = "") {

    }

    fun onCancel(@PlatformType formType: String) {

    }

}
