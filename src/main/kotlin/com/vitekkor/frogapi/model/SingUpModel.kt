package com.vitekkor.frogapi.model

data class SingUpModel(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val confirmPassword: String? = null,
    val badEmail: String = "",
    val badPassword: String = ""
) {
    fun isValid(): Boolean =
        !name.isNullOrEmpty() && !email.isNullOrEmpty() && !password.isNullOrEmpty() && !confirmPassword.isNullOrEmpty()

    fun checkPassword(): Boolean = password == confirmPassword
}
