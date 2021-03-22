package com.charles.sharesdk.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import com.charles.sharesdk.core.SocialSdkApi
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

/**
 *
 * @author dq on 2020/11/20.
 */
object ShareImageUtils {
    /**
     * 压缩缩略图
     *
     * @param thumbPath 本地地址或网络地址
     * @param size      最大尺寸
     * @return
     */
    fun scaleThumb(thumbPath: String, size: Long): ByteArray? {
        if (TextUtils.isEmpty(thumbPath)) {
            return null
        }
        try {
            val thumb: Bitmap?
            thumb = if (thumbPath.startsWith("http")) { //网络图片
                BitmapFactory.decodeStream(URL(thumbPath).openStream())
            } else {
                val file = File(thumbPath)
                if (!file.exists()) {
                    return null
                }
                BitmapFactory.decodeFile(file.absolutePath)
            }
            if (thumb == null) {
                return null
            }
            val baos = ByteArrayOutputStream()
            thumb.compress(Bitmap.CompressFormat.JPEG, 85, baos)
            val length = baos.size().toLong()
            return if (length < size) {
                baos.toByteArray()
            } else {
                baos.reset()
                val thumbBmp = Bitmap.createScaledBitmap(thumb, 150, 150, true)
                thumb.recycle()
                thumbBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                baos.toByteArray()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 压缩大图
     *
     * @param imagePath 本地地址或网络地址
     * @param size      最大尺寸
     * @return
     */
    fun scaleImage(imagePath: String, size: Long): ByteArray? {
        if (TextUtils.isEmpty(imagePath)) {
            return null
        }
        try {
            val imageBitmap: Bitmap = if (imagePath.startsWith("http")) { //网络图片
                BitmapFactory.decodeStream(URL(imagePath).openStream())
            } else {
                val file = File(imagePath)
                if (!file.exists()) {
                    return null
                }
                BitmapFactory.decodeFile(file.absolutePath)
            }
            return scaleImage(imageBitmap, size, 85)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 压缩图片
     *
     * @param bitmap
     * @param size
     * @return
     */
    fun scaleImage(bitmap: Bitmap?, size: Long, scaleQuality: Int): ByteArray? {
        if (bitmap == null) {
            return null
        }
        try {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, if (scaleQuality in 1..100) scaleQuality else 85, baos)
            var length = baos.size().toLong()
            if (length < size) {
                return baos.toByteArray()
            }
            var quality = 100
            while (length > size && quality > 10) {
                baos.reset()
                quality = 10.coerceAtLeast(quality - 10)
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
                length = baos.size().toLong()
            }
            return baos.toByteArray()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

    fun scaleBitmap(bitmap: Bitmap?, size: Long): Bitmap? {
        return scaleBitmap(bitmap, size, 100)
    }

    fun scaleBitmap(bitmap: Bitmap?, size: Long, scaleQuality: Int): Bitmap? {
        if (bitmap == null) {
            return null
        }
        try {
            val data = scaleImage(bitmap, size, scaleQuality)
            if (data != null) {
                return BitmapFactory.decodeByteArray(data, 0, data.size)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

    fun scaleBitmap(bitmapByte: ByteArray?, size: Long): Bitmap? {
        if (bitmapByte == null || bitmapByte.isEmpty()) {
            return null
        }
        var tempBitmap: Bitmap? = null
        try {
            tempBitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.size)
            return scaleBitmap(tempBitmap, size)
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            try {
                tempBitmap?.recycle()
            } catch (ignore: Throwable) {
            }
        }
        return null
    }

    /**
     * 压缩图片并保存到本地缓存
     *
     * @param context
     * @param imagePath
     * @param size
     * @return
     */
    fun getLocalImagePath(context: Context?, imagePath: String?, size: Long): String? {
        if (imagePath == null || imagePath.isEmpty()) {
            return null
        }
        var tempBitmap: Bitmap? = null
        try {
            tempBitmap = if (imagePath.startsWith("http")) { //网络图片
                BitmapFactory.decodeStream(URL(imagePath).openStream())
            } else {
                val file = File(imagePath)
                if (!file.exists()) {
                    return null
                }
                BitmapFactory.decodeFile(imagePath)
            }
            return getLocalImagePath(context, tempBitmap, size)
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            try {
                tempBitmap?.recycle()
            } catch (ignore: Throwable) {
            }
        }
        return null
    }

    fun getLocalImagePath(context: Context, bitmapByte: ByteArray?, size: Long): String? {
        if (bitmapByte == null || bitmapByte.isEmpty()) {
            return null
        }
        var tempBitmap: Bitmap? = null
        try {
            tempBitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.size)
            return getLocalImagePath(context, tempBitmap, size)
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            try {
                tempBitmap?.recycle()
            } catch (ignore: Throwable) {
            }
        }
        return null
    }

    fun getLocalImagePath(context: Context?, bitmap: Bitmap?, size: Long): String? {
        if (bitmap == null) {
            return null
        }
        try {
            val tempBitmap = scaleBitmap(bitmap, size)
            return getLocalImagePath(context, tempBitmap)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

    fun getLocalImagePath(context: Context?, bitmap: Bitmap?): String? {
        if (bitmap == null) {
            return null
        }
        val rootPath: String = if (context == null) {
            SocialSdkApi.get().getContext().getExternalFilesDir(null)!!.path
        } else {
            context.getExternalFilesDir(null)!!.path
        }
        var fos: FileOutputStream? = null
        try {
            val dir: File = File("${rootPath}/Images/tmp")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val tempFilePath: String = "${dir.absolutePath}/share_picture.jpg"
            val tempFile = File(tempFilePath)
            fos = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            return tempFilePath
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (ignore: Throwable) {
            }
        }
        return null
    }
}