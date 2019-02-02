package de.michaelpohl.loopy.ui.main.player

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
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
import de.michaelpohl.loopy.ui.main.BaseFragment
import kotlinx.android.synthetic.main.fragment_player.*
import timber.log.Timber

class PlayerFragment : BaseFragment() {


    //TODO loopslist needs to be persistent and gets given to the fragment when creating (which means in the activity?)
    private lateinit var loopsList: List<AudioModel>
    private lateinit var viewModel: PlayerViewModel
    private lateinit var binding: FragmentPlayerBinding

    companion object {
        fun newInstance(appData: AppData): PlayerFragment {
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
        binding.model = viewModel
    }

    override fun onStart() {
        super.onStart()
        initAdapter()
    }

    override fun onResume() {
        super.onResume()
        loopsList = DataRepository.testIntegrity(loopsList)
        try {
            viewModel.playerActionsListener = context as PlayerViewModel.PlayerActionsListener
        } catch (e: Exception) {
            throw Exception("${context} should implement MusicBrowserFragment.OnItemCLickListener")
        }
    }

    fun updateViewModel() {
        viewModel.loopsList = DataRepository.currentSelectedAudioModels
        viewModel.updateData()
    }

    private fun initAdapter() {
        rv_loops.layoutManager = LinearLayoutManager(context)
        viewModel.loopsList = loopsList
        val adapter = viewModel.getAdapter()
        adapter.dialogHelper = DialogHelper(activity!!)  //TODO investigate assertion

        rv_loops.adapter = adapter
        viewModel.updateData()
        viewModel.getAdapter().onItemSelectedListener =
                { a: AudioModel, b: Int, c: PlayerItemViewModel.SelectionState ->
                    viewModel.onItemSelected(a, b, c)
                }
    }
}
