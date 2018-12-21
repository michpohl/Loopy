package de.michaelpohl.loopy.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import de.michaelpohl.loopy.MainActivity
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.databinding.FragmentFilesListBinding
import hugo.weaving.DebugLog
import kotlinx.android.synthetic.main.fragment_files_list.*

@DebugLog
class FileBrowserFragment : BaseFragment() {

    /**
    TODO The whole browser entity needs a business model of sorts
    TODO to handle collecting and updating the selections
    TODO The way it is now is a mess
     */

    private lateinit var viewModel: FileBrowserViewModel
    private lateinit var binding: FragmentFilesListBinding
    private lateinit var path: String


    companion object {

        fun newInstance(path: String): FileBrowserFragment {
            val fragment = FileBrowserFragment()
            val args = Bundle()
            args.putString("path", path)
            fragment.arguments = args
            return fragment
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            path = arguments!!.getString("path")
        }

    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FileBrowserViewModel::class.java)
        try {
            viewModel.listener = context as FileBrowserViewModel.OnItemClickListener
        } catch (e: Exception) {
            throw Exception("${context} should implement FileBrowserFragment.OnItemCLickListener")
        }
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
        rv_files.layoutManager = LinearLayoutManager(context)
        rv_files.adapter = viewModel.getAdapter()
        viewModel.path = path
        viewModel.updateData()
    }




}