package de.michaelpohl.loopy.ui.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.FileType
import kotlinx.android.synthetic.main.item_recycler_file.view.*

class LoopsAdapter : RecyclerView.Adapter<LoopsAdapter.ViewHolder>(){

    var loopsList = listOf<FileModel>()
    var onItemClickListener: ((FileModel) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoopsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loop, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = loopsList.size

    override fun onBindViewHolder(holder: LoopsAdapter.ViewHolder, position: Int) = holder.bindView(position)


    fun updateData(loopsList: List<FileModel>) {
        this.loopsList = loopsList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            onItemClickListener?.invoke(loopsList[adapterPosition])
        }

        override fun onLongClick(v: View?): Boolean {
//            currentFilesList = filesList
//            onItemLongClickListener?.invoke(filesList[adapterPosition])
            return true
        }

        fun bindView(position: Int) {
            val fileModel = loopsList[position]
            itemView.nameTextView.text = fileModel.name

        }
    }
}