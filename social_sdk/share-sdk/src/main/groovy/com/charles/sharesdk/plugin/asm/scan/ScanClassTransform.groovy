package com.charles.sharesdk.plugin.asm.scan

import com.charles.sharesdk.plugin.Settings
import com.charles.sharesdk.plugin.Utils
import com.charles.sharesdk.plugin.asm.AbsClassVisitor
import com.charles.sharesdk.plugin.asm.AbsTransform
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor

import java.util.function.Function

class ScanClassTransform extends AbsTransform {
    private Factory classVisitorFactory

    ScanClassTransform() {
        Settings.platformClassList = new HashSet<String>()
        classVisitorFactory = new Factory()
    }

    @Override
    protected boolean isAttentionFile(String name) {
        return super.isAttentionFile(name) && name.startsWith("com/charles/sharesdk/core/platform")
    }

    @Override
    protected boolean isShowLog() {
        return false
    }

    @Override
    protected Function<ClassWriter, ClassVisitor> onEachFileClassFile(String name) {
        return classVisitorFactory
    }

    static class Factory implements Function<ClassWriter, ClassVisitor> {

        @Override
        ClassVisitor apply(ClassWriter classWriter) {
            return new ScanClassVisitor(classWriter)
        }
    }

    static class ScanClassVisitor extends AbsClassVisitor {

        ScanClassVisitor(ClassVisitor classVisitor) {
            super(classVisitor)
        }

        @Override
        MethodVisitor watch(MethodVisitor methodVisitor, ClassInfo classInfo, MethodInfo methodInfo) {
            if (Utils.isSubClass(classInfo.superName,
                    classInfo.interfaces,
                    "com/charles/sharesdk/core/platform/IShareFactory")) {
                Settings.platformClassList.add(classInfo.name)
            }
            return null
        }
    }
}
