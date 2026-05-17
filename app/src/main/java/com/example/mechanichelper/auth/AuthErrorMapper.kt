package com.example.mechanichelper.auth

import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object AuthErrorMapper {

    private val gson = Gson()

    fun fromHttp(response: Response<*>, isLogin: Boolean): String {
        parseErrorBody(response.errorBody()?.string())?.let { return it }
        return httpCodeMessage(response.code(), isLogin)
    }

    fun fromThrowable(t: Throwable): String = when (t) {
        is UnknownHostException, is ConnectException ->
            "Не удалось подключиться к серверу. Проверьте интернет и что сервер запущен"
        is SocketTimeoutException ->
            "Превышено время ожидания ответа сервера"
        else -> when {
            t.message.orEmpty().contains("Failed to connect", ignoreCase = true) ->
                "Не удалось подключиться к серверу"
            t.message.orEmpty().contains("timeout", ignoreCase = true) ->
                "Превышено время ожидания"
            else -> "Ошибка сети. Проверьте подключение и попробуйте снова"
        }
    }

    private fun httpCodeMessage(code: Int, isLogin: Boolean): String = when (code) {
        400 -> if (isLogin) "Неверный формат данных" else "Проверьте введённые данные"
        401 -> "Неверный логин или пароль"
        403 -> "Доступ запрещён"
        404 -> if (isLogin) "Сервис входа недоступен" else "Сервис регистрации недоступен"
        409 -> "Пользователь с таким логином или email уже существует"
        422 -> "Данные не прошли проверку на сервере"
        in 500..599 -> "Сервер временно недоступен. Попробуйте позже"
        else -> if (isLogin) {
            "Не удалось войти (код $code). Попробуйте ещё раз"
        } else {
            "Не удалось зарегистрироваться (код $code). Попробуйте ещё раз"
        }
    }

    private fun parseErrorBody(body: String?): String? {
        val trimmed = body?.trim().orEmpty()
        if (trimmed.isEmpty()) return null

        return try {
            val json = gson.fromJson(trimmed, JsonObject::class.java)
            sequenceOf("message", "error", "detail", "title")
                .mapNotNull { key ->
                    json.get(key)?.takeIf { !it.isJsonNull }?.asString?.trim()?.takeIf { it.isNotEmpty() }
                }
                .firstOrNull()
                ?: trimmed.takeIf { it.length <= 160 && !trimmed.startsWith("<") }
        } catch (_: Exception) {
            trimmed.takeIf { it.length <= 160 && !trimmed.startsWith("<") }
        }
    }
}
