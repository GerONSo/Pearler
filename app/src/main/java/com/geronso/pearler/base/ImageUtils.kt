package com.geronso.pearler.base

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.core.net.toUri
import com.squareup.picasso.Transformation
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtils {

    private const val COMPRESS_QUALITY = 75
    private const val IMAGE_MULTIPART_TAG = "image_data"
    private const val ROTATE_90 = 90f
    private const val ROTATE_180 = 180f
    private const val ROTATE_270 = 270f

    fun compressImageToMultipart(context: Context, bitmap: Bitmap): MultipartBody.Part {
        val file = File(context.cacheDir, "image")
        file.createNewFile()
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, bos)
        val bitmapData = bos.toByteArray()
        val fos = FileOutputStream(file)
        fos.write(bitmapData)
        fos.flush()
        fos.close()
        val requestFile = RequestBody.create(
            context.contentResolver.getType(file.toUri())?.toMediaTypeOrNull(),
            file
        )
        return MultipartBody.Part.createFormData(IMAGE_MULTIPART_TAG, file.name, requestFile)
    }

    fun modifyOrientation(bitmap: Bitmap, orientation: Int): Bitmap {
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate(bitmap, ROTATE_90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate(bitmap, ROTATE_180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate(bitmap, ROTATE_270)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flip(
                bitmap = bitmap,
                horizontal = true,
                vertical = false
            )
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flip(
                bitmap = bitmap,
                horizontal = false,
                vertical = true
            )
            else -> Bitmap.createBitmap(bitmap)
        }
    }

    private fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.preScale(
            if (horizontal) -1f else 1f,
            if (vertical) -1f else 1f
        )
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}

class OrientationTransformation(
    private val uri: Uri,
    private val context: Context,
) : Transformation {

    override fun transform(source: Bitmap): Bitmap {
        var result: Bitmap
        context.contentResolver.openInputStream(uri).use { inputStream ->
            val exif = ExifInterface(inputStream!!)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            result = ImageUtils.modifyOrientation(source, orientation)
        }
        source.recycle()
        return result
    }

    override fun key(): String = uri.encodedPath ?: "path"

}