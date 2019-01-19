package de.michaelpohl.loopy.ui.main.mediabrowser

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

//TODO rebuild for audioModels!

class MusicBrowserFragment : BaseFragment() {

    private lateinit var viewModel: MusicBrowserViewModel
    private lateinit var binding: FragmentFilesListBinding //TODO Why not MusicBrowserBinding?
    private lateinit var album: String

    companion object {

        fun newInstance(album: String): MusicBrowserFragment {
            val fragment = MusicBrowserFragment()
            val args = Bundle()
            args.putString("path", album)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            album = arguments!!.getString("path")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MusicBrowserViewModel::class.java)

// I can probably throw this away, MusicBrowser has only the checkbox functionality
//        try {
//            viewModel.listener = context as MusicBrowserViewModel.OnItemClickListener
//        } catch (e: Exception) {
//            throw Exception("${context} should implement MusicBrowserFragment.OnItemCLickListener")
//        }
        binding.model = viewModel
        initViews()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        viewModel.audioModels = DataRepository.getMediaStoreEntries(context!!).filter {
true            //TODO filter them by album name
        }
        viewModel.updateAdapter()
    }
}