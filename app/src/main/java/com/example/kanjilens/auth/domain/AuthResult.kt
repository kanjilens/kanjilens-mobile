package com.example.kanjilens.auth.domain

sealed class AuthResult<out T> {

    data class Success<T>(
        val data: T
    ) : AuthResult<T>()

    data class Error(
        val error: AuthError
    ) : AuthResult<Nothing>()
}