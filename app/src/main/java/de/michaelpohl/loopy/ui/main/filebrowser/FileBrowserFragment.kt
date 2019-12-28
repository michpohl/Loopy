package de.michaelpohl.loopy.ui.main.filebrowser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.databinding.FragmentFilesListBinding
import de.michaelpohl.loopy.ui.main.BaseFragment
import kotlinx.android.synthetic.main.fragment_files_list.*
import org.koin.android.ext.android.inject

class FileBrowserFragment : BaseFragment() {

    val viewModel: FileBrowserViewModel by inject()
    private lateinit var binding: FragmentFilesListBinding
    private lateinit var path: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            path = arguments!!.getString("string")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        try {
            viewModel.listener = context as BrowserViewModel.OnBrowserActionListener
        } catch (e: Exception) {
            throw Exception("${context} should implement MusicBrowserFragment.OnItemCLickListener")
        }
        binding.model = viewModel
        initViews()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_files_list, container, false)
        var myView: View = binding.root
        return myView
    }

    override fun onStart() {
        super.onStart()
        initViews()
    }

    override fun getTitle(): String {
        return getString(R.string.appbar_title_file_browser)
    }

    private fun initViews() {
        rv_files.layoutManager = LinearLayoutManager(context)
        rv_files.adapter = viewModel.getAdapter()
        viewModel.path = path
        viewModel.updateAdapter()
    }
}