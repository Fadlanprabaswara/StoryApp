package com.example.storyapp.addStory

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.exifinterface.media.ExifInterface
import java.io.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
private val File_Format = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.US)

@RequiresApi(Build.VERSION_CODES.O)
val timeStamp: String = LocalDate.now().format(File_Format)

@RequiresApi(Build.VERSION_CODES.O)
fun createCustomFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

@RequiresApi(Build.VERSION_CODES.O)
fun File(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createCustomFile(context)

    val input = contentResolver.openInputStream(selectedImg) as InputStream
    val output: OutputStream = FileOutputStream(myFile)
    val Byte = ByteArray(1024)
    var len: Int

    while (input.read(Byte).also { len = it } > 0) output.write(Byte, 0, len)
    output.close()
    input.close()

    return myFile
}

fun FileImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)

    val exif = ExifInterface(file.path)
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED
    )
    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
    }
    val rotatedBitmap =
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

    var compressQuality = 100
    var streamLength: Int
    val bitmapJpeg = Bitmap.CompressFormat.JPEG
    val outputStream = FileOutputStream(file)

    do {
        val bmpStream = ByteArrayOutputStream()
        rotatedBitmap.compress(bitmapJpeg, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > 1000000)
    rotatedBitmap.compress(bitmapJpeg, compressQuality, outputStream)

    return file
}

