package de.michaelpohl.loopy.ui.main

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.databinding.ItemFileBrowserBinding
import hugo.weaving.DebugLog
import kotlinx.android.synthetic.main.item_file_browser.view.*
import timber.log.Timber

@DebugLog
class FileBrowserItem(
    val context: Context,
    var binding: ItemFileBrowserBinding,
    private var filesList: List<FileModel>,
    onClickListener: ((FileModel) -> Unit)?,
    onSelectedListener: ((FileModel) -> Unit)?
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    private var onItemClickListener = onClickListener
    private var onItemSelectedListener = onSelectedListener

    init {
        binding.root.setOnClickListener(this)
        binding.root.btn_pick_folder.setOnClickListener(this)
    }

    // not sure if this is the smartest way of doing this but it works
    override fun onClick(v: View?) {

        if (v!!.id == R.id.btn_pick_folder) {
            onItemSelectedListener?.invoke(filesList[adapterPosition])
        } else {
            onItemClickListener?.invoke(filesList[adapterPosition])
        }
    }

    fun bind(model: FileBrowserItemViewModel) {
        binding.model = model
        binding.executePendingBindings()
    }


}