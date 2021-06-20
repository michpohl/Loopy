package com.michaelpohl.loopyplayer2.ui.licenses

import android.view.View
import android.widget.TextView
import com.michaelpohl.delegationadapter.DelegationAdapterItemHolder
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.common.find
import com.michaelpohl.loopyplayer2.common.getString

class LicenseViewHolder(itemView: View) : DelegationAdapterItemHolder<Libraries.Library>(itemView) {

    private var name: TextView = itemView.find(R.id.tv_name)
    private var statement: TextView = itemView.find(R.id.tv_copyright_statement)
    private var license: TextView = itemView.find(R.id.tv_license)
    override fun bind(item: Libraries.Library) {
        name.text = item.libraryName
        item.artifactId?.name?.let {
            item.copyrightStatement?.let {
                statement.text = it
            }
            item.license?.let {
                license.text = getString(R.string.library_License, it)
            }
        }
    }
}
