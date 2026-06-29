package com.example.kanjilens.auth.data.mapper

import com.example.kanjilens.auth.domain.AuthError

object FirebaseAuthErrorMapper {

    fun map(errorCode: String): AuthError {
        return when (errorCode) {

            // Cadastro
            "ERROR_EMAIL_ALREADY_IN_USE" ->
                AuthError.EmailAlreadyInUse

            "ERROR_WEAK_PASSWORD" ->
                AuthError.WeakPassword

            // Login
            "ERROR_WRONG_PASSWORD" ->
                AuthError.WrongPassword

            "ERROR_USER_NOT_FOUND" ->
                AuthError.UserNotFound

            "ERROR_INVALID_CREDENTIAL" ->
                AuthError.InvalidCredentials

            // E-mail
            "ERROR_INVALID_EMAIL" ->
                AuthError.InvalidEmail

            "ERROR_MISSING_EMAIL" ->
                AuthError.MissingEmail

            // Senha
            "ERROR_MISSING_PASSWORD" ->
                AuthError.MissingPassword

            // Conta
            "ERROR_USER_DISABLED" ->
                AuthError.UserDisabled

            "ERROR_REQUIRES_RECENT_LOGIN" ->
                AuthError.RequiresRecentLogin

            // Sessão
            "ERROR_USER_TOKEN_EXPIRED" ->
                AuthError.UserTokenExpired

            "ERROR_INVALID_USER_TOKEN" ->
                AuthError.InvalidUserToken

            // Rede
            "ERROR_NETWORK_REQUEST_FAILED" ->
                AuthError.NetworkError

            // Configuração
            "ERROR_OPERATION_NOT_ALLOWED" ->
                AuthError.OperationNotAllowed

            // Limite
            "ERROR_TOO_MANY_REQUESTS" ->
                AuthError.TooManyRequests

            else ->
                AuthError.Unknown
        }
    }
}