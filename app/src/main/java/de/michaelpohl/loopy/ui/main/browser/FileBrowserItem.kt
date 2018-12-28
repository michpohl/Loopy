package de.michaelpohl.loopy.ui.main.browser

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.databinding.ItemFileBrowserBinding
import hugo.weaving.DebugLog


class FileBrowserItem(
    val context: Context,
    var binding: ItemFileBrowserBinding,
    private var filesList: List<FileModel>,
    onClickListener: ((FileModel) -> Unit)?,
    onSelectedListener: ((FileModel) -> Unit)?
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {


    init {
        binding.root.setOnClickListener(this)
    }

    // not sure if this is the smartest way of doing this but it works
    //TODO also: move to viewModel
    override fun onClick(v: View?) {
//        var selectedFileModel = filesList[adapterPosition]
//        if (FileHelper.isExcludedFolderName(selectedFileModel.path)) return
//
//        if (v!!.id == R.id.btn_pick_folder) {
//            onItemSelectedListener?.invoke(selectedFileModel)
//        } else {
//            onItemClickListener?.invoke(selectedFileModel)
//        }


    }

    fun bind(model: FileBrowserItemViewModel) {
        binding.model = model
        binding.executePendingBindings()
    }
}