package de.michaelpohl.loopy.ui.main.help

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.databinding.FragmentHelpBinding
import de.michaelpohl.loopy.ui.main.BaseFragment

class HelpFragment : BaseFragment() {

    private lateinit var viewModel: HelpViewModel
    private lateinit var binding: FragmentHelpBinding

    companion object {
        fun newInstance(): HelpFragment {
            return HelpFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_help, container, false)
        var myView: View = binding.root
        return myView
    }
}