package de.michaelpohl.loopy.ui.main.player

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.AppData
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.databinding.FragmentPlayerBinding
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.ui.main.BaseFragment
import kotlinx.android.synthetic.main.fragment_player.*
import java.lang.ref.WeakReference

class PlayerFragment : BaseFragment() {


    //TODO loopslist needs to be persistent and gets given to the fragment when creating (which means in the activity?)
    private lateinit var loopsList: List<FileModel>
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
            loopsList = appData.models
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
        viewModel.loopsList = loopsList
        viewModel.pickFileTypesListener = { showPickFileTypesDialog() }

        //handing the dropdown layout to the viewModel  as WeakReferences to avoid context leak
        //the viewModel handles showing and hiding the dropdowns
        viewModel.fileOptionsDropDown = WeakReference(ll_files_dropdown)
        viewModel.settingsDropDown = WeakReference(ll_settings_dropdown)

        binding.model = viewModel
    }

    override fun onStart() {
        super.onStart()
        initAdapter()
    }

    override fun onResume() {
        super.onResume()

        loopsList = DataRepository.testIntegrity(loopsList)

        if (changeActionBarLayoutCallBack != null) {
            changeActionBarLayout(R.menu.menu_main)
        }
        //TODO find a nicer way for this
        try {
            viewModel.playerActionsListener = context as PlayerViewModel.PlayerActionsListener
        } catch (e: Exception) {
            throw Exception("${context} should implement MusicBrowserFragment.OnItemCLickListener")
        }
    }

    override fun onBackPressed(): Boolean {
        return viewModel.closeDropDowns()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_gear -> {

            viewModel.toggleSettingsDropDown()
            true
        }

        R.id.action_browser -> {

            viewModel.toggleFilesDropDown()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }



    private fun initAdapter() {
        rv_loops.layoutManager = LinearLayoutManager(context)
        rv_loops.adapter = viewModel.getAdapter()
        viewModel.loopsList = loopsList
        viewModel.updateData()

        viewModel.getAdapter().onItemSelectedListener = { a: FileModel, b: Int, c: PlayerItemViewModel.SelectionState ->
            viewModel.onItemSelected(a, b, c)
        }
    }

    private fun showPickFileTypesDialog() {
        val dialog = PickFileTypeDialogFragment()
        dialog.setCurrentSettings(DataRepository.settings)
        dialog.resultListener = {
            DataRepository.settings = it
            DataRepository.saveCurrentState()
            viewModel.updateData()
        }

        dialog.show(fragmentManager, "pick-filetypes")
    }
}
