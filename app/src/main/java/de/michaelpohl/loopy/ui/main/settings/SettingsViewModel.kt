package de.michaelpohl.loopy.ui.main.settings

import androidx.lifecycle.MutableLiveData
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.Settings
import de.michaelpohl.loopy.common.immutable
import de.michaelpohl.loopy.model.AppStateRepository
import de.michaelpohl.loopy.ui.main.base.BaseViewModel
import de.michaelpohl.loopy.ui.main.settings.items.SettingsItemModel

class SettingsViewModel(private val stateRepo: AppStateRepository) : BaseViewModel() {

    private val _state = MutableLiveData<UIState>(UIState(stateRepo.settings.toItemModels()))
    val state = _state.immutable()

    data class UIState(
        val settings: List<SettingsItemModel>
    )

    // TODO this is ugly
    private fun Settings.toItemModels(): List<SettingsItemModel> {
        val list = mutableListOf<SettingsItemModel>()
        list.add(SettingsItemModel.Header(
            label = getString(R.string.settings_label_loop_switching_behaviour)
        ))
        list.add(
            SettingsItemModel.ToggleableSetting(
                label = getString(R.string.settings_item_switch_immediately),
                secondLabel = getString(R.string.settings_item_wait_until_finished),
                setting = AppSetting.WAIT_MODE,
                toggleState = if (this.isWaitMode)
                    SettingsItemModel.ToggleableSetting.Companion.ToggleState.SECOND else
                    SettingsItemModel.ToggleableSetting.Companion.ToggleState.FIRST
            )
        )
        list.add(SettingsItemModel.Header(
            label = getString(R.string.settings_label_accepted_file_types)
        ))
        list.add(
            SettingsItemModel.CheckableSetting(
                label = getString(R.string.settings_item_allow_wav),
                setting = AppSetting.FILE_TYPE,
                isChecked = this.acceptedFileTypes.contains(AppStateRepository.Companion.AudioFileType.WAVE)
            )
        )
        list.add(
            SettingsItemModel.CheckableSetting(
                label = getString(R.string.settings_item_allow_mp3),
                setting = AppSetting.FILE_TYPE,
                isChecked = this.acceptedFileTypes.contains(AppStateRepository.Companion.AudioFileType.MP3)
            )
        )
        list.add(
            SettingsItemModel.CheckableSetting(
                label = getString(R.string.settings_item_allow_ogg),
                setting = AppSetting.FILE_TYPE,
                isChecked = this.acceptedFileTypes.contains(AppStateRepository.Companion.AudioFileType.OGG)
            )
        )
        list.add(SettingsItemModel.Header(
            label = getString(R.string.settings_label_other_settings)
        ))
        list.add(
            SettingsItemModel.CheckableSetting(
                label = getString(R.string.settings_item_show_loop_count),
                setting = AppSetting.COUNT_LOOPS,
                isChecked = this.showLoopCount
            )
        )
        list.add(
            SettingsItemModel.CheckableSetting(
                label = getString(R.string.settings_item_show_loop_count),
                setting = AppSetting.COUNT_LOOPS,
                isChecked = this.showLoopCount
            )
        )
        list.add(
            SettingsItemModel.CheckableSetting(
                label = getString(R.string.settings_item_keep_screen_on),
                setting = AppSetting.COUNT_LOOPS,
                isChecked = this.keepScreenOn
            )
        )
        list.add(
            SettingsItemModel.CheckableSetting(
                label = getString(R.string.settings_item_play_in_background),
                setting = AppSetting.COUNT_LOOPS,
                isChecked = this.playInBackground
            )
        )
        return list.toList()
    }
}

