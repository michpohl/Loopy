package de.michaelpohl.loopy.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.FileModelsList
import de.michaelpohl.loopy.databinding.FragmentPlayerBinding
import kotlinx.android.synthetic.main.fragment_player.*

class PlayerFragment : BaseFragment() {


    //TODO loopslist needs to be persistent and gets given to the fragment when creating (which means in the activity?)
    private lateinit var loopsList: List<FileModel>
    private lateinit var viewModel: PlayerViewModel
    private lateinit var binding: FragmentPlayerBinding

    companion object {
        fun newInstance(loopFiles: List<FileModel>): PlayerFragment {
            val fragment = PlayerFragment()
            val args = Bundle()
            val loops = FileModelsList(loopFiles)
            args.putParcelable("loopsList", loops)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            val loops: FileModelsList = arguments!!.getParcelable("loopsList")
            loopsList = loops.models
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
        binding.model = viewModel
    }

    override fun onStart() {
        super.onStart()
        initAdapter()
    }

    override fun onResume() {
        super.onResume()

        //TODO find a nicer way for this
        try {
            viewModel.selectFolderListener = context as PlayerViewModel.OnSelectFolderClickedListener
        } catch (e: Exception) {
            throw Exception("${context} should implement FileBrowserFragment.OnItemCLickListener")
        }
    }

    private fun initAdapter() {
        rv_loops.layoutManager = LinearLayoutManager(context)
        rv_loops.adapter = viewModel.getAdapter()
        viewModel.loopsList = loopsList
        viewModel.updateData()

        viewModel.getAdapter().onItemClickListener = { a: FileModel, b: Int ->
            viewModel.onItemSelected(a, b)
        }
    }
}
