package com.charles.sharesdk.dingtalk

import com.android.dingtalk.share.ddsharemodule.IDDAPIEventHandler
import com.android.dingtalk.share.ddsharemodule.message.BaseReq
import com.android.dingtalk.share.ddsharemodule.message.BaseResp
import com.charles.sharesdk.core.ui.BaseActionActivity

/**
 *
 * @author dq on 2021/2/22.
 */
class DingTalkCallBackActivity : BaseActionActivity(), IDDAPIEventHandler {
    override fun onReq(rep: BaseReq?) {

    }

    override fun onResp(resp: BaseResp?) {
        handResp(resp)
    }
}