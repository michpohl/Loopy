package com.michaelpohl.loopyplayer2.ui.filebrowser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.michaelpohl.delegationadapter.customAdapter
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.common.FileModel
import com.michaelpohl.loopyplayer2.common.find
import com.michaelpohl.loopyplayer2.common.setDivider
import com.michaelpohl.loopyplayer2.databinding.FragmentFilesListBinding
import com.michaelpohl.loopyplayer2.ui.base.BaseFragment
import com.michaelpohl.loopyplayer2.ui.filebrowser.adapter.AudioItemDelegate
import com.michaelpohl.loopyplayer2.ui.filebrowser.adapter.FileBrowserSorting
import com.michaelpohl.loopyplayer2.ui.filebrowser.adapter.FileItemDelegate
import com.michaelpohl.loopyplayer2.ui.filebrowser.adapter.FolderItemDelegate
import org.koin.android.ext.android.inject
import timber.log.Timber

open class FileBrowserFragment : BaseFragment() {

    override val titleResource = R.string.title_file_browser
    override val viewModel: FileBrowserViewModel by inject()
    private lateinit var binding: FragmentFilesListBinding
    private lateinit var recycler: RecyclerView

    private val browserAdapter = customAdapter<FileModel, FileBrowserViewModel.UIState> {
        delegates = listOf(
            FileItemDelegate(),
            FolderItemDelegate { viewModel.onFolderClicked(it) },
            AudioItemDelegate { model ->
                viewModel.onFileSelectionChanged(model)
            })
        sorting = FileBrowserSorting()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            requireArguments().getString("string")?.let {
                viewModel.initialPath = it
            } ?: error("No path provided to FileBrowser. This is an error!")
        }
        viewModel.onSelectionSubmittedListener = { addSelectionToPlayer(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_files_list, container, false)
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        recycler = binding.root.find<RecyclerView>(R.id.rv_files).apply {
            adapter = browserAdapter
            setDivider(R.drawable.divider)

        }
        observe()
        return binding.root
    }

    override fun getTitle(): String {
        return getString(R.string.title_file_browser)
    }

    override fun onBackPressed(): Boolean {
        return viewModel.onBackPressed()
    }

    private fun observe() {
        viewModel.state.observeWith {
            Timber.d("State: $it")
            browserAdapter.update(it)
        }
    }

    private fun addSelectionToPlayer(models: List<FileModel.AudioFile>) {
        val arguments = bundleOf(Pair("models", models)) //TODO move string to val
        navigateTo(R.id.action_fileBrowserFragment_to_playerFragment, arguments)
    }
}

