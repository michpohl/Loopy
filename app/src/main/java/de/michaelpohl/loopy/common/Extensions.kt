package de.michaelpohl.loopy.common

import android.view.View
import androidx.core.view.children
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

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
    } ?: Timber.w("Tried to add DividerItemDecoration to a RecyclerView, but the drawable was not found!")
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
fun RecyclerView.ViewHolder.getString(resourceId: Int): String {
    return this.itemView.resources.getString(resourceId)
}

fun <T : View> View.find(id: Int): T {
    return this.findViewById(id)
}

