package de.michaelpohl.loopy.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.databinding.FragmentFilesListBinding
import hugo.weaving.DebugLog
import kotlinx.android.synthetic.main.fragment_files_list.*

@DebugLog
class FilesListFragment : BaseFragment() {

    private lateinit var viewModel: FilesListViewModel
    private lateinit var binding: FragmentFilesListBinding



    companion object {
        private const val ARG_PATH: String = "com.de.michaelpohl.loopy.fileslist.path"
        fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder {
        var path: String = ""

        fun build(): FilesListFragment {
            val fragment = FilesListFragment()
            val args = Bundle()
            args.putString(ARG_PATH, path)
            fragment.arguments = args;
            return fragment
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FilesListViewModel::class.java)
        binding.model = viewModel

//        val filesPath = viewModel.getFilesPath()
//        if (filesPath == null) {
//            Toast.makeText(context, "Path should not be null!", Toast.LENGTH_SHORT).show()
//            return
//        }
        initViews()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_files_list, container, false)
        var myView: View = binding.root
        return myView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        initViews()
    }

    private fun initViews() {
        filesRecyclerView.layoutManager = LinearLayoutManager(context)
        filesRecyclerView.adapter = viewModel.getAdapter()
        viewModel.updateDate()
    }


}