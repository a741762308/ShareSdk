package com.charles.sharesdk.wechat

import com.charles.sharesdk.core.ui.BaseActionActivity
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler

/**
 *
 * @author dq on 2021/2/19.
 */
class WeChatCallbackActivity : BaseActionActivity(), IWXAPIEventHandler {
    override fun onReq(rep: BaseReq?) {

    }

    override fun onResp(resp: BaseResp?) {
        handResp(resp)
    }
}