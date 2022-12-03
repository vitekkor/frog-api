package com.vitekkor.frogapi.model

data class SingUpModel(val name: String?, val email: String?, val password: String?) {
    constructor() : this(null, null, null)
}
