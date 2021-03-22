package com.charles.sharesdk.plugin.asm.lifecycle

import com.charles.sharesdk.plugin.asm.AbsTransform
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.function.Function

class LifeCycleTransform extends AbsTransform {
    @Override
    protected boolean isAttentionFile(String name) {
        return super.isAttentionFile(name) && "androidx/fragment/app/FragmentActivity.class" == name
    }

    @Override
    protected Function<ClassWriter, ClassVisitor> onEachFileClassFile(String name) {
        return LifeCycleClassVisitor.factory
    }

}
