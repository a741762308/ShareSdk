package com.charles.sharesdk.plugin.asm.share

import com.charles.sharesdk.plugin.Settings
import com.charles.sharesdk.plugin.Utils
import com.charles.sharesdk.plugin.asm.AbsClassVisitor
import com.charles.sharesdk.plugin.asm.AbsMethodVisitor
import com.charles.sharesdk.plugin.extension.ConfigExt
import com.charles.sharesdk.plugin.extension.ShareExt
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

import java.util.function.Function

class ShareConfigClassVisitor extends AbsClassVisitor {

    static final def className = "com/charles/sharesdk/core/ShareOptions\$Builder"

    static Factory factory = new Factory()

    ShareConfigClassVisitor(ClassVisitor classVisitor) {
        super(classVisitor)
    }

    static class Factory implements Function<ClassWriter, ClassVisitor> {

        @Override
        ClassVisitor apply(ClassWriter classWriter) {
            return new ShareConfigClassVisitor(classWriter)
        }
    }


    @Override
    MethodVisitor watch(MethodVisitor visitor, ClassInfo classInfo, MethodInfo methodInfo) {
        if (className == classInfo.name) {
            if ("initConfigByAsm" == methodInfo.name) {
                Utils.log "-------find ShareOptions\$Builder initConfigByAsm-------"
                return new ShareConfigMethodVisitor(visitor)
            }
        }
        return null
    }

    static class ShareConfigMethodVisitor extends AbsMethodVisitor {

        ShareConfigMethodVisitor(MethodVisitor methodVisitor) {
            super(methodVisitor)
        }

        @Override
        void visitCode() {
            Utils.log "-------ShareOptions\$Builder initConfigByAsm visitCode start===>"

            ShareExt.ShareConfig config = Settings.shareConfig
            if (config.qq().enable) {
                ConfigExt qq = config.qq
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitInsn(Opcodes.ICONST_1)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "qqEnable", "Z")

                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitLdcInsn(qq.appId)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "qqAppId", "Ljava/lang/String;")
            }

            if (config.wechat().enable) {
                ConfigExt wechat = config.wechat
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitInsn(Opcodes.ICONST_1)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "wechatEnable", "Z")

                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitLdcInsn(wechat.appId)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "wechatAppId", "Ljava/lang/String;")

                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitLdcInsn(wechat.appSecret)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "wechatAppSecret", "Ljava/lang/String;")

                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitInsn(wechat.onlyAuthCode ? Opcodes.ICONST_1 : Opcodes.ICONST_0)
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "wechatOnlyAuthCode", "Ljava/lang/Boolean;")
            }

            if (config.weibo().enable) {
                ConfigExt weibo = config.weibo
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitInsn(Opcodes.ICONST_1)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "weiboEnable", "Z")

                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitLdcInsn(weibo.appId)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "weiboAppId", "Ljava/lang/String;")

                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitLdcInsn(weibo.url)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "weiboRedirectUrl", "Ljava/lang/String;")
            }

            if (config.alipay().enable) {
                ConfigExt alipay = config.alipay
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitInsn(Opcodes.ICONST_1)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "alipayEnable", "Z")

                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitLdcInsn(alipay.appId)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "alipayAppId", "Ljava/lang/String;")

                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitLdcInsn(alipay.appSecret)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "alipayAppSecret", "Ljava/lang/String;")
            }

            if (config.dingtalk().enable) {
                ConfigExt dingtalk = config.dingtalk
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitInsn(Opcodes.ICONST_1)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "dingtalkEnable", "Z")

                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitLdcInsn(dingtalk.appId)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "dingtalkAppId", "Ljava/lang/String;")

                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitLdcInsn(dingtalk.appSecret)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "dingtalkAppSecret", "Ljava/lang/String;")

                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitInsn(dingtalk.onlyAuthCode ? Opcodes.ICONST_1 : Opcodes.ICONST_0)
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "dingtalkOnlyAuthCode", "Ljava/lang/Boolean;")
            }

            if (config.facebook().enable) {
                ConfigExt facebook = config.facebook
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitInsn(Opcodes.ICONST_1)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "facebookEnable", "Z")

                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitLdcInsn(facebook.appId)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "facebookAppId", "Ljava/lang/String;")

                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitLdcInsn(facebook.appSecret)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "facebookAppSecret", "Ljava/lang/String;")

                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitLdcInsn(facebook.url)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "facebookRedirectUrl", "Ljava/lang/String;")
            }

            if (config.twitter().enable) {
                ConfigExt twitter = config.twitter
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitInsn(Opcodes.ICONST_1)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "twitterEnable", "Z")

                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitLdcInsn(twitter.appId)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "twitterAppId", "Ljava/lang/String;")

                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitLdcInsn(twitter.appSecret)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "twitterAppSecret", "Ljava/lang/String;")

                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitLdcInsn(twitter.url)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "twitterRedirectUrl", "Ljava/lang/String;")
            }

            if (config.whatsapp().enable) {
                ConfigExt whatsapp = config.whatsapp
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitInsn(Opcodes.ICONST_1)
                mv.visitFieldInsn(Opcodes.PUTFIELD, className, "whatsAppEnable", "Z")
            }

            Utils.log "-------ShareOptions\$Builder initConfigByAsm visitCode end"

            if (Settings.platformClassList != null) {
                for (String name : Settings.platformClassList) {
                    def pkgClassPath = name.replace("/", ".")
                    mv.visitVarInsn(Opcodes.ALOAD, 0)
                    mv.visitFieldInsn(Opcodes.GETFIELD, className, "factoryClassList", "Ljava/util/Set;")
                    mv.visitLdcInsn(pkgClassPath)
                    mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Set", "add", "(Ljava/lang/Object;)Z", true)
                    mv.visitInsn(Opcodes.POP)
                }
            }
            super.visitCode()
        }
    }
}
