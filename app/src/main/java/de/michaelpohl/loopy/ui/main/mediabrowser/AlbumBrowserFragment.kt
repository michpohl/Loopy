package de.michaelpohl.loopy.ui.main.browser

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.databinding.FragmentFilesListBinding
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.ui.main.BaseFragment
import kotlinx.android.synthetic.main.fragment_files_list.*

class AlbumBrowserFragment : BaseFragment() {

    private lateinit var viewModel: AlbumBrowserViewModel
    private lateinit var binding: FragmentFilesListBinding
    private lateinit var albums: List<String>

    companion object {

        fun newInstance(): AlbumBrowserFragment {
            return AlbumBrowserFragment()

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO examine non-null assertion!
        this.albums = DataRepository.getAlbumTitles(context!!)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AlbumBrowserViewModel::class.java)
        try {
            viewModel.listener = context as AlbumBrowserViewModel.OnItemClickListener
        } catch (e: Exception) {
            throw Exception("${context} should implement AlbumBrowserFragment.OnItemCLickListener")
        }
        binding.model = viewModel
        initViews()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_files_list, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initViews()
    }

    override fun getTitle(): String {
        return getString(R.string.appbar_title_album_browser)
    }

    private fun initViews() {
        rv_files.layoutManager = LinearLayoutManager(context)
        rv_files.adapter = viewModel.getAdapter()
        viewModel.albums = albums
        viewModel.updateAdapter()
    }
}