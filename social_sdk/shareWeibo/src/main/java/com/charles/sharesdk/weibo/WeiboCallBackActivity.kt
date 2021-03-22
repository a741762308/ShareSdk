package com.charles.sharesdk.weibo

import com.charles.sharesdk.core.ui.BaseActionActivity
import com.sina.weibo.sdk.constant.WBConstants
import com.sina.weibo.sdk.share.WbShareCallback

/**
 *
 * @author dq on 2021/2/18.
 */
class WeiboCallBackActivity : BaseActionActivity(), WbShareCallback {
    override fun onWbShareSuccess() {
        handResp(WBConstants.ErrorCode.ERR_OK)
    }

    override fun onWbShareCancel() {
        handResp(WBConstants.ErrorCode.ERR_CANCEL)
    }

    override fun onWbShareFail() {
        handResp(WBConstants.ErrorCode.ERR_FAIL)
    }
}