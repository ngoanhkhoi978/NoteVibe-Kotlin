package dev.khoi.notevibe.data.source

import android.content.Context
import android.net.Uri
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CloudinarySource(private val context: Context) {
    private val cloudinary = Cloudinary(mapOf(
        "cloud_name" to "dfuogshgb",
        "api_key" to "563363237216657",
        "api_secret" to "5uiP_MLqxqCC6HrEqcWqL055HrY"
    ))

    suspend fun uploadImage(imageUri: Uri): String {
        return withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: throw IllegalStateException("Cannot open input stream for URI: $imageUri")
            val uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap())
            println("Upload result: $uploadResult")
            uploadResult["secure_url"] as String
        }
    }
}