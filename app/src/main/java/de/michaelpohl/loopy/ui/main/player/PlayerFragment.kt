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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.DialogHelper
import de.michaelpohl.loopy.common.find
import de.michaelpohl.loopy.databinding.FragmentPlayerBinding
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.model.PlayerService
import de.michaelpohl.loopy.model.PlayerServiceBinder
import de.michaelpohl.loopy.ui.main.BaseFragment
import org.koin.android.viewmodel.ext.android.getViewModel
import timber.log.Timber

class PlayerFragment : BaseFragment() {

    private lateinit var adapter: NewPlayerAdapter

    private lateinit var viewModel: PlayerViewModel
    private lateinit var binding: FragmentPlayerBinding
    private lateinit var recycler: RecyclerView

    private lateinit var loopsList: List<AudioModel>
    private lateinit var playerService: PlayerService
    lateinit var onResumeListener: (PlayerFragment) -> Unit

    private var playerServiceBinder: PlayerServiceBinder? = null
        set(value) {
            value?.let {
            field = value
            viewModel.looper = value
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        //        TODO Reinstate arguments or do it differently
        //        if (arguments != null) {
        //            val appData: AppData = requireArguments().getParcelable("appData")!!
        //            Timber.d("Loops when starting player: %s", loopsList)
        //        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false)
        viewModel = getViewModel()
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        recycler = binding.root.find(R.id.rv_loops)
        initAdapter(viewModel.loopsList)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        playerService = PlayerService()
        bindAudioService()
        requireActivity().startService(Intent(activity, playerService::class.java))
    }

    override fun onResume() {
        super.onResume()
        observe()
        Timber.d("onResume in fragment")

        // TODO change that to lose reflection here
        if (::onResumeListener.isInitialized) onResumeListener.invoke(this)

        try {
            viewModel.playerActionsListener = context as PlayerViewModel.PlayerActionsListener
        } catch (e: Exception) {
            throw Exception("${context} should implement MusicBrowserFragment.OnItemCLickListener")
        }
    }

    override fun onPause() {
        super.onPause()
        if (!DataRepository.settings.playInBackground) {
            pausePlayback()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopLooper()
        DataRepository.saveCurrentState(viewModel.loopsList)
        unBindAudioService()
    }

    fun updateViewModel() {
        //TODO this should be handled differently
        //        loopsList = DataRepository.currentSelectedAudioModels
        //        //        viewModel.loopsList = DataRepository.currentSelectedAudioModels
        //        viewModel.showEmptyState()
    }

    fun pausePlayback() {
//        playerServiceBinder?.pause()
    }

    private fun initAdapter(loopsList: List<AudioModel>) {
        adapter = NewPlayerAdapter({
            viewModel.onProgressChangedByUser(it)
        }, { viewModel.onLoopClicked(it) }).also {
            it.items = loopsList
            it.dialogHelper = DialogHelper(requireActivity()) //TODO can it be injected?

        }.apply {
            selected.observe(viewLifecycleOwner, androidx.lifecycle.Observer { viewModel.currentlySelected = it })
        }

        // todo sort recycler and adapter code blocks better
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter
        // TODO this could get handled better
        viewModel.showEmptyState(loopsList.isEmpty())
    }

    private fun observe() {
        viewModel.fileCurrentlyPlayed.observe(viewLifecycleOwner, Observer { adapter.updateFileCurrentlyPlayed(it) })
        viewModel.filePreselected.observe(viewLifecycleOwner, Observer { adapter.updateFilePreselected(it) })
        viewModel.playbackProgress.observe(viewLifecycleOwner, Observer { adapter.updatePlaybackProgress(it)})

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
            Timber.d("Does viewModel have a binder now? ${viewModel.looper != null}")
        }
    }

    private fun unBindAudioService() {
        if (playerServiceBinder != null) {
            activity?.unbindService(serviceConnection)
        }
    }
}
