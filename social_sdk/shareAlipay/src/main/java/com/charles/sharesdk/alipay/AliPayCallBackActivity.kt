package com.charles.sharesdk.alipay

import com.alipay.share.sdk.openapi.BaseReq
import com.alipay.share.sdk.openapi.BaseResp
import com.alipay.share.sdk.openapi.IAPAPIEventHandler
import com.charles.sharesdk.core.ui.BaseActionActivity

/**
 *
 * @author dq on 2021/2/20.
 */
class AliPayCallBackActivity : BaseActionActivity(), IAPAPIEventHandler {
    override fun onReq(req: BaseReq?) {

    }

    override fun onResp(resp: BaseResp?) {
        handResp(resp)
    }
}