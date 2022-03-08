package com.michaelpohl.loopyplayer2.common

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.michaelpohl.loopyplayer2.model.AppStateRepository
import com.michaelpohl.shared.FileModel
import timber.log.Timber
import java.io.File
import kotlin.math.pow
import kotlin.math.roundToInt

@Suppress("TooManyFunctions")

fun Boolean.toVisibility(hideInsteadGone: Boolean = false): Int {
    return if (this) View.VISIBLE else if (hideInsteadGone) View.INVISIBLE else View.GONE
}

fun <T : Any> MutableLiveData<T>.immutable(): LiveData<T> {
    return this
}

/**
 * Adds a bottom divider to a RecyclerView
 * @param dividerDrawable the Drawable to be used. If no drawable is specified, the standard divider_horizontal.xml
 * will be used.If no drawable is found at all, the function logs a warning
 */
fun RecyclerView.setDivider(dividerDrawable: Int) {
    context?.getDrawable(dividerDrawable)?.let {
        val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL).apply {
            setDrawable(it)
        }
        this.addItemDecoration(divider)
    }
        ?: Timber.w("Tried to add DividerItemDecoration to a RecyclerView, but the drawable was not found!")
}

/**
 * Convenience method for getting Strings easily inside a ViewHolder class
 * @param resourceId id of the desired string
 */
@Suppress("SpreadOperator")
fun RecyclerView.ViewHolder.getString(resourceId: Int, vararg formatArgs: Any): String {
    return this.itemView.context.resources.getString(resourceId, *formatArgs)
}

fun <T : View> View.find(id: Int): T {
    return this.findViewById(id)
}

/*************************************
 * Extension functions for Views     *
 *************************************/

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.getColor(resource: Int): Int {
    return ContextCompat.getColor(context, resource)
}

/**
 * Remember that you can only change the height if the view is already measured,
 * so this function will crash the app if you use it before measuring, since no
 * LayoutParams exist yet.
 */
fun View.setHeight(newHeight: Int) {
    val params = this.layoutParams
    params.height = newHeight
    this.layoutParams = params
}

fun View.setHeight(newHeight: Float) {
    val params = this.layoutParams
    params.height = Math.round(newHeight)
    this.layoutParams = params
}

fun View.getString(resourceId: Int): String {
    return this.resources.getString(resourceId)
}

/**
 * Convenience method for getting Drawables easily inside a View
 * @param resourceId id of the desired Drawable
 */
fun View.getDrawable(resourceId: Int): Drawable? {
    return ContextCompat.getDrawable(this.context, resourceId)
}

fun Long.convertFileSizeToMB(): Double {
    return (this.toDouble()) / (1024 * 1024)
}

fun List<File>.toFileModels(types: Set<AppStateRepository.Companion.AudioFileType>? = null): List<FileModel> {
    return this.map { it.toFileModel(types) }.filter { it !is FileModel.File }
}

fun File.toFileModel(allowedTypes: Set<AppStateRepository.Companion.AudioFileType>? = null): FileModel {
    // we're only interested in files suitable for the app so we filter the rest out
    val subFiles = (this.listFiles() ?: arrayOf<File>())
        .filter { it.isFile && it.isAcceptedAudioType(allowedTypes) }

    return when {
        this.isFolder() -> {
            FileModel.Folder(
                this.path,
                this.name,
                subFiles.size,
                subFiles.any { file -> file.isDirectory },
                subFiles.any { file ->
                    if (allowedTypes != null) file.isAcceptedAudioType(
                        allowedTypes
                    ) else true
                }
            )
        }
        this.isAcceptedAudioType(allowedTypes) -> {
            FileModel.AudioFile(
                this.path,
                this.name,
                this.length().convertFileSizeToMB(),
                this.extension,
            )
        }
        else -> {
            FileModel.File(
                this.path,
                this.name,
                this.length().convertFileSizeToMB(),
                this.extension
            )
        }
    }
}

fun File.isAcceptedAudioType(types: Set<AppStateRepository.Companion.AudioFileType>? = null): Boolean {
    val extension = this.extension.lowercase()
    types?.forEach {
        if (it.suffix == extension) return true
    }
    return false
}

fun String.hasAcceptedAudioFileExtension(types: Set<AppStateRepository.Companion.AudioFileType>): Boolean {
    val extension = this.substringAfterLast(".").lowercase()
    types.forEach {
        if (it.suffix == extension) return true
    }
    return false
}

fun RecyclerView.ViewHolder.getDrawable(resourceId: Int): Drawable? {
    return ContextCompat.getDrawable(this.itemView.context, resourceId)
}

fun Double.roundTo(numFractionDigits: Int): Double {
    val factor = 10.0.pow(numFractionDigits.toDouble())
    return (this * factor).roundToInt() / factor
}

fun File.isFolder(): Boolean {
    return this.isDirectory
}
