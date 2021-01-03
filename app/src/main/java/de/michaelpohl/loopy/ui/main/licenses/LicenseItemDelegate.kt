package de.michaelpohl.loopy.ui.main.licenses

import android.view.ViewGroup
import com.example.adapter.adapter.AdapterItemDelegate
import com.example.adapter.adapter.inflateLayout
import de.michaelpohl.loopy.R

class LicenseItemDelegate : AdapterItemDelegate<Libraries.Library, LicenseViewHolder>() {

    override fun createViewHolder(parent: ViewGroup): LicenseViewHolder {
return LicenseViewHolder(inflateLayout(R.layout.item_license, parent))    }

    override fun isForItemType(item: Any): Boolean {
        return item is Libraries.Library
    }
}
