package com.charles.sharesdk.plugin

import com.android.build.gradle.AppPlugin
import com.charles.sharesdk.plugin.asm.scan.ScanClassTransform
import com.charles.sharesdk.plugin.asm.share.ShareConfigTransform
import com.charles.sharesdk.plugin.extension.ShareExt
import org.gradle.api.Plugin
import org.gradle.api.Project


class ShareSdkPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        log "------------------------------------"
        log "-----------ShareSdkPlugin-----------"
        def hasApp = project.plugins.withType(AppPlugin)
        if (!hasApp) {
            throw new IllegalStateException("'android application' plugin required.")
        }
        project.extensions.create("shareSdk", ShareExt.class)
        final def buildType = isReleaseType(project)
        ShareExt ext = project.shareSdk
        project.android.applicationVariants.all { variant ->
            ShareExt.ShareConfig config
            if (buildType) {
                config = ext.release
                log "current compile build type release"
            } else {
                config = ext.debug
                log "current compile build type debug"
            }
            Settings.shareConfig = config
            printPushSdkConfig(config)
            addManifestPlaceholder(project, config)
            prepareDependencies(project, config)
        }

//        project.android.registerTransform(new YxLifeCycleTransform())
        project.android.registerTransform(new ScanClassTransform())
        project.android.registerTransform(new ShareConfigTransform())
    }

    private static boolean isReleaseType(Project project) {
        def sp = project.gradle.startParameter.taskNames
        log "compile environment:${sp}"
        boolean flag = false
        sp.forEach {
            if (it.contains("Release")) {
                log "find release task:${it}"
                flag = true
            }
        }
        return flag
    }

    private static void printPushSdkConfig(ShareExt.ShareConfig ext) {
        if (ext.qq != null) {
            log "config.qqEnable => ${ext.qq.enable}"
            log "config.qqAppId => ${ext.qq.appId}"
        } else {
            log "QQ not configured"
        }
        if (ext.wechat != null) {
            log "config.wechatEnable => ${ext.wechat.enable}"
            log "config.wechatAppId => ${ext.wechat.appId}"
            log "config.wechatAppSecret => ${ext.wechat.appSecret}"
            log "config.wechatonlyAuthCode => ${ext.wechat.onlyAuthCode}"
        } else {
            log "WeChat not configured"
        }
        if (ext.weibo != null) {
            log "config.weiboEnable => ${ext.weibo.enable}"
            log "config.weiboAppKey => ${ext.weibo.appId}"
            log "config.weiboRedirectUrl => ${ext.weibo.url}"
        } else {
            log "WeiBo not configured"
        }
        if (ext.alipay != null) {
            log "config.alipayEnable => ${ext.alipay.enable}"
        } else {
            log "AliPay not configured"
        }
        if (ext.dingtalk != null) {
            log "config.dingtalkEnable => ${ext.dingtalk.enable}"
            log "config.dingtalkAppId => ${ext.dingtalk.appId}"
            log "config.dingtalkOnlyAuthCode => ${ext.dingtalk.onlyAuthCode}"
        } else {
            log "DingTalk not configured"
        }
        if (ext.facebook != null) {
            log "config.facebookEnable => ${ext.facebook.enable}"
            log "config.facebookAppId => ${ext.facebook.appId}"
            log "config.facebookAppSecret => ${ext.facebook.appSecret}"
            log "config.facebookRedirectUrl => ${ext.facebook.url}"
        } else {
            log "FaceBook not configured"
        }
        if (ext.twitter != null) {
            log "config.twitterEnable => ${ext.twitter.enable}"
            log "config.twitterAppId => ${ext.twitter.appId}"
            log "config.twitterAppSecret => ${ext.twitter.appSecret}"
            log "config.twitterRedirectUrl => ${ext.twitter.url}"
        } else {
            log "Twitter not configured"
        }
        if (ext.whatsapp != null) {
            log "config.whatsappEnable => ${ext.whatsapp.enable}"
        } else {
            log "WhatsApp not configured"
        }
    }

    private static void addManifestPlaceholder(Project project, ShareExt.ShareConfig config) {
        if (config.qq != null && config.qq.enable) {
            log "add manifestPlaceholder, QQ"
            def qq = false
            project.android.buildTypes.all { buildType ->
                qq = true
                buildType.manifestPlaceholders.QQ_APP_ID = config.qq.appId
            }
            if (!qq) {
                project.android.defaultConfig.manifestPlaceholders.QQ_APP_ID = config.qq.appId
            }
        }
        if (config.alipay != null && config.alipay.enable) {
            log "add manifestPlaceholder, AliPay"
            def alipay = false
            project.android.buildTypes.all { buildType ->
                alipay = true
                buildType.manifestPlaceholders.ALI_APP_ID = config.alipay.appId
            }
            if (!alipay) {
                project.android.defaultConfig.manifestPlaceholders.ALI_APP_ID = config.alipay.appId
            }
        }
        if (config.facebook != null && config.facebook.enable) {
            log "add manifestPlaceholder, FaceBook"
            def facebook = false
            project.android.buildTypes.all { buildType ->
                facebook = true
                buildType.manifestPlaceholders.FB_APP_ID = config.facebook.appId
            }
            if (!facebook) {
                project.android.defaultConfig.manifestPlaceholders.FB_APP_ID = config.facebook.appId
            }
        }
    }


    private static void prepareDependencies(Project pjt, ShareExt.ShareConfig ext) {
        log 'dependency append => start append'
        def defaultVersion = "0.0.1"
        pjt.dependencies {
//            implementation(project(path: ':social_sdk:shareCore'))
            implementation("com.charles.share.sdk:share-core:${defaultVersion}")
        }
        if (ext.qq != null && ext.qq.enable) {
            log "dependency append => add QQ , appId = ${ext.qq.appId} , appsercret = ${ext.qq.appSecret}"
            pjt.dependencies {
//                implementation(project(path: ':social_sdk:shareQQ'))
                implementation("com.charles.share.sdk:share-qq:${defaultVersion}")
            }
        }
        if (ext.wechat != null && ext.wechat.enable) {
            log "dependency append => add WeChat , appId = ${ext.wechat.appId} , appsercret = ${ext.wechat.appSecret} , onlyAuthCode = ${ext.wechat.onlyAuthCode}"
            pjt.dependencies {
//                implementation(project(path: ':social_sdk:shareWechat')) {
                implementation("com.charles.share.sdk:share-wechat:${defaultVersion}") {
                    transitive = true
                }
            }
        }
        if (ext.weibo != null && ext.weibo.enable) {
            log "dependency append => add SinaWeiBo , appId = ${ext.weibo.appId} , appsercret = ${ext.weibo.appSecret} , redirecturl = ${ext.weibo.url}"
            log "add WeiBo maven url"
            pjt.rootProject.buildscript.repositories {
                maven {
                    url 'https://dl.bintray.com/thelasterstar/maven/'
                }
            }
            pjt.rootProject.allprojects {
                repositories {
                    maven {
                        url 'https://dl.bintray.com/thelasterstar/maven/'
                    }
                }
            }
            pjt.dependencies {
//                implementation(project(path: ':social_sdk:shareWeibo')) {
                implementation("com.charles.share.sdk:share-weibo:${defaultVersion}") {
                    transitive = true
                }
            }
        }
        if (ext.alipay != null && ext.alipay.enable) {
            log "dependency append => add AliPay , appId = ${ext.alipay.appId} , appsercret = ${ext.alipay.appSecret}"
            pjt.dependencies {
//                implementation(project(path: ':social_sdk:shareAlipay'))
                implementation("com.charles.share.sdk:share-alipay:${defaultVersion}")
            }
        }
        if (ext.dingtalk != null && ext.dingtalk.enable) {
            log "dependency append => add DingTalk , appId = ${ext.dingtalk.appId} , appsercret = ${ext.dingtalk.appSecret}"
            pjt.dependencies {
//                implementation(project(path: ':social_sdk:shareDingtalk')) {
                implementation("com.charles.share.sdk:share-dingtalk:${defaultVersion}") {
                    transitive = true
                }
            }
        }
        if (ext.facebook != null && ext.facebook.enable) {
            log "dependency append => add FaceBook , appId = ${ext.facebook.appId} , appsercret = ${ext.facebook.appSecret} , redirecturl = ${ext.facebook.url}"
            pjt.dependencies {
//                implementation(project(path: ':social_sdk:shareFacebook')) {
                implementation("com.charles.share.sdk:share-facebook:${defaultVersion}") {
                    transitive = true
                }
            }
        }
        if (ext.twitter != null && ext.twitter.enable) {
            log "dependency append => add Twitter , appId = ${ext.twitter.appId} , appsercret = ${ext.twitter.appSecret} , redirecturl = ${ext.twitter.url}"
            pjt.dependencies {
//                implementation(project(path: ':social_sdk:shareTwitter')) {
                implementation("com.charles.share.sdk:share-twitter:${defaultVersion}") {
                    transitive = true
                }
            }
        }
        if (ext.whatsapp != null && ext.whatsapp.enable) {
            log "dependency append => add WhatsApp"
            pjt.dependencies {
//                implementation(project(path: ':social_sdk:shareWhatsapp')) {
                implementation("com.charles.share.sdk:share-whatsapp:${defaultVersion}")
            }
        }
    }

    private static void log(msg) {
        Utils.log(msg)
    }
}