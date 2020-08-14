package de.michaelpohl.loopy.ui.main.filebrowser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.find
import de.michaelpohl.loopy.databinding.FragmentFilesListBinding
import de.michaelpohl.loopy.ui.main.BaseFragment
import org.koin.android.ext.android.inject

class FileBrowserFragment : BaseFragment() {

    private val viewModel: FileBrowserViewModel by inject()
    private lateinit var binding: FragmentFilesListBinding
    private lateinit var recycler: RecyclerView
    private lateinit var browserAdapter: FileBrowserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            requireArguments().getString("string")?.let {
                viewModel.path = it
                viewModel.getFolderContent()
            } ?: error("No path provided to FileBrowser. This is an error!")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_files_list, container, false)
        recycler = binding.root.find(R.id.rv_files)
        browserAdapter = FileBrowserAdapter({}, {})
        recycler.adapter = browserAdapter
        observe()
        return binding.root
    }

    private fun observe() {
        viewModel.currentFiles.observe(viewLifecycleOwner, Observer { browserAdapter.updateData(it)})
    }

    override fun getTitle(): String {
        return getString(R.string.appbar_title_file_browser)
    }
}
