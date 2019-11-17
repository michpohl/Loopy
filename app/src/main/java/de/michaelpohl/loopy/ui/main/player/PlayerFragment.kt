package de.michaelpohl.loopy.ui.main.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.databinding.FragmentPlayerBinding
import de.michaelpohl.loopy.ui.main.BaseFragment
import org.koin.android.viewmodel.ext.android.getViewModel

class PlayerFragment: BaseFragment() {
//    private val viewModel: PlayerViewModel = getViewModel()
    private lateinit var binding : FragmentPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false)
//        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}