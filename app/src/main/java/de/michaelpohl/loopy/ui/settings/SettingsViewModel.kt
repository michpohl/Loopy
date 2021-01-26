package de.michaelpohl.loopy.ui.settings

import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.SampleRate
import de.michaelpohl.loopy.common.Settings
import de.michaelpohl.loopy.common.SettingsBuilder
import de.michaelpohl.loopy.common.toSampleRate
import de.michaelpohl.loopy.model.AppStateRepository
import de.michaelpohl.loopy.ui.base.BaseUIState
import de.michaelpohl.loopy.ui.base.BaseViewModel
import de.michaelpohl.loopy.ui.settings.AppSetting.*
import de.michaelpohl.loopy.ui.settings.items.SettingsChoice
import de.michaelpohl.loopy.ui.settings.items.SettingsItemModel
import de.michaelpohl.loopy.ui.settings.items.isChecked
import de.michaelpohl.loopy.ui.settings.items.name

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

            with(currentSettings.toMutableList()) {
                removeAt(index)
                add(index, newSetting)
                _state.value = currentState.copy(settings = this)
            }
        }
        save()
    }

    private fun save() {
        val models = currentState.settings
        val builder = SettingsBuilder()
        models.forEach {
            when (it.setting) {
                WAIT_MODE -> {
                    builder.isWaitMode =
                        (it as SettingsItemModel.MultipleChoiceSetting).choices.last().second == true
                }
                FILE_TYPE_MP3, FILE_TYPE_OGG, FILE_TYPE_WAV -> with(it as SettingsItemModel.FileTypeSetting) {
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
                SAMPLE_RATE -> {
                    builder.sampleRate =
                        (it as SettingsItemModel.MultipleChoiceSetting).choices
                            .find { choice -> choice.isChecked() }
                            ?.name()
                            ?.toSampleRate() ?: SampleRate.RATE_44_KHZ
                }
                NONE -> { /* do nothing */
                }
            }
        }
        stateRepo.settings = builder.build()
    }

    override fun initUIState(): UIState {
        return UIState(stateRepo.settings.toItemModels())
    }

    private fun Settings.toItemModels(): List<SettingsItemModel> {
        val list = mutableListOf<SettingsItemModel>()
        list.add(
            SettingsItemModel.MultipleChoiceSetting(
                setting = WAIT_MODE,
                choices = setOf(
                    SettingsChoice(getString(R.string.settings_item_switch_immediately), this.isWaitMode),
                    SettingsChoice(getString(R.string.settings_item_wait_until_finished), !this.isWaitMode))
            )
        )
        list.add(
            SettingsItemModel.MultipleChoiceSetting(
                setting = SAMPLE_RATE,
                choices = SampleRate.values().map {
                    SettingsChoice(it.displayName, this.sampleRate == it)
                }.toSet()
            )
        )
        list.add(
            SettingsItemModel.FileTypeSetting(
                setting = FILE_TYPE_WAV,
                isChecked = this.acceptedFileTypes.contains(AppStateRepository.Companion.AudioFileType.WAVE),
                type = AppStateRepository.Companion.AudioFileType.WAVE
            )
        )
        list.add(
            SettingsItemModel.FileTypeSetting(
                setting = FILE_TYPE_MP3,
                isChecked = this.acceptedFileTypes.contains(AppStateRepository.Companion.AudioFileType.MP3),
                type = AppStateRepository.Companion.AudioFileType.MP3

            )
        )
        list.add(
            SettingsItemModel.FileTypeSetting(
                setting = FILE_TYPE_OGG,
                isChecked = this.acceptedFileTypes.contains(AppStateRepository.Companion.AudioFileType.OGG),
                type = AppStateRepository.Companion.AudioFileType.OGG

            )
        )
        list.add(
            SettingsItemModel.CheckableSetting(
                setting = COUNT_LOOPS,
                isChecked = this.showLoopCount
            )
        )
        list.add(
            SettingsItemModel.CheckableSetting(
                setting = KEEP_SCREEN_ON,
                isChecked = this.keepScreenOn
            )
        )
        list.add(
            SettingsItemModel.CheckableSetting(
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

