package de.michaelpohl.loopy.ui.main.mediastorebrowser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.deutschebahn.streckenagent2.ui.common.recycler.AnyDiffCallback
import com.deutschebahn.streckenagent2.ui.common.recycler.DelegationAdapter
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.find
import de.michaelpohl.loopy.databinding.FragmentFilesListBinding
import de.michaelpohl.loopy.databinding.FragmentMediastoreListBinding
import de.michaelpohl.loopy.ui.main.BaseFragment
import de.michaelpohl.loopy.ui.main.filebrowser.adapter.FileBrowserSorting
import de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter.AlbumDelegate
import de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter.ArtistDelegate
import de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter.MediaStoreBrowserSorting
import de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter.TrackDelegate
import org.koin.android.ext.android.inject

open class MediaStoreBrowserFragment : BaseFragment() {

    private val viewModel: MediaStoreBrowserViewModel by inject()
    private lateinit var binding: FragmentMediastoreListBinding
    private lateinit var recycler: RecyclerView
    private var browserAdapter = DelegationAdapter(
        AnyDiffCallback(),
        ArtistDelegate(),
        AlbumDelegate(),
        TrackDelegate()
    ).also {
    it.sorting = MediaStoreBrowserSorting()
}

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

//    viewModel.onSelectionSubmittedListener = { addSelectionToPlayer(it) }
}

override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mediastore_list, container, false)
    binding.model = viewModel
    recycler = binding.root.find(R.id.rv_files)
    recycler.adapter = browserAdapter
    observe()
    return binding.root
}

private fun observe() {
    viewModel.getMediaStoreEntries().observe(
        viewLifecycleOwner,
        Observer { browserAdapter.update(it.toMutableList()) })
}

override fun getTitle(): String {
    return getString(R.string.appbar_title_file_browser)
}

private fun addSelectionToPlayer(models: List<AudioModel>) {
//        val arguments = bundleOf(Pair("models", models))
//        Timber.d("Navigating")
//        navigateTo(R.id.action_fileBrowserFragment_to_playerFragment, arguments)
}
}

