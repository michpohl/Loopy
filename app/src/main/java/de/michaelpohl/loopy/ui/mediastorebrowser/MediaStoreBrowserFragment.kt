package de.michaelpohl.loopy.ui.mediastorebrowser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.adapter.adapter.adapter
import com.example.adapter.adapter.customAdapter
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.find
import de.michaelpohl.loopy.common.setDivider
import de.michaelpohl.loopy.databinding.FragmentMediaListBinding
import de.michaelpohl.loopy.ui.base.BaseFragment
import de.michaelpohl.loopy.ui.mediastorebrowser.adapter.*
import org.koin.android.ext.android.inject
import timber.log.Timber

open class MediaStoreBrowserFragment : BaseFragment() {

    override val titleResource = R.string.title_mediastore_browser
    override val viewModel: MediaStoreBrowserViewModel by inject()
    private lateinit var binding: FragmentMediaListBinding
    private lateinit var recycler: RecyclerView

    private val browserAdapter = customAdapter<MediaStoreItemModel, MediaStoreBrowserViewModel.UIState> {
        delegates = listOf(ArtistDelegate(),
            AlbumDelegate { viewModel.onAlbumClicked(it) },
            TrackDelegate { model -> viewModel.onTrackSelectionChanged(model) })
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
            DataBindingUtil.inflate(inflater, R.layout.fragment_media_list, container, false)
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        recycler = binding.root.find(R.id.rv_files)
        recycler.adapter = browserAdapter
        recycler.setDivider(R.drawable.divider)
        observe()
        return binding.root
    }

    override fun onBackPressed(): Boolean {
        return if (!viewModel.onBackPressed()) super.onBackPressed() else false
    }

    private fun observe() {
        viewModel.state.observeWith {
            Timber.d("Updating: $it")
            browserAdapter.update(it)
        }
    }

    override fun getTitle(): String {
        return getString(R.string.title_file_browser)
    }

    private fun addSelectionToPlayer(models: List<FileModel.AudioFile>) {
        val arguments = bundleOf(Pair("models", models))
        Timber.d("Navigating")
        navigateTo(R.id.action_mediaStoreBrowserFragment_to_playerFragment, arguments)
    }
}

