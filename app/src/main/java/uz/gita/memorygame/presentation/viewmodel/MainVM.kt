package uz.gita.memorygame.presentation.viewmodel

import androidx.lifecycle.LiveData
import uz.gita.memorygame.data.model.LevelEnum

interface MainVM {

    val isSoundEnabledLD: LiveData<Boolean>
    val emitSoundClickLD: LiveData<Unit>
    val soundIconLD: LiveData<Int>
    val navigateGameScreenLD: LiveData<LevelEnum>
    val showBackSnackLD: LiveData<Int>
    val popBackStackLD: LiveData<Unit>

    fun init()
    fun onClickLevel(levelEnum: LevelEnum)
    fun onClickSound()
    fun onClickBack()
    fun popBackStack()

}