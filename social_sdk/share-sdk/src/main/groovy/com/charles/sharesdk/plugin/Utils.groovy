package com.charles.sharesdk.plugin

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.function.Function

class Utils {
    static def log(msg) {
        log(true, msg)
    }

    static def log(boolean enable, String msg) {
        if (enable) {
            println(msg)
        }
    }

    static boolean isNotAttentionClass(String name) {
        return name.startsWith("R\$") || "R.class" == name || "BuildConfig.class" == name || !name.endsWith(".class")
    }

    static byte[] visitCLass(byte[] bytes, Function<ClassWriter, ClassVisitor> mapper) {
        def classReader = new ClassReader(bytes)
        def classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        classReader.accept(mapper.apply(classWriter), ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }

    static boolean isSubClass(String superName, String[] interfaces, String target) {
        if (superName == target) {
            return true
        }
        for (String anInterface : interfaces) {
            if (anInterface == target) {
                return true
            }
        }
        return false
    }
}