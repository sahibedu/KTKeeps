package com.thegeekdogs.auth

import io.ktor.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

val hashKey = hex(System.getenv("SECRET_KEY"))
val hMacKey = SecretKeySpec(hashKey, "HmacSHA1")

// Convert Password Strings To Hash
fun String.toHash(): String {
    val hMac = Mac.getInstance("HmacSHA1")
    hMac.init(hMacKey)
    return hex(hMac.doFinal(this.toByteArray()))
}
