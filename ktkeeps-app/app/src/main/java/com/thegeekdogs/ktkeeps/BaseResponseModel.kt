package com.thegeekdogs.ktkeeps

import com.squareup.moshi.JsonClass

/**
 * @author Sahib Singh
 * @since 19/02/21
 **/

@JsonClass(generateAdapter = true)
data class BaseResponseModel<T>(
    val success: Boolean,
    val data: T?,
    val error: String?
)