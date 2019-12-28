package de.michaelpohl.loopy.common

import com.squareup.moshi.Moshi

/**
 * All sub classes must be data classes!
 */
open class JsonDataClass {

    fun toJsonString(): String {
        return Moshi.Builder().build().adapter(this.javaClass).toJson(this)
    }

    companion object {
        inline fun <reified T : JsonDataClass> fromJsonString(string: String): T? {
            return Moshi.Builder().build().adapter(T::class.java).fromJson(string)
        }
    }
}
