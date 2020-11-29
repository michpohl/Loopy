package de.michaelpohl.loopy.ui.main.filebrowser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.adapter.adapter.adapter
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.find
import de.michaelpohl.loopy.common.setDivider
import de.michaelpohl.loopy.databinding.FragmentFilesListBinding
import de.michaelpohl.loopy.ui.main.base.BaseFragment
import de.michaelpohl.loopy.ui.main.filebrowser.adapter.AudioItemDelegate
import de.michaelpohl.loopy.ui.main.filebrowser.adapter.FileBrowserSorting
import de.michaelpohl.loopy.ui.main.filebrowser.adapter.FileItemDelegate
import de.michaelpohl.loopy.ui.main.filebrowser.adapter.FolderItemDelegate
import org.koin.android.ext.android.inject

open class FileBrowserFragment : BaseFragment() {

    override val viewModel: FileBrowserViewModel by inject()
    private lateinit var binding: FragmentFilesListBinding
    private lateinit var recycler: RecyclerView

    private var browserAdapter = adapter<FileModel> {
        delegates = listOf(
            FileItemDelegate(),
            FolderItemDelegate { viewModel.onFolderClicked(it) },
            AudioItemDelegate { model, isSelected ->
                viewModel.onFileSelectionChanged(
                    model,
                    isSelected
                )
            })
        sorting = FileBrowserSorting()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            requireArguments().getString("string")?.let {
                viewModel.getFolderContent(it)
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
        recycler = binding.root.find<RecyclerView>(R.id.rv_files).apply {
            adapter = browserAdapter
            setDivider(R.drawable.divider)

        }
        observe()
        return binding.root
    }

    override fun getTitle(): String {
        return getString(R.string.appbar_title_file_browser)
    }

    override fun onBackPressed(): Boolean {
        return viewModel.onBackPressed()
    }

    private fun observe() {
        viewModel.filesToDisplay.observeWith { browserAdapter.update(it.toMutableList()) }
    }

    private fun addSelectionToPlayer(models: List<FileModel.AudioFile>) {
        val arguments = bundleOf(Pair("models", models)) //TODO move string to val
        navigateTo(R.id.action_fileBrowserFragment_to_playerFragment, arguments)
    }
}

