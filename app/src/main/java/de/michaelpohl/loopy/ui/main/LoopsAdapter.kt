package de.michaelpohl.loopy.ui.main

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import kotlinx.android.synthetic.main.item_file_browser.view.*
class LoopsAdapter(context: Context) : RecyclerView.Adapter<LoopsAdapter.ViewHolder>() {

    var loopsList = listOf<FileModel>()
    var onItemClickListener: ((FileModel, Int ) -> Unit)? = null
    var selectedPosition = -1
    var context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoopsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loop, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = loopsList.size

    override fun onBindViewHolder(holder: LoopsAdapter.ViewHolder, position: Int) {
        holder.bindView(position)
        if (holder.adapterPosition == selectedPosition) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.action))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.window_background))

        }
    }

    fun updateData(loopsList: List<FileModel>) {
        this.loopsList = loopsList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener,
        View.OnLongClickListener {

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            onItemClickListener?.invoke(loopsList[adapterPosition], adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
//            no longClicklistener needed at this point
//            onItemSelectedListener?.invoke(filesList[adapterPosition])
            return true
        }

        fun bindView(position: Int) {
            val fileModel = loopsList[position]
            itemView.nameTextView.text = fileModel.name
        }
    }
}

