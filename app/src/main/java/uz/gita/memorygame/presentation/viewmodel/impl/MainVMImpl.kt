package uz.gita.memorygame.presentation.viewmodel.impl

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.gita.memorygame.R
import uz.gita.memorygame.data.model.LevelEnum
import uz.gita.memorygame.domain.usecase.SoundEffectsUC
import uz.gita.memorygame.presentation.viewmodel.MainVM
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class MainVMImpl
@Inject constructor(
    private val soundEffectsUC: SoundEffectsUC
) : ViewModel(), MainVM {

    override val isSoundEnabledLD: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    override val emitSoundClickLD: MutableLiveData<Unit> by lazy { MutableLiveData<Unit>() }
    override val soundIconLD: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    override val navigateGameScreenLD: MutableLiveData<LevelEnum> by lazy { MutableLiveData<LevelEnum>() }
    override val showBackSnackLD: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    override val popBackStackLD: MutableLiveData<Unit> by lazy { MutableLiveData<Unit>() }

    private var soundStatus by Delegates.notNull<Boolean>()

    override fun init() {
        soundEffectsUC
            .soundEffectsStatus()
            .onEach { state ->
                soundStatus = state
                isSoundEnabledLD.value = state
                soundIconLD.value = when (soundStatus) {
                    true -> R.drawable.ic_sound_on
                    else -> R.drawable.ic_sound_off
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onClickLevel(levelEnum: LevelEnum) {
        if (soundStatus) emitSoundClickLD.value = Unit
        navigateGameScreenLD.value = levelEnum
    }

    override fun onClickSound() {
        if (!soundStatus) emitSoundClickLD.value = Unit
        soundEffectsUC
            .setSoundEffectsStatus(soundStatus)
            .onEach { status ->
                soundStatus = status
                soundIconLD.value = when (soundStatus) {
                    true -> R.drawable.ic_sound_on
                    else -> R.drawable.ic_sound_off
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onClickBack() {
        if (soundStatus) emitSoundClickLD.value = Unit
        showBackSnackLD.value = R.string.text_back_pressed
    }

    override fun popBackStack() {
        if (soundStatus) emitSoundClickLD.value = Unit
        popBackStackLD.value = Unit
    }
}