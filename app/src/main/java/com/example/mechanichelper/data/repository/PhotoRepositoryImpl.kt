package com.example.mechanichelper.data.repository

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.mechanichelper.domain.PhotoRepository
import com.example.mechanichelper.domain.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferencesRepository
) : PhotoRepository {

    override fun createImageFile(carId: String): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "${photoPrefix(carId)}$timestamp.jpg"
        ).apply { createNewFile() }
    }

    override fun getSavedPhotoUri(carId: String): Uri? {
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: return null
        val prefix = photoPrefix(carId)
        val files = directory.listFiles { file ->
            file.name.startsWith(prefix) && file.extension.equals("jpg", true)
        }?.sortedByDescending { it.lastModified() }

        return files?.firstOrNull()?.let { file ->
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        }
    }

    override fun deletePhoto(carId: String): Boolean {
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: return false
        val prefix = photoPrefix(carId)
        val files = directory.listFiles { file ->
            file.name.startsWith(prefix) && file.extension.equals("jpg", true)
        }?.sortedByDescending { it.lastModified() }

        return files?.firstOrNull()?.delete() ?: false
    }

    private fun photoPrefix(carId: String): String {
        val login = userPreferences.getCurrentLogin()
        return if (login != null) {
            "CAR_${sanitizeUserKey(login)}_${carId}_"
        } else {
            "CAR_${carId}_"
        }
    }
}
