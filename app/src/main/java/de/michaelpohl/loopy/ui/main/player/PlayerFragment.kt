package de.michaelpohl.loopy.ui.main.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.find
import de.michaelpohl.loopy.common.toAudioModel
import de.michaelpohl.loopy.databinding.FragmentPlayerBinding
import de.michaelpohl.loopy.model.PlayerService
import de.michaelpohl.loopy.model.PlayerServiceBinder
import de.michaelpohl.loopy.ui.main.base.BaseFragment
import de.michaelpohl.loopy.ui.main.player.adapter.PlayerDelegationAdapter
import de.michaelpohl.loopy.ui.main.player.adapter.PlayerItemDelegate
import org.koin.android.ext.android.inject
import timber.log.Timber

class PlayerFragment : BaseFragment() {

    private lateinit var adapter: PlayerDelegationAdapter

    override val viewModel: PlayerViewModel by inject()
    private lateinit var binding: FragmentPlayerBinding
    private lateinit var recycler: RecyclerView

    private lateinit var playerService: PlayerService

    override val showOptionsMenu = true

    private var playerServiceBinder: PlayerServiceBinder? = null
        set(value) {
            value?.let {
                field = value
                viewModel.setPlayer(value)
            }
        }

    // This service connection object is the bridge between activity and background service.
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            playerServiceBinder = iBinder as PlayerServiceBinder
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        playerService = PlayerService()
        bindAudioService()

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false)
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        recycler = binding.root.find(R.id.rv_loops)
        initAdapter()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().startService(Intent(activity, playerService::class.java))
    }

    override fun onResume() {
        super.onResume()
        findNavController().popBackStack(R.id.playerFragment, false)
        observe()

        if (arguments != null) {
            val newAudioFiles = requireArguments().getParcelableArrayList<FileModel>("models")
            viewModel.addNewLoops(
                newAudioFiles.filterIsInstance<FileModel.AudioFile>().map { it.toAudioModel() })

            try {
                viewModel.playerActionsListener = context as PlayerViewModel.PlayerActionsListener
            } catch (e: Exception) {
                throw Exception("${context} should implement MusicBrowserFragment.OnItemCLickListener")
            }
        }
        viewModel.onFragmentResumed()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopLooper()
        unBindAudioService()
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
            with(it) {
                adapter.update(loopsList)
                fileInFocus?.let { file ->
                    adapter.updateFileCurrentlyPlayed(file)
                }
                filePreselected?.let { file ->
                    adapter.updateFilePreselected(file)
                }
                playbackProgress?.let { progress ->
                    adapter.updatePlaybackProgress(progress, this.settings.showLoopCount)
                }
                viewModel.setPlayerWaitMode()
            }
        }

    }

    private fun bindAudioService() {
        Timber.d("bindAudioService")
        if (playerServiceBinder == null) {
            val intent = Intent(activity, PlayerService::class.java)

            // Below code will invoke serviceConnection's onServiceConnected method.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireActivity().startForegroundService(intent)
            } else {
                requireActivity().startService(intent)
            }
            requireActivity().bindService(
                intent,
                serviceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
    }

    private fun unBindAudioService() {
        if (playerServiceBinder != null) {
            activity?.unbindService(serviceConnection)
        }
    }
}
