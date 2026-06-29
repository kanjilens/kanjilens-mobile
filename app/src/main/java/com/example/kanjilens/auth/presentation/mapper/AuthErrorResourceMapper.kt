package com.example.kanjilens.auth.presentation.mapper

import com.example.kanjilens.R
import com.example.kanjilens.auth.domain.AuthError

object AuthErrorResourceMapper {

    fun map(error: AuthError): Int {
        return when (error) {

            // Cadastro
            AuthError.EmailAlreadyInUse ->
                R.string.error_email_already_in_use

            AuthError.WeakPassword ->
                R.string.error_weak_password

            // Login
            AuthError.WrongPassword ->
                R.string.error_wrong_password

            AuthError.UserNotFound ->
                R.string.error_user_not_found

            AuthError.InvalidCredentials ->
                R.string.error_invalid_credentials

            // E-mail
            AuthError.InvalidEmail ->
                R.string.error_invalid_email

            AuthError.MissingEmail ->
                R.string.error_missing_email

            // Senha
            AuthError.MissingPassword ->
                R.string.error_missing_password

            // Conta
            AuthError.UserDisabled ->
                R.string.error_user_disabled

            AuthError.RequiresRecentLogin ->
                R.string.error_requires_recent_login

            // Sessão
            AuthError.UserTokenExpired ->
                R.string.error_user_token_expired

            AuthError.InvalidUserToken ->
                R.string.error_invalid_user_token

            // Rede
            AuthError.NetworkError ->
                R.string.error_network

            // Configuração
            AuthError.OperationNotAllowed ->
                R.string.error_operation_not_allowed

            // Limite
            AuthError.TooManyRequests ->
                R.string.error_too_many_requests

            // Validação local da senha
            AuthError.PasswordTooShort ->
                R.string.error_password_too_short

            AuthError.PasswordMissingUppercase ->
                R.string.error_password_missing_uppercase

            AuthError.PasswordMissingNumber ->
                R.string.error_password_missing_number

            AuthError.PasswordMissingSpecialCharacter ->
                R.string.error_password_missing_special_character

            // Genérico
            AuthError.Unknown ->
                R.string.error_unknown
        }
    }
}