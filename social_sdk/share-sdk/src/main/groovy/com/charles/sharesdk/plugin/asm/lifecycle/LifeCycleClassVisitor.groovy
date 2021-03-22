package com.charles.sharesdk.plugin.asm.lifecycle

import com.charles.sharesdk.plugin.asm.AbsClassVisitor
import com.charles.sharesdk.plugin.asm.AbsMethodVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

import java.util.function.Function

class LifeCycleClassVisitor extends AbsClassVisitor {

    static Factory factory = new Factory()

    LifeCycleClassVisitor(ClassVisitor classVisitor) {
        super(classVisitor)
    }

    static class Factory implements Function<ClassWriter, ClassVisitor> {

        @Override
        ClassVisitor apply(ClassWriter classWriter) {
            return new LifeCycleClassVisitor(classWriter)
        }
    }


    @Override
    MethodVisitor watch(MethodVisitor visitor, ClassInfo classInfo, MethodInfo methodInfo) {
        if ("onCreate" == methodInfo.name) {
            return new OnCreateMethodVisitor(visitor)
        } else if ("onDestroy" == methodInfo.name) {
            return new OnDestroyMethodVisitor(visitor)
        }
        return null
    }

    static class OnCreateMethodVisitor extends AbsMethodVisitor {

        OnCreateMethodVisitor(MethodVisitor methodVisitor) {
            super(methodVisitor)
        }

        @Override
        void visitCode() {
            mv.visitLdcInsn("YX_ASM_TAG")
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder")
            mv.visitInsn(Opcodes.DUP)
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)
            mv.visitLdcInsn("-------> onCreate : ")
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getSimpleName", "()Ljava/lang/String;", false)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false)
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)I", false)
            mv.visitInsn(Opcodes.POP)
            super.visitCode()
        }
    }

    static class OnDestroyMethodVisitor extends AbsMethodVisitor {

        OnDestroyMethodVisitor(MethodVisitor methodVisitor) {
            super(methodVisitor)
        }

        @Override
        void visitCode() {
            mv.visitLdcInsn("YX_ASM_TAG")
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder")
            mv.visitInsn(Opcodes.DUP)
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)
            mv.visitLdcInsn("-------> onDestroy : ")
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getSimpleName", "()Ljava/lang/String;", false)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false)
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)I", false)
            mv.visitInsn(Opcodes.POP)
            super.visitCode()
        }
    }
}
