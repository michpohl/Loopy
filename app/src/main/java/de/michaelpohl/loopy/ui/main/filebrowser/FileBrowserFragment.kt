package de.michaelpohl.loopy.ui.main.filebrowser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.deutschebahn.streckenagent2.ui.common.recycler.AnyDiffCallback
import com.deutschebahn.streckenagent2.ui.common.recycler.DelegationAdapter
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.find
import de.michaelpohl.loopy.databinding.FragmentFilesListBinding
import de.michaelpohl.loopy.ui.main.BaseFragment
import de.michaelpohl.loopy.ui.main.player.PlayerFragment
import org.koin.android.ext.android.inject
import timber.log.Timber

class FileBrowserFragment : BaseFragment() {

    private val viewModel: FileBrowserViewModel by inject()
    private lateinit var binding: FragmentFilesListBinding
    private lateinit var recycler: RecyclerView
    private var browserAdapter = DelegationAdapter(
        AnyDiffCallback(),
        FileItemDelegate(),
        FolderItemDelegate { viewModel.onFolderClicked(it) },
        AudioItemDelegate { model, isSelected ->
            viewModel.onFileSelectionChanged(
                model,
                isSelected
            )
        }
    ).also {
        it.sorting = FileBrowserSorting()
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
        recycler = binding.root.find(R.id.rv_files)
        recycler.adapter = browserAdapter
        observe()
        return binding.root
    }

    private fun observe() {
        viewModel.currentFiles.observe(
            viewLifecycleOwner,
            Observer { browserAdapter.update(it.toMutableList()) })
    }

    override fun getTitle(): String {
        return getString(R.string.appbar_title_file_browser)
    }

    private fun addSelectionToPlayer(models: List<FileModel.AudioFile>) {
        val arguments = bundleOf(Pair("models", models))
        Timber.d("Navigating")
        navigateTo(R.id.action_fileBrowserFragment_to_playerFragment, arguments)
    }
}

