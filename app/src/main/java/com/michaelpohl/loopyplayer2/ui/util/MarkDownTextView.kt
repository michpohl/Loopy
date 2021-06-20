package com.michaelpohl.loopyplayer2.ui.util

import android.content.Context
import android.util.AttributeSet
import ru.noties.markwon.Markwon

class MarkDownTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    fun setMarkdownText(text: String?) {
        text?.let {
            Markwon.setMarkdown(this, text?.let { it } ?: "Sorry, the source file was not found.")
        }
    }
}
