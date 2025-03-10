package com.example.mechanichelper.domain

import android.net.Uri
import java.io.File

interface PhotoRepository {
    fun createImageFile(): File
    fun getLastSavedPhotoUri(): Uri?
    fun deleteLastPhoto(): Boolean
}