package com.example.mechanichelper.data.repository

internal fun sanitizeUserKey(login: String): String =
    login.trim().replace(Regex("[^a-zA-Z0-9._-]"), "_")
