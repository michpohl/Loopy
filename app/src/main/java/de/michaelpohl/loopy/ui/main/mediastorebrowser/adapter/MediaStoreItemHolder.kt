package de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.adapter.adapter.DelegationAdapterItemHolder
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.find

abstract class MediaStoreItemHolder<T : MediaStoreItemModel>(itemView: View) :

    DelegationAdapterItemHolder<T>(itemView) {
    val label: TextView = itemView.find(R.id.tv_label)
    val subLabel: TextView = itemView.find(R.id.tv_sublabel)
    val icon: ImageView = itemView.find(R.id.iv_icon)
    val checkBox: CheckBox = itemView.find(R.id.cb_checkbox)

}
