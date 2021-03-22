package com.charles.sharesdk.wechat.http

/**
 *
 * @author dq on 2021/2/20.
 */
data class WxHttpResponse(
    var access_token: String?,
    var expires_in: String?,
    var refresh_token: String?,
    var openid: String?,
    var scope: String?,
    var unionid: String
)