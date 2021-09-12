package com.michaelpohl.loopyplayer2.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.michaelpohl.loopyplayer2.MainActivity
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.common.DialogHelper
import com.michaelpohl.loopyplayer2.common.find
import com.michaelpohl.loopyplayer2.databinding.FragmentPlayerBinding
import com.michaelpohl.loopyplayer2.ui.base.BaseFragment
import com.michaelpohl.loopyplayer2.ui.player.adapter.PlayerDelegationAdapter
import com.michaelpohl.loopyplayer2.ui.player.adapter.PlayerItemDelegate
import com.michaelpohl.service.PlayerServiceConnection
import com.michaelpohl.shared.FileModel
import org.koin.android.ext.android.inject
import timber.log.Timber

class PlayerFragment : BaseFragment() {

    override val showOptionsMenu = true

    override val viewModel: PlayerViewModel by inject()

    private lateinit var adapter: PlayerDelegationAdapter
    private lateinit var binding: FragmentPlayerBinding
    private lateinit var recycler: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false)
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        recycler = binding.root.find(R.id.rv_loops)
        initAdapter()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        findNavController().popBackStack(R.id.playerFragment, false)
        observe()
        viewModel.onFragmentResumed()
        if (arguments != null) {
            Timber.d("We have arguments")
            handleArguments()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopLooper()
    }

    override fun onBackPressed(): Boolean {

        /*
        TODO FIXME this is a quickfix for the fact that when the player presses back
         while conversion is in progress, the process does not get canceled properly.
         This can clearly lead to ANRs so it needs to be changed
        */
        if (viewModel.state.value?.processingOverlayVisibility == View.VISIBLE) return true

        (requireActivity() as MainActivity).finishAffinity()
        return true
    }

    private fun handleArguments() {
        val newAudioFiles = requireArguments().getParcelableArrayList<FileModel>("models")!!
        viewModel.addNewLoops(
            newAudioFiles.filterIsInstance<FileModel.AudioFile>()
        )
        arguments = null
    }

    private fun initAdapter() {

        adapter = PlayerDelegationAdapter(
            PlayerItemDelegate(
                clickReceiver = { viewModel.onLoopClicked(it) },
                deleteReceiver = { viewModel.onDeleteLoopClicked(it) })
        ).also {
            recycler.adapter = it
        }
    }

    private fun observe() {

        // FIXME adapter needs to take nullable values
        viewModel.state.observeWith {
            adapter.update(it.loopsList)
            it.fileInFocus?.let { file ->
                adapter.updateFileCurrentlyPlayed(file)
            }
            it.filePreselected?.let { file ->
                adapter.updateFilePreselected(file)
            }
            it.playbackProgress?.let { progress ->
                adapter.updatePlaybackProgress(progress, it.settings.showLoopCount)
            }
            viewModel.setPlayerWaitMode(it.settings.isWaitMode)
        }
    }

    fun clearLoops() {
        val dialogHelper = DialogHelper(requireActivity())
        dialogHelper.requestConfirmation(
            getString(R.string.dialog_clear_list_header),
            getString(R.string.dialog_clear_list_content)
        ) {
            viewModel.clearLoops()
        }
    }
}
