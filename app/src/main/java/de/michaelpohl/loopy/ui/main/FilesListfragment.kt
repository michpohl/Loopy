package de.michaelpohl.loopy.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.databinding.FragmentFilesListBinding
import hugo.weaving.DebugLog
import kotlinx.android.synthetic.main.fragment_files_list.*

@DebugLog
class FilesListFragment : BaseFragment() {

    private lateinit var viewModel: FilesListViewModel
    private lateinit var binding: FragmentFilesListBinding
    private lateinit var path: String
    private lateinit var mCallback: OnItemClickListener

    interface OnItemClickListener {
        fun onClick(fileModel: FileModel)

        fun onLongClick(fileModel: FileModel)
    }

    companion object {

        fun newInstance(path: String): FilesListFragment {
            val fragment = FilesListFragment()
            val args = Bundle()
            args.putString("path", path)
            fragment.arguments = args
            return fragment
        }


    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            mCallback = context as OnItemClickListener
        } catch (e: Exception) {
            throw Exception("${context} should implement FilesListFragment.OnItemCLickListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            path = arguments!!.getString("path");
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FilesListViewModel::class.java)
        binding.model = viewModel
        initViews()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_files_list, container, false)
        var myView: View = binding.root
        return myView
    }

    override fun onStart() {
        super.onStart()
        initViews()
    }

    private fun initViews() {
        filesRecyclerView.layoutManager = LinearLayoutManager(context)
        filesRecyclerView.adapter = viewModel.getAdapter()
        viewModel.path = path
        viewModel.updateData()

        viewModel.getAdapter().onItemClickListener = {
            mCallback.onClick(it)
        }

        viewModel.getAdapter().onItemLongClickListener = {
            mCallback.onLongClick(it)
        }
    }


}