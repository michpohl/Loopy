package com.example.adapter.adapter.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Helper to quickly inflate views for ViewHolders (and elsewhere)
 */
fun inflateLayout(layout: Int, parent: ViewGroup, attachToRoot: Boolean? = false): View =
    LayoutInflater.from(parent.context).inflate(layout, parent, attachToRoot!!)

/**
 * Helper to quickly inflate views for ViewHolders (and elsewhere)
 * Note the standard width and height settings! change them if needed.
 */
fun <T : ViewGroup> inflateView(
    view: T,
    widthParam: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    heightParam: Int = ViewGroup.LayoutParams.WRAP_CONTENT
): T {
    view.layoutParams = ViewGroup.LayoutParams(widthParam, heightParam)
    return view
}
