package com.charles.sharesdk.plugin.asm


import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

abstract class AbsMethodVisitor extends MethodVisitor {

    AbsMethodVisitor(MethodVisitor methodVisitor) {
        this(Opcodes.ASM4, methodVisitor)
    }

    AbsMethodVisitor(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor)
    }
}
