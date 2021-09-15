package com.michaelpohl.loopyplayer2.ui.filebrowser.adapter

import android.content.res.Resources
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.michaelpohl.delegationadapter.DelegationAdapterItemHolder
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.common.find
import com.michaelpohl.shared.FileModel
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class BrowserItemHolder<T : FileModel>(itemView: View) :
    DelegationAdapterItemHolder<T>(itemView), KoinComponent {

    val resources : Resources by inject() // TODO don't inject here, this is a quick fix. Should be solved differently

    val label: TextView = itemView.find(R.id.tv_label)
    val subLabel: TextView = itemView.find(R.id.tv_sublabel)
    val icon: ImageView = itemView.find(R.id.iv_icon)
    val checkBox: CheckBox = itemView.find(R.id.cb_checkbox)
}
