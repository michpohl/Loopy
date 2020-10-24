package de.michaelpohl.loopy.ui.main.settings

import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.Settings
import de.michaelpohl.loopy.common.SettingsBuilder
import de.michaelpohl.loopy.model.AppStateRepository
import de.michaelpohl.loopy.ui.main.base.BaseUIState
import de.michaelpohl.loopy.ui.main.base.BaseViewModel
import de.michaelpohl.loopy.ui.main.settings.AppSetting.*
import de.michaelpohl.loopy.ui.main.settings.items.SettingsItemModel
import de.michaelpohl.loopy.ui.main.settings.items.SettingsItemModel.ToggleableSetting.Companion.ToggleState.FIRST
import de.michaelpohl.loopy.ui.main.settings.items.SettingsItemModel.ToggleableSetting.Companion.ToggleState.SECOND

class SettingsViewModel(private val stateRepo: AppStateRepository) :
    BaseViewModel<SettingsViewModel.UIState>() {

    init {
        _state.postValue(initUIState())
    }

    fun onSettingsItemClicked(setting: SettingsItemModel) {

        val newSetting = setting.flip()
        val currentSettings = state.value?.settings
        currentSettings?.let { settings ->
            val index = settings.indexOf(settings.find { it.label == newSetting.label })
            val mutable = settings.toMutableList()
            mutable.removeAt(index)
            mutable.add(index, newSetting)
            _state.postValue(currentState.copy(settings = mutable))
        }
    }

    // TODO this is less sexy than it could be
    fun save() {
        val models = currentState.settings
        val builder = SettingsBuilder()
        models.forEach {
            when (it.setting) {
                WAIT_MODE -> {
                    builder.isWaitMode =
                        (it as SettingsItemModel.ToggleableSetting).toggleState == SECOND
                }
                FILE_TYPE -> with(it as SettingsItemModel.FileTypeSetting) {
                    if (this.isChecked) builder.addFileType(this.type) else builder.removeFileType(
                        this.type
                    )
                }
                COUNT_LOOPS -> builder.showLoopCount =
                    (it as SettingsItemModel.CheckableSetting).isChecked
                KEEP_SCREEN_ON -> builder.keepScreenOn =
                    (it as SettingsItemModel.CheckableSetting).isChecked
                PLAY_IN_BACKGROUND -> builder.playInBackground =
                    (it as SettingsItemModel.CheckableSetting).isChecked
                NONE -> { /* do nothing */
                }
            }
        }
        stateRepo.settings = builder.build()

    }

    override fun initUIState(): UIState {
        return UIState(stateRepo.settings.toItemModels())
    }

    // TODO this is ugly
    private fun Settings.toItemModels(): List<SettingsItemModel> {
        val list = mutableListOf<SettingsItemModel>()
        list.add(
            SettingsItemModel.Header(
                label = getString(R.string.settings_label_loop_switching_behaviour)
            )
        )
        list.add(
            SettingsItemModel.ToggleableSetting(
                label = getString(R.string.settings_item_switch_immediately),
                secondLabel = getString(R.string.settings_item_wait_until_finished),
                setting = WAIT_MODE,
                toggleState = if (this.isWaitMode)
                    SECOND else
                    FIRST
            )
        )
        list.add(
            SettingsItemModel.Header(
                label = getString(R.string.settings_label_accepted_file_types)
            )
        )
        list.add(
            SettingsItemModel.FileTypeSetting(
                label = getString(R.string.settings_item_allow_wav),
                setting = FILE_TYPE,
                isChecked = this.acceptedFileTypes.contains(AppStateRepository.Companion.AudioFileType.WAVE),
                type = AppStateRepository.Companion.AudioFileType.WAVE
            )
        )
        list.add(
            SettingsItemModel.FileTypeSetting(
                label = getString(R.string.settings_item_allow_mp3),
                setting = FILE_TYPE,
                isChecked = this.acceptedFileTypes.contains(AppStateRepository.Companion.AudioFileType.MP3),
                type = AppStateRepository.Companion.AudioFileType.MP3

            )
        )
        list.add(
            SettingsItemModel.FileTypeSetting(
                label = getString(R.string.settings_item_allow_ogg),
                setting = FILE_TYPE,
                isChecked = this.acceptedFileTypes.contains(AppStateRepository.Companion.AudioFileType.OGG),
                type = AppStateRepository.Companion.AudioFileType.OGG

            )
        )
        list.add(
            SettingsItemModel.Header(
                label = getString(R.string.settings_label_other_settings)
            )
        )
        list.add(
            SettingsItemModel.CheckableSetting(
                label = getString(R.string.settings_item_show_loop_count),
                setting = COUNT_LOOPS,
                isChecked = this.showLoopCount
            )
        )
        list.add(
            SettingsItemModel.CheckableSetting(
                label = getString(R.string.settings_item_keep_screen_on),
                setting = KEEP_SCREEN_ON,
                isChecked = this.keepScreenOn
            )
        )
        list.add(
            SettingsItemModel.CheckableSetting(
                label = getString(R.string.settings_item_play_in_background),
                setting = PLAY_IN_BACKGROUND,
                isChecked = this.playInBackground
            )
        )
        return list.toList()
    }

    data class UIState(
        val settings: List<SettingsItemModel>
    ) : BaseUIState()
}

