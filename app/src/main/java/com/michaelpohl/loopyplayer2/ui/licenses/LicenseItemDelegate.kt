package com.michaelpohl.loopyplayer2.ui.licenses

import android.view.ViewGroup
import com.michaelpohl.delegationadapter.AdapterItemDelegate
import com.michaelpohl.delegationadapter.inflateLayout
import com.michaelpohl.loopyplayer2.R

class LicenseItemDelegate : AdapterItemDelegate<Libraries.Library, LicenseViewHolder>() {

    override fun createViewHolder(parent: ViewGroup): LicenseViewHolder {
return LicenseViewHolder(inflateLayout(R.layout.item_license, parent))    }

    override fun isForItemType(item: Any): Boolean {
        return item is Libraries.Library
    }
}
