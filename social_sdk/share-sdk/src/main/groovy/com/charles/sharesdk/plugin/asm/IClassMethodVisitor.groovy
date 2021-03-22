package com.charles.sharesdk.plugin.asm

import org.objectweb.asm.MethodVisitor

interface IClassMethodVisitor {
    MethodVisitor watch(MethodVisitor visitor, AbsClassVisitor.ClassInfo classInfo, AbsClassVisitor.MethodInfo methodInfo)
}
