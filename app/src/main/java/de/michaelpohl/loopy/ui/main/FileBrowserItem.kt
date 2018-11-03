package de.michaelpohl.loopy.ui.main

import android.support.v7.widget.RecyclerView
import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import kotlinx.android.synthetic.main.item_file_browser.view.*
import timber.log.Timber

class FileBrowserItem(
    itemView: View,
    private var filesList: List<FileModel>,
    onClickListener: ((FileModel) -> Unit)?,
    onSelectedListener: ((FileModel) -> Unit)?
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private var onItemClickListener = onClickListener
    private var onItemSelectedListener = onSelectedListener
    lateinit var model: FileBrowserItemViewModel

    init {
        itemView.setOnClickListener(this)
        itemView.btn_pick_folder.setOnClickListener(this)
    }

    // not sure if this is the smartest way of doing this but it works
    override fun onClick(v: View?) {
        if (v!!.id == R.id.btn_pick_folder) {
            onItemSelectedListener?.invoke(filesList[adapterPosition])
        } else {
            onItemClickListener?.invoke(filesList[adapterPosition])
        }
    }

    fun bindView(position: Int) {
        Timber.d("binding view at position: %s", position)
        model = FileBrowserItemViewModel(filesList[position])
    }
}