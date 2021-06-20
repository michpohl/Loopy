package com.michaelpohl.loopyplayer2.common.util.coroutines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun ViewModel.uiJob(job: suspend CoroutineScope.() -> Unit): Job {
    return viewModelScope.launch(CoroutinesConfiguration.uiDispatcher) { job() }
}

fun ViewModel.ioJob(job: suspend CoroutineScope.() -> Unit): Job {
    return viewModelScope.launch(CoroutinesConfiguration.ioDispatcher) { job() }
}

fun ViewModel.backgroundJob(job: suspend CoroutineScope.() -> Unit): Job {
    return viewModelScope.launch(CoroutinesConfiguration.backgroundDispatcher) { job() }
}

suspend fun <T> withIO(codeBlock: suspend CoroutineScope.() -> T): T {
    return withContext(CoroutinesConfiguration.ioDispatcher) { codeBlock.invoke(this) }
}

suspend fun <T> withUI(codeBlock: suspend CoroutineScope.() -> T): T {
    return withContext(CoroutinesConfiguration.uiDispatcher) { codeBlock.invoke(this) }
}

suspend fun <T> withBackground(codeBlock: suspend CoroutineScope.() -> T): T {
    return withContext(CoroutinesConfiguration.backgroundDispatcher) { codeBlock.invoke(this) }
}
