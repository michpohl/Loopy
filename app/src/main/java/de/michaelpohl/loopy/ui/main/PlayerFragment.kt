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
import de.michaelpohl.loopy.common.FileSet
import de.michaelpohl.loopy.databinding.FragmentPlayerBinding
import kotlinx.android.synthetic.main.fragment_player.*
import timber.log.Timber

class PlayerFragment : BaseFragment() {

    private lateinit var loopsList: List<FileModel>

    companion object {

        fun newInstance(loopFiles : List<FileModel>): PlayerFragment {
            val fragment = PlayerFragment()
            val args = Bundle()
            val loops = FileSet(loopFiles)
            args.putParcelable("loopsList", loops)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: PlayerViewModel

    private lateinit var binding: FragmentPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            val loops : FileSet= arguments!!.getParcelable("loopsList")
            loopsList = loops.models}

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false)
        var myView: View = binding.root
        return myView
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

        //TODO find a nicer way for this
        try {
            viewModel.selectFolderListener = context as PlayerViewModel.OnSelectFolderClickedListener
        } catch (e: Exception) {
            throw Exception("${context} should implement FilesListFragment.OnItemCLickListener")
        }
        Timber.d("Loop selection: %s", loopsList)
    }

    private fun initAdapter() {
        rv_loops.layoutManager = LinearLayoutManager(context)
        rv_loops.adapter = viewModel.getAdapter()
        viewModel.loopsList = loopsList
        viewModel.updateData()

        viewModel.getAdapter().onItemClickListener = {a: FileModel, b: Int ->
            viewModel.onItemSelected(a, b )
        }

//        viewModel.getAdapter().onItemClickListener = {
//            mCallback.onClick(it)
//        }
//
//        viewModel.getAdapter().onItemSelectedListener = {
//            mCallback.onLongClick(it)
//        }
    }

}
