# ShareSdk

一款快速接入三方登录与分享的插件，旨在解决繁杂的接入流程，使用简单方便。目前已支持QQ、TIM、QQ轻聊版、微信、微博、支付宝、钉钉等

## 背景
之前一直在使用Mob的ShareSdk，无论是在eclipse时代还是目前的AS。但在AS上时常出现问题，它经常更新而且还下载失败，导致我每次都要用离线模式，影响使用体验。于是ShareSDK插件应运而生。

## 接入使用

1、project build.gradle
```groovy
dependencies {
    //添加插件
     classpath "com.charles.share.sdk:share-sdk:0.0.1"
}
```
2、app或lib buil.gradle
```groovy
apply plugin: 'com.charles.sharesdk'

shareSdk {
    debug {
        qq {
            appId = ""
            appSecret = ""
        }
        wechat {
            appId = ""
            appSecret = ""
        }
        weibo {
            appId = ""
            appSecret = ""
            url = ""
        }
        alipay {
            appId = ""
        }
        dingtalk {
            appId = ""
        }
    }
    release {
        qq {
            appId = ""
            appSecret = ""
        }
        wechat {
            appId = ""
            appSecret = ""
        }
        weibo {
            appId = ""
            appSecret = ""
            url = ""
        }
        alipay {
            appId = ""
        }
        dingtalk {
            appId = ""
        }
    }
}
```
* 需要什么平台就添加相应的配置信息，插件会自动依赖相应的aar包

3、Application
```klotlin
 override fun onCreate() {
    super.onCreate()
    val options = ShareOptions.Builder(this).build()
    SocialSdkApi.get().init(this, options)
}
```
* 平台的配置信息也可以通过 ShareOptions.Builder实现，提供了相应的api

## 如何使用
只需要很少的代码即可实现分享或登录功能,主要使用PlatformManager提供的authorize、share、shareProgram等api，详细使用可参看相应Demo
### 登录
```kotlin
 PlatformManager.authorize(this, PlatformType.QQ, object :IAuthListener{
    override fun onComplete(platformType: String?, map: Map<String?, String?>?) {
                  
    }

    override fun onCancel(platformType: String?) {
                
    }

    override fun onError(platformType: String?, msg: String?) {
                   
    }
})
```
* 微信登录 支持仅获取authCode,由后端通过code获取
access_token，只需配置onlyAuthCode为ture即可，默认false即返回access_token。[微信code获取token相关文档](https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Authorized_API_call_UnionID.html)
* 支付宝与钉钉只返回authCode，获取access_token 涉及参数签名检验，建议放到后端获取。[支付宝登录](https://opendocs.alipay.com/open/218/sxc60m) 与[钉钉登录](https://developers.dingtalk.com/document/mobile-app-guide/android-platform-application-authorization-login-access)

### 图片分享
```klotlin
 PlatformManager.share(this, PlatformType.QQ, null, "/storage/emulated/0/Download/screenshot_20210107_180826.png", object :IShareListener{
    override fun onComplete(formType: String) {
                   
    }

    override fun onCancel(formType: String) {
                  
    }

    override fun onError(formType: String, errorCode: Int?, errorMsg: String?) {
                    
    }
})
```
### 网页分享
```kotlin
PlatformManager.share(this, PlatformType.QQ_ZONE, "哈哈", "https://www.baidu.com", "", "/storage/emulated/0/Download/screenshot_20210107_180826.png", object :IShareListener{
    override fun onComplete(formType: String) {
                   
    }

    override fun onCancel(formType: String) {
                    
    }

    override fun onError(formType: String, errorCode: Int?, errorMsg: String?) {
                    
    }
})
```
### 微信小程序
```kotlin
PlatformManager.shareProgram(
    this,
    PlatformType.WECHAT,
    "https://m.yxzq.com/mini/guess-stock/index.html",
    "gh_a116b4c8b2c6",
    "pages/index/index?invitationCode=84zq&nickName=***&avatar=http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLGqibUOngLiboAlmObZJarzDaiaU1LFoIKz5enIic05nUibsSJMwmhWXWmibianhKxwsElVbOhhlrC1cib4g/132",
    false,
    0,
    "***邀你玩热股猜涨跌，冲榜赢现金股票豪礼",
    "10万港币现金/iPhone 12 Pro / 特斯拉股票 等赛季大奖等你来抽",
    "https://jy-common-prd-1257884527.cos.ap-guangzhou.myqcloud.com/mini/guess-stock/share.png",
    object :IShareListener{
        override fun onComplete(formType: String) {
                       
        }

        override fun onCancel(formType: String) {
                       
        }

        override fun onError(formType: String, errorCode: Int?, errorMsg: String?) {
                        
        }
    }
)
```
## 自定义平台
支持自定义平台如接入手机厂商登录，只需2步
1. 继承AbsSharePlatform
```kotlin
class PlatformHuaWei private constructor(context: Context?, appId: String?, platform: String) : AbsSharePlatform(context, appId, platform) {
    
    override fun authorize(activity: Activity?, authListener: IAuthListener?) {
        super.authorize(activity, authListener)
    }

    override fun share(activity: Activity?, shareMedia: IShareMedia?, shareListener: IShareListener?) {
        super.share(activity, shareMedia, shareListener)
    }

    override fun onActivityResult(activity: Activity?, requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(activity, requestCode, resultCode, data)
    }

    override fun handleIntent(intent: Activity?) {
        super.handleIntent(intent)
    }

    override fun onResp(resp: Any?) {
        super.onResp(resp)
    }

    override fun getUICallBackClass(): Class<*>? {
        return super.getUICallBackClass()
    }
}
```
* getUICallBackClass 返回用于实现分享的透明Activity
或者需要实现回调的activity

根据实际情况重写相应方法
2. 实现IShareFactory
```kotlin
class PlatformHuaWei private constructor(context: Context?, appId: String?, platform: String) : AbsSharePlatform(context, appId, platform) {

    class HuaWeiShareFactory : IShareFactory {
        override fun create(context: Context?, target: String): AbsSharePlatform {
            return PlatformHuaWei(context, "appId", target)
        }

        override fun getTargetPlatform(): String {
            return "HuaWei"
        }

    }
    //其他...
}
```
* 使用静态内部类调用私有构造
* 插件会自动寻找实现IShareFactory的类，无需特别处理
* targetform建议使用注解类
3. 调用PlatformManager的authorize方法
```klotlin
PlatformManager.authorize(this, "HuaWei", object :IAuthListener{
    override fun onComplete(platformType: String?, map: Map<String?, String?>?) {
                  
    }

    override fun onCancel(platformType: String?) {
                
    }

    override fun onError(platformType: String?, msg: String?) {
                   
    }
})
```
## 混淆设置
SDK已支持混淆，不需要特别设置
## 相关文档
1. 钉钉
* [登录SDK接入流程](https://developers.dingtalk.com/document/mobile-app-guide/android-platform-application-authorization-login-access)
* [登录SDK下载地址](https://files.alicdn.com/tpsservice/e76b61f0c65ddda660bb07f265c35b19.zip?spm=ding_open_doc.document.0.0.562749edEAkgkq&file=e76b61f0c65ddda660bb07f265c35b19.zip)
* [分享SDK接入流程](https://developers.dingtalk.com/document/mobile-app-guide/android-sharing-sdk-access-process)
* [分享SDK下载地址](https://developers.dingtalk.com/document/mobile-app-guide/sdk-download)
2. 支付宝
* [极简版授权SDK接入流程](https://opendocs.alipay.com/open/218/sxc60m)
* [支付SDK下载地址](https://opendocs.alipay.com/open/54/104509)
* [分享SDK接入流程](https://opendocs.alipay.com/open/215/105104)
* [分享SDK下载地址](https://opendocs.alipay.com/open/54/104508)
3. 微信
* [登录SDK接入流程](https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Development_Guide.html)
* [分享SDK键入流程](https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Share_and_Favorites/Android.html)
* [SDK下载地址](https://developers.weixin.qq.com/doc/oplatform/Downloads/Android_Resource.html)
4. 微博
* [接入流程](https://github.com/sinaweibosdk/weibo_android_sdk)
5. QQ
* [QQ登录Api](https://wiki.open.qq.com/wiki/mobile/API%E8%B0%83%E7%94%A8%E8%AF%B4%E6%98%8E#1.1_.E7.99.BB.E5.BD.95.2F.E6.A0.A1.E9.AA.8C.E7.99.BB.E5.BD.95.E6.80.81)
* [QQ分享Api](https://wiki.open.qq.com/wiki/mobile/API%E8%B0%83%E7%94%A8%E8%AF%B4%E6%98%8E#1.13_.E5.88.86.E4.BA.AB.E6.B6.88.E6.81.AF.E5.88.B0QQ.EF.BC.88.E6.97.A0.E9.9C.80QQ.E7.99.BB.E5.BD.95.EF.BC.89)
* [SDK下载地址](https://wiki.open.qq.com/wiki/mobile/SDK%E4%B8%8B%E8%BD%BD)

## License
Apache License 2.0
