package de.michaelpohl.loopy.ui.main.mediastorebrowser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.adapter.adapter.adapter
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.find
import de.michaelpohl.loopy.databinding.FragmentMediastoreListBinding
import de.michaelpohl.loopy.ui.main.base.BaseFragment
import de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter.*
import org.koin.android.ext.android.inject
import timber.log.Timber

open class MediaStoreBrowserFragment : BaseFragment() {

    override val viewModel: MediaStoreBrowserViewModel by inject()
    private lateinit var binding: FragmentMediastoreListBinding
    private lateinit var recycler: RecyclerView

    private val browserAdapter = adapter<MediaStoreItemModel> {
        delegates = listOf(ArtistDelegate(),
            AlbumDelegate { viewModel.onAlbumClicked(it) },
            TrackDelegate { model, selected -> viewModel.onTrackSelectionChanged(model, selected) })
        sorting = MediaStoreBrowserSorting()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onSelectionSubmittedListener = { addSelectionToPlayer(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_mediastore_list, container, false)
        binding.model = viewModel
        recycler = binding.root.find(R.id.rv_files)
        recycler.adapter = browserAdapter
        observe()
        return binding.root
    }

    override fun onBackPressed(): Boolean {
        Timber.d("Pressing")
        return viewModel.onBackPressed()
    }

    private fun observe() {
        viewModel.entriesToDisplay.observe(
            viewLifecycleOwner,
            Observer { browserAdapter.update(it.toMutableList()) })
    }

    override fun getTitle(): String {
        return getString(R.string.appbar_title_file_browser)
    }

    private fun addSelectionToPlayer(models: List<FileModel.AudioFile>) {
        val arguments = bundleOf(Pair("models", models))
        Timber.d("Navigating")
        navigateTo(R.id.action_mediaStoreBrowserFragment_to_playerFragment, arguments)
    }
}

