package com.charles.sharesdk.plugin.asm

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

abstract class AbsClassVisitor extends ClassVisitor implements IClassMethodVisitor {
    protected def classInfo = new ClassInfo()
    protected def methodInfo = new MethodInfo()
    protected IClassMethodVisitor methodVisitorWatcher = this

    AbsClassVisitor(ClassVisitor classVisitor) {
        this(Opcodes.ASM4, classVisitor)
    }

    AbsClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        classInfo.version = version
        classInfo.access = access
        classInfo.name = name
        classInfo.signature = signature
        classInfo.superName = superName
        classInfo.interfaces = interfaces
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        def mv = super.visitMethod(access, name, descriptor, signature, exceptions)

        methodInfo.access = access
        methodInfo.name = name
        methodInfo.descriptor = descriptor
        methodInfo.signature = signature
        methodInfo.exceptions = exceptions

        if (methodVisitorWatcher != null) {
            def watcher = methodVisitorWatcher.watch(mv, classInfo, methodInfo)
            if (watcher != null) {
                return watcher
            }
        }

        return mv
    }

    void setMethodVisitor(IClassMethodVisitor classMethodVisitor) {
        this.methodVisitorWatcher = classMethodVisitor
    }

    static class ClassInfo {
        int version
        int access
        String name
        String signature
        String superName
        String[] interfaces
    }

    static class MethodInfo {
        int access
        String name
        String descriptor
        String signature
        String[] exceptions
    }
}
