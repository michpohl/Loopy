package com.michaelpohl.loopyplayer2.ui.mediastorebrowser.adapter

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.michaelpohl.delegationadapter.DelegationAdapterItemHolder
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.common.find

abstract class MediaStoreItemHolder<T : MediaStoreItemModel>(itemView: View) :

    DelegationAdapterItemHolder<T>(itemView) {

    val label: TextView = itemView.find(R.id.tv_label)
    val subLabel: TextView = itemView.find(R.id.tv_sublabel)
    val icon: ImageView = itemView.find(R.id.iv_icon)
    val checkBox: CheckBox = itemView.find(R.id.cb_checkbox)
}
