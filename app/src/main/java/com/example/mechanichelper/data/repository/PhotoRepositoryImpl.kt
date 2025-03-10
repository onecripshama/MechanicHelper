package com.example.mechanichelper.data.repository

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.mechanichelper.domain.PhotoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PhotoRepository {

    override fun createImageFile(): File {
        val timestamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())

        return File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "CAR_${timestamp}.jpg"
        ).apply { createNewFile() }
    }

    override fun getLastSavedPhotoUri(): Uri? {
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val files = directory?.listFiles { file ->
            file.name.startsWith("CAR_") && file.extension.equals("jpg", true)
        }?.sortedByDescending { it.lastModified() }

        return files?.firstOrNull()?.let { file ->
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        }
    }

    override fun deleteLastPhoto(): Boolean {
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val files = directory?.listFiles { file ->
            file.name.startsWith("CAR_") && file.extension.equals("jpg", true)
        }?.sortedByDescending { it.lastModified() }

        val fileToDelete = files?.firstOrNull()
        return fileToDelete?.delete() ?: false
    }
}