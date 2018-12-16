package de.michaelpohl.loopy.ui.main

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileHelper
import de.michaelpohl.loopy.common.FileModel
import kotlinx.android.synthetic.main.item_loop.view.*
import rm.com.audiowave.AudioWaveView
import timber.log.Timber
import java.util.*

class LoopsAdapter(context: Context) : RecyclerView.Adapter<LoopsAdapter.ViewHolder>() {


    var loopsList = listOf<FileModel>()
    var onItemClickListener: ((FileModel, Int) -> Unit)? = null
    var onProgressUpdatedListener: ((Float) -> Unit)? = null

    var selectedPosition = -1
    var context = context

    //hey kotlin, this is really not sexy
//    val progressListener = object : OnProgressChangedListener {
//        override fun update(position: Float) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//    }

    class Test {
        var onAction = fun(x: Int, y: Int): Int = null!!
        fun doAction() {
            onAction(1, 2)
        }
    }

    class Test2{
        fun testFun(){
            var test = Test()
            test.onAction = fun(x, y): Int {
                return x + y
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoopsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loop, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = loopsList.size

    override fun onBindViewHolder(holder: LoopsAdapter.ViewHolder, position: Int) {
        holder.bindView(position)
        if (holder.adapterPosition == selectedPosition) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.action))
            holder.selected = true
            holder.initializeOnProgressUpdatedListener()
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.window_background))
        }

    }

    fun updateData(loopsList: List<FileModel>) {
        this.loopsList = loopsList
        notifyDataSetChanged()
    }

    fun updateProgress(position: Float) {
        onProgressUpdatedListener?.invoke(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener,
        View.OnLongClickListener, OnProgressChangedListener {

        var selected = false


        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun update(position: Float) {
            Timber.d("progess update: %s", position)
            if (!selected) return
            itemView.wave.progress = position
        }

        fun initializeOnProgressUpdatedListener() {
            onProgressUpdatedListener = {
                it -> update(it)
            }
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
            val bytes = FileHelper.getSingleFile(fileModel.path).readBytes()
            itemView.tv_name.text = fileModel.name
            inflateWave(itemView.wave, bytes)
        }

        private fun inflateWave(view: AudioWaveView, bytes: ByteArray) {
            view.setRawData(bytes)
        }
    }

    interface OnProgressChangedListener {
        fun update(position: Float) : Unit
    }
}

