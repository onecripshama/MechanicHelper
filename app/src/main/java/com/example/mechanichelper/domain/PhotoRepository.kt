package com.example.mechanichelper.domain

import android.net.Uri
import java.io.File

interface PhotoRepository {
    fun createImageFile(carId: String): File
    fun getSavedPhotoUri(carId: String): Uri?
    fun deletePhoto(carId: String): Boolean
}
