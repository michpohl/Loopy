package de.michaelpohl.loopy.ui.main.filebrowser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adapter.adapter.inflateLayout
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.ui.main.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_files_list.*
import org.koin.android.ext.android.inject

class AlbumBrowserFragment : BaseFragment() {

    private val viewModel: AlbumBrowserViewModel by inject()
//    private lateinit var binding: FragmentFilesListBinding
    private lateinit var albums: List<String>

    companion object {

        fun newInstance(): AlbumBrowserFragment {
            return AlbumBrowserFragment()

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO examine non-null assertion!
        this.albums = DataRepository.getAlbumTitles(requireContext())

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        try {
//            viewModel.listener = context as BrowserViewModel.OnBrowserActionListener
//        } catch (e: Exception) {
//            throw Exception("${context} should implement AlbumBrowserFragment.OnItemCLickListener")
//        }
        initViews()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflateLayout(R.layout.fragment_files_list, container!!, false)
//        binding.model = viewModel
//
//        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initViews()
    }

    override fun getTitle(): String {
        return getString(R.string.appbar_title_browse_music_library)
    }

    private fun initViews() {
        rv_files.layoutManager = LinearLayoutManager(context)
        rv_files.adapter = viewModel.getAdapter()
        viewModel.albums = albums
        viewModel.updateAdapter()
    }
}
