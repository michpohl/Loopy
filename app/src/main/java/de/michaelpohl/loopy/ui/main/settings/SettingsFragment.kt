package de.michaelpohl.loopy.ui.main.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.adapter.adapter.adapter
import com.example.adapter.adapter.delegate
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.R.id.rv_settings_recycler
import de.michaelpohl.loopy.common.find
import de.michaelpohl.loopy.databinding.FragmentSettingsBinding
import de.michaelpohl.loopy.ui.main.base.BaseFragment
import de.michaelpohl.loopy.ui.main.settings.items.SettingsCheckableViewHolder
import de.michaelpohl.loopy.ui.main.settings.items.SettingsHeaderViewHolder
import de.michaelpohl.loopy.ui.main.settings.items.SettingsItemModel
import de.michaelpohl.loopy.ui.main.settings.items.SettingsToggleableViewHolder
import org.koin.android.ext.android.inject
import timber.log.Timber

class SettingsFragment : BaseFragment() {

    private val viewModel: SettingsViewModel by inject()
    private lateinit var binding: FragmentSettingsBinding

    private val adapter = adapter<SettingsItemModel> {
        delegates = listOf(
            delegate<SettingsItemModel.CheckableSetting, SettingsCheckableViewHolder>(R.layout.item_settings_checkable),
            delegate<SettingsItemModel.Header, SettingsHeaderViewHolder>(R.layout.item_settings_header),
            delegate<SettingsItemModel.ToggleableSetting, SettingsToggleableViewHolder>(R.layout.item_settings_toggleable)
        )
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
            Timber.d("UIState: $it")
            adapter.update(it.settings)
        }
    }
}
