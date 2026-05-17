package com.example.mechanichelper.data.network

import retrofit2.HttpException
import retrofit2.Response

fun <T> Response<T>.requireSuccess() {
    if (!isSuccessful) throw HttpException(this)
}
