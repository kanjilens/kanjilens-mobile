package com.example.kanjilens.auth.domain

sealed class AuthError {
    // Cadastro
    data object EmailAlreadyInUse : AuthError()
    data object WeakPassword : AuthError()

    // Login
    data object WrongPassword : AuthError()
    data object UserNotFound : AuthError()
    data object InvalidCredentials : AuthError()

    // E-mail
    data object InvalidEmail : AuthError()
    data object MissingEmail : AuthError()

    // Senha
    data object MissingPassword : AuthError()

    // Conta
    data object UserDisabled : AuthError()
    data object RequiresRecentLogin : AuthError()

    // Sessão
    data object UserTokenExpired : AuthError()
    data object InvalidUserToken : AuthError()

    // Rede
    data object NetworkError : AuthError()

    // Configuração
    data object OperationNotAllowed : AuthError()

    // Limite
    data object TooManyRequests : AuthError()

    // Genérico
    data object Unknown : AuthError()
    data object PasswordTooShort : AuthError()
    data object PasswordMissingUppercase : AuthError()
    data object PasswordMissingNumber : AuthError()
    data object PasswordMissingSpecialCharacter : AuthError()
}