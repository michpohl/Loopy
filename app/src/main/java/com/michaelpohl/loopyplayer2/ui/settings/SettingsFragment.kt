package com.michaelpohl.loopyplayer2.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.michaelpohl.delegationadapter.clickableDelegate
import com.michaelpohl.delegationadapter.customAdapter
import com.michaelpohl.delegationadapter.delegate
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.R.id.rv_settings_recycler
import com.michaelpohl.loopyplayer2.common.find
import com.michaelpohl.loopyplayer2.databinding.FragmentSettingsBinding
import com.michaelpohl.loopyplayer2.ui.base.BaseFragment
import com.michaelpohl.loopyplayer2.ui.settings.items.*
import org.koin.android.ext.android.inject

class SettingsFragment : BaseFragment() {

    override val titleResource = R.string.title_settings
    override val viewModel: SettingsViewModel by inject()
    private lateinit var binding: FragmentSettingsBinding

    private val adapter = customAdapter<SettingsItemModel, SettingsViewModel.UIState> {
        delegates = listOf(
            delegate<SettingsItemModel.Header, SettingsHeaderViewHolder>(
                R.layout.item_settings_header),
            clickableDelegate<SettingsItemModel.CheckableSetting, SettingsCheckableViewHolder>
                (R.layout.item_settings_checkable) { viewModel.onSettingsItemClicked(it) },
            clickableDelegate<SettingsItemModel.MultipleChoiceSetting, SettingsMultipleChoiceViewHolder>
                (R.layout.item_settings_multiplechoice) { viewModel.onSettingsItemClicked(it) },
            clickableDelegate<SettingsItemModel.FileTypeSetting, SettingsFileTypeViewHolder>
                (R.layout.item_settings_checkable) { viewModel.onSettingsItemClicked(it) }
        )
        sorting = SettingsItemSorting()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.find<RecyclerView>(rv_settings_recycler).adapter = this.adapter
        viewModel.state.observeWith {
            adapter.update(it)
        }
    }
}
