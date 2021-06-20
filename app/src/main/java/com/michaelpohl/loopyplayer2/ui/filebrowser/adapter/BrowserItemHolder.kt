package com.michaelpohl.loopyplayer2.ui.filebrowser.adapter

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.michaelpohl.delegationadapter.DelegationAdapterItemHolder
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.common.FileModel
import com.michaelpohl.loopyplayer2.common.find

abstract class BrowserItemHolder<T : FileModel>(itemView: View) :
    DelegationAdapterItemHolder<T>(itemView) {

    val label: TextView = itemView.find(R.id.tv_label)
    val subLabel: TextView = itemView.find(R.id.tv_sublabel)
    val icon: ImageView = itemView.find(R.id.iv_icon)
    val checkBox: CheckBox = itemView.find(R.id.cb_checkbox)
}
