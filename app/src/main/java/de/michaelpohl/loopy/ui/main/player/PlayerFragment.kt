package de.michaelpohl.loopy.ui.main.player

import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.IBinder
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.AppData
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.DialogHelper
import de.michaelpohl.loopy.databinding.FragmentPlayerBinding
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.model.PlayerService
import de.michaelpohl.loopy.model.PlayerServiceBinder
import de.michaelpohl.loopy.ui.main.BaseFragment
import kotlinx.android.synthetic.main.fragment_player.*
import timber.log.Timber

class PlayerFragment : BaseFragment() {

    //TODO loopslist needs to be persistent and gets given to the fragment when creating (which means in the activity?)
    private lateinit var loopsList: List<AudioModel>
    private lateinit var viewModel: PlayerViewModel
    private lateinit var binding: FragmentPlayerBinding
    private lateinit var playerService : PlayerService
    lateinit var onResumeListener: (PlayerFragment) -> Unit

    ////
    private var playerServiceBinder: PlayerServiceBinder? = null
    set(value) {
        field = value
        viewModel.looper = value
    }

    // This service connection object is the bridge between activity and background service.
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            // Cast and assign background service's onBind method returned iBander object.
            Timber.d("Now setting the binder!")
            playerServiceBinder = iBinder as PlayerServiceBinder
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
        }
    }
    ////

    companion object {
        fun newInstance(
            appData: AppData
        ): PlayerFragment {
            val fragment = PlayerFragment()
            val args = Bundle()
            args.putParcelable("appData", appData)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (arguments != null) {
            val appData: AppData = arguments!!.getParcelable("appData")
            loopsList = appData.audioModels
            Timber.d("Loops when starting player: %s", loopsList)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PlayerViewModel::class.java)
        playerService = PlayerService()
        bindAudioService()
        activity!!.startService(Intent(activity, playerService::class.java))
        binding.model = viewModel
    }

    override fun onStart() {
        super.onStart()
        initAdapter()
    }

    override fun onResume() {
        super.onResume()
        //TODO can be not initialized when rotating. We'll just live with it for now
        //TODO we'll have to deal with that though!
        if (::onResumeListener.isInitialized) onResumeListener.invoke(this)
        loopsList = DataRepository.testIntegrity(loopsList)
        try {
            viewModel.playerActionsListener = context as PlayerViewModel.PlayerActionsListener
        } catch (e: Exception) {
            throw Exception("${context} should implement MusicBrowserFragment.OnItemCLickListener")
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopLooper()
        DataRepository.saveCurrentState(viewModel.loopsList)
    }

    fun updateViewModel() {
        loopsList = DataRepository.currentSelectedAudioModels
        viewModel.loopsList = DataRepository.currentSelectedAudioModels
        viewModel.updateData()
    }

    private fun initAdapter() {
        rv_loops.layoutManager = LinearLayoutManager(context)
        viewModel.loopsList = loopsList
        val adapter = viewModel.getAdapter().apply {
            dialogHelper = DialogHelper(activity!!)
            onItemSelectedListener =
                { a: AudioModel, b: Int, c: PlayerItemViewModel.SelectionState ->
                    viewModel.onItemSelected(a, b, c)
                }
        }

        rv_loops.adapter = adapter
        viewModel.updateData()
    }

    private fun bindAudioService() {
        Timber.d("bindAudioService")
        if (playerServiceBinder == null) {
            val intent = Intent(activity, PlayerService::class.java)
            // Below code will invoke serviceConnection's onServiceConnected method.
            activity!!.bindService(intent, serviceConnection, BIND_AUTO_CREATE) //TODO check if activity is always there
            viewModel.looper = playerServiceBinder
            Timber.d("Does viewModel have a binder now? ${viewModel.looper != null}")
        }

    }

    private fun unBindAudioService() {
        if (playerServiceBinder != null) {
            activity?.unbindService(serviceConnection)
        }
    }
}
