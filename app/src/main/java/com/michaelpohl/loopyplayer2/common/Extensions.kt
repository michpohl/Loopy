package com.michaelpohl.loopyplayer2.common

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.michaelpohl.loopyplayer2.model.AppStateRepository
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
 * Returns a RecyclerView's ViewHolders as a list if they are of the specified type.
 * ViewHolders of different types are skipped.
 */
inline fun <reified T : RecyclerView.ViewHolder> RecyclerView.getViewHoldersOfType(): List<T> {
    val holders = mutableListOf<T>()
    this.children.forEach {
        val holder = this.getChildViewHolder(it)
        if (holder is T) {
            holders.add(holder)
        }
    }
    return holders
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

fun View.getScreenWidth(): Int {
    return context.resources.displayMetrics.widthPixels
}

fun View.getColor(resource: Int): Int {
    return ContextCompat.getColor(context, resource)
}

/**
 * Gets a drawable from a resource and executes the ContextCompat call internally.
 */
fun View.getDrawableFromResource(resource: Int): Drawable? {
    return ContextCompat.getDrawable(context, resource)
}

/**
 * Gets a drawable from a resource and executes the ContextCompat call internally.
 * In addition, the drawable gets tinted in the color passed to the method.
 */
fun View.getColoredDrawable(resource: Int, tintColor: Int): Drawable? {
    val drawable = getDrawableFromResource(resource)
    drawable?.setColorFilter(getColor(tintColor), PorterDuff.Mode.SRC_IN)
    return drawable
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

fun View.setWidth(newWidth: Float) {
    val params = this.layoutParams
    params.width = Math.round(newWidth)
    this.layoutParams = params
}

fun View.setWidth(newWidth: Int) {
    val params = this.layoutParams
    params.width = newWidth
    this.layoutParams = params
}

fun View.setWidthInDp(newDpWidth: Float) {
    setWidth(dpToPx(newDpWidth))
}

fun View.dpToPx(dp: Float): Float {
    return dp * resources.displayMetrics.density
}

fun View.pxToDp(pixelValue: Int): Float {
    return pixelValue.toFloat() / resources.displayMetrics.density
}

fun View.animateAlpha(
    startAlpha: Float = this.alpha,
    endAlpha: Float,
    duration: Long = 100
) {
    val animator = ValueAnimator.ofFloat(startAlpha, endAlpha)
    animator.addUpdateListener { animation ->
        val value = animation.animatedValue as Float
        this.alpha = value
    }
    animator.duration = duration
    animator.start()
}

fun View.animateBackgroundColor(oldColor: Int, newColor: Int, duration: Long? = null) {
    val colorAnimator =
        ObjectAnimator.ofObject(this, "backgroundColor", ArgbEvaluator(), oldColor, newColor)
    colorAnimator.target = this
    colorAnimator.duration = duration ?: 1000L
    colorAnimator.start()
}

fun View.getResInt(resource: Int): Int {
    return this.context.resources.getInteger(resource)
}

/**
 * Returns if a view is currently displayed on the device's screen
 */
fun View.isCurrentlyVisible(): Boolean {
    if (!this.isShown) {
        return false
    }
    val scrollBounds = Rect()
    return this.getGlobalVisibleRect(scrollBounds) && height == scrollBounds.height() && width == scrollBounds.width()
}

/*************************************
 * Extension functions for ViewGroups*
 *************************************/

/**
 * Animates a [ViewGroup]'s height change.
 * @param startHeight the starting height (default value is this view's height)
 * @param endHeight the desired end height of the view. No default value!
 * @param duration the duration of the animation. Default value is 100ms
 */
fun ViewGroup.animateHeightChange(
    startHeight: Int = this.height,
    endHeight: Int,
    duration: Long = 100
) {
    val animator = ValueAnimator.ofInt(startHeight, endHeight)
    animator.addUpdateListener { animation ->
        val value = animation.animatedValue as Int
        val params = this.layoutParams as ViewGroup.LayoutParams
        params.height = value
        this.layoutParams = params
    }
    animator.duration = duration
    animator.start()
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

fun TextView.underline() {
    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

fun Long.convertFileSizeToMB(): Double {
    return (this.toDouble()) / (1024 * 1024)
}

fun String.isForbiddenFolderName(): Boolean {
    return AppStateRepository.Companion.ForbiddenFolder.values().any { it.folderName == this }
}

fun List<File>.toFileModels(types: Set<AppStateRepository.Companion.AudioFileType>? = null): List<FileModel> {
    return this.map { it.toFileModel(types) }.filter { it !is FileModel.File }
}

fun File.toFileModel(allowedTypes: Set<AppStateRepository.Companion.AudioFileType>? = null): FileModel {
    // we're only interested in files suitable for the app so we filter the rest out
    val subFiles = (this.listFiles() ?: arrayOf<File>()).filter { it.isFile }.filter { it.isAcceptedAudioType(allowedTypes) }
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
    val extension = this.extension
    types?.forEach {
        if (it.suffix == extension) return true
    }
    return false
}

fun String.hasAcceptedAudioFileExtension(types: Set<AppStateRepository.Companion.AudioFileType>): Boolean {
    val extension = this.substringAfterLast(".")
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
