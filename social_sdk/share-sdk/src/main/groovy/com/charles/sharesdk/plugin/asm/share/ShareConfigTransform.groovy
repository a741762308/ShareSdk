package com.charles.sharesdk.plugin.asm.share

import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.charles.sharesdk.plugin.Utils
import com.charles.sharesdk.plugin.asm.AbsTransform
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.function.Function

class ShareConfigTransform extends AbsTransform {
    @Override
    protected boolean isAttentionFile(String name) {
        return super.isAttentionFile(name) && "${ShareConfigClassVisitor.className}.class" == name
    }

    @Override
    protected Function<ClassWriter, ClassVisitor> onEachFileClassFile(String name) {
        return ShareConfigClassVisitor.factory
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
       Utils.log "####################################################################\n" +
               "###                                                              ###\n" +
               "###                                                              ###\n" +
               "###                   欢迎使用 ShareSdk编译插件                     ###\n" +
               "###                         GitHub 地址：                         ###\n" +
               "###           https://github.com/a741762308/ShareSdk             ###\n" +
               "###                                                              ###\n" +
               "###                                                              ###\n" +
               "####################################################################"
        super.transform(transformInvocation)
    }
}
