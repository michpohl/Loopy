package de.michaelpohl.loopy.ui.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.FileType
import kotlinx.android.synthetic.main.item_recycler_file.view.*

class FilesRecyclerAdapter : RecyclerView.Adapter<FilesRecyclerAdapter.ViewHolder>() {

    var onItemClickListener: ((FileModel) -> Unit)? = null
    var onItemLongClickListener: ((FileModel) -> Unit)? = null

    var filesList = listOf<FileModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_file, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = filesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bindView(position)

    fun updateData(filesList: List<FileModel>) {
        this.filesList = filesList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            onItemClickListener?.invoke(filesList[adapterPosition])
        }

        override fun onLongClick(v: View?): Boolean {
            onItemLongClickListener?.invoke(filesList[adapterPosition])
            return true
        }

        fun bindView(position: Int) {
            val fileModel = filesList[position]
            itemView.nameTextView.text = fileModel.name

            if (fileModel.fileType == FileType.FOLDER) {
                itemView.folderTextView.visibility = View.VISIBLE
                itemView.totalSizeTextView.visibility = View.GONE
                itemView.folderTextView.text = "(${fileModel.subFiles} files)"
            } else {
                itemView.folderTextView.visibility = View.GONE
                itemView.totalSizeTextView.visibility = View.VISIBLE
                itemView.totalSizeTextView.text = "${String.format("%.2f", fileModel.sizeInMB)} mb"
            }
        }
    }
}