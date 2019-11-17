package de.michaelpohl.loopy.ui.main.player

import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
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
import org.koin.android.viewmodel.ext.android.getViewModel
import timber.log.Timber

class PlayerFragmentOld : BaseFragment() {

    //TODO loopslist needs to be persistent and gets given to the fragment when creating (which means in the activity?)
    private lateinit var loopsList: List<AudioModel>
    private val viewModel : PlayerViewModel = getViewModel()
    private lateinit var binding: FragmentPlayerBinding
    private lateinit var playerService: PlayerService
    lateinit var onResumeListener: (PlayerFragmentOld) -> Unit

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

//    companion object {
//        fun newInstance(
//            appData: AppData
//        ): PlayerFragmentOld {
//            val fragment = PlayerFragmentOld()
//            val args = Bundle()
//            args.putParcelable("appData", appData)
//            fragment.arguments = args
//            return fragment
//        }
//    }

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
        playerService = PlayerService()
        bindAudioService()
        activity!!.startService(Intent(activity, playerService::class.java))
        binding.model = viewModel
    }

    override fun onStart() {
        super.onStart()
        loopsList = DataRepository.testIntegrity(listOf())
        initAdapter()
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume in fragment")

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
    }

    fun updateViewModel() {
        loopsList = DataRepository.currentSelectedAudioModels
        viewModel.loopsList = DataRepository.currentSelectedAudioModels
        viewModel.updateData()
    }

    fun pausePlayback() {
        playerServiceBinder?.pause()
    }

    private fun initAdapter() {
        rv_loops.layoutManager = LinearLayoutManager(context)
        viewModel.loopsList = loopsList
        viewModel.adapter =
            LoopsAdapter(context!!) { viewModel.onProgressChangedByUser(it) }.apply {
                dialogHelper = DialogHelper(activity!!)
                onItemSelectedListener =
                    { a: AudioModel, b: Int, c: PlayerItemViewModel.SelectionState ->
                        viewModel.onItemSelected(a, b, c)
                    }
            }

        //TODO move adapter out of viewModel (or not? what's right?)
        rv_loops.adapter = viewModel.adapter
        viewModel.updateData()
    }

    private fun bindAudioService() {
        Timber.d("bindAudioService")
        if (playerServiceBinder == null) {
            val intent = Intent(activity, PlayerService::class.java)

            // Below code will invoke serviceConnection's onServiceConnected method.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity!!.startForegroundService(intent)
            } else {
                activity!!.startService(intent)
            }
            activity!!.bindService(
                intent,
                serviceConnection,
                BIND_AUTO_CREATE
            )
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
