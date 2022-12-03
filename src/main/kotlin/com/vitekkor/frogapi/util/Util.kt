package com.vitekkor.frogapi.util

import java.util.Base64

fun String.encodeBase64(): String = Base64.getEncoder().encodeToString(this.toByteArray())

fun String.decodeBase64(): String = Base64.getDecoder().decode(this).decodeToString()
