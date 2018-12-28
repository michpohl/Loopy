package de.michaelpohl.loopy.ui.main.player

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.databinding.ItemLoopBindingImpl
import de.michaelpohl.loopy.ui.main.browser.FileBrowserItem
import timber.log.Timber

class LoopsAdapter(var context: Context) : RecyclerView.Adapter<PlayerItem>() {


    private var loopsList = listOf<FileModel>()
    var onItemSelectedListener: ((FileModel, Int) -> Unit)? = null
    var onItemPreSelectedListener: ((FileModel, Int) -> Unit)? = null
    var onProgressUpdatedListener: ((Float) -> Unit)? = null
    var selectedPosition = -1
    var preSelectedPosition = -1
    private var playerItems: MutableList<PlayerItem> = mutableListOf()
    private var hasPreSelection = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerItem {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemLoopBindingImpl =
            DataBindingUtil.inflate(inflater, R.layout.item_loop, parent, false)
        return PlayerItem(
            parent.context,
            binding)
    }

    override fun getItemCount() = loopsList.size

    override fun onBindViewHolder(holder: PlayerItem, position: Int) {

        val itemViewModel = PlayerItemViewModel()

        holder.bind(itemViewModel)
//        holder.bindView(position)
//        Timber.d("postion: %s, preselected: %s, selected: %s", position, preSelectedPosition, selectedPosition)
//
//        when {
//            holder.positionInList == preSelectedPosition -> {
//                Timber.d("Inflating, preselected")
//                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.preselected_item))
//                preSelectedPosition = -1
//            }
//            holder.positionInList == selectedPosition -> {
//                Timber.d("Inflating, selected")
//
//                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.action))
//                holder.selected = true
//                holder.initializeOnProgressUpdatedListener()
//            }
//
//            else -> {
//                Timber.d("Inflating, other")
//
//                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.content_background))
//                holder.selected = false
//            }
//        }
    }

    fun updateData(newList: List<FileModel>) {
        setLoopsList(newList)
        notifyDataSetChanged()
    }

    fun updateProgress(position: Float) {
        onProgressUpdatedListener?.invoke(position)
    }

    fun resetProgress() {
        onProgressUpdatedListener?.invoke(0F)
    }

    private fun setLoopsList(newList: List<FileModel>) {
        loopsList = newList.sortedWith(compareBy { it.name.toLowerCase() }).filter { it.isValidFileType() }
        Timber.d("Adapter updating with these loops: ")
        loopsList.forEach { Timber.d("%s", it) }
    }

//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener,
//        View.OnLongClickListener {
//
//        var preSelected = false
//        var selected = false
//        var positionInList = -1
//
//        init {
//            itemView.setOnClickListener(this)
//            itemView.setOnLongClickListener(this)
//        }
//
//        private fun update(progress: Float) {
//
//            if (!selected) {
//                Timber.d("not selected, position: %s", positionInList)
//                itemView.wave.progress = 0F
//                return
//            }
//            itemView.wave.progress = progress
//        }
//
//        fun initializeOnProgressUpdatedListener() {
//            onProgressUpdatedListener = { it -> update(it) }
//        }
//
//        override fun onClick(v: View?) {
//
//            Timber.d("This item's position: %s", positionInList)
//            Timber.d("Selected position: %s", selectedPosition)
//            if (positionInList != selectedPosition) hasPreSelection = true
//            // do nothing if it is already the selected item
//
//            // we change the color when in SWITCH mode to signal
//            // this is the one we're waiting for to play next
//            //also we make sure no other view looks preselected at the same time
//            if (LoopsRepository.settings.switchingLoopsBehaviour == SwitchingLoopsBehaviour.WAIT && hasPreSelection) {
////
//                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.preselected_item))
//                onItemPreSelectedListener?.invoke(loopsList[adapterPosition], positionInList)
//
//                hasPreSelection = false
//
//            } else {
//                onItemSelectedListener?.invoke(loopsList[adapterPosition], positionInList)
//            }
//        }
//
//        override fun onLongClick(v: View?): Boolean {
////            no longClicklistener needed at this point
////            onItemSelectedListener?.invoke(filesList[adapterPosition])
//            //TODO show file details on long click
//            return true
//        }
//
//
//
//
//        fun bindView(position: Int) {
//            val fileModel = loopsList[position]
//            val bytes = FileHelper.getSingleFile(fileModel.path).readBytes()
//            positionInList = position
//            itemView.tv_name.text = fileModel.name
//            inflateWave(itemView.wave, bytes)
//        }
//
//        private fun inflateWave(view: AudioWaveView, bytes: ByteArray) {
//            view.setRawData(bytes)
//        }
//    }
}

