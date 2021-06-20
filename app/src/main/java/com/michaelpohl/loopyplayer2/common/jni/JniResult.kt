package com.michaelpohl.loopyplayer2.common.jni

sealed class JniResult<out T> {
    abstract val data: T?
    abstract fun <T> copy(data: T): JniResult<T>
    data class Success<out T>(override val data: T?) : JniResult<T>() {
        override fun <T> copy(data: T): JniResult<T> {
            return Success<T>(data)
        }
    }

    data class Error(val code: Int? = null, val e: Exception? = null) : JniResult<Nothing>() {
        override val data = null
        override fun <T> copy(data: T): JniResult<T> = this
    }

    object JniError : JniResult<Nothing>() {
        override val data = null
        override fun <T> copy(data: T): JniError = this
    }

    /**
     * Convenience function to quickly check if this result is a [JniResult.Success]
     */
    fun isSuccess() = this is Success<*>

    /**
     * Convenience function to quickly check if this result is a [JniResult.Error]
     */
    fun isError() = this is Error
}

/**
 * Creates a [JniResult.Success] with no data payload
 */
fun successResult() = JniResult.Success(null)

/**
 * Creates a [JniResult.Success] with data payload
 */
fun <T> successResult(data: T?) = JniResult.Success(data)

/**
 * Creates a [JniResult.Error] with no further content
 */
fun errorResult() = JniResult.Error(null)
fun Boolean.toJniResult(): JniResult<Nothing> {
    return if (this) successResult() else errorResult()
}


