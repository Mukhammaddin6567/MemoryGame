package uz.gita.memorygame.presentation.viewmodel

import android.widget.ImageView
import androidx.lifecycle.LiveData
import uz.gita.memorygame.data.model.ImageData
import uz.gita.memorygame.data.model.LevelEnum

interface GameVM {

    val loadDefaultImagesLD: LiveData<Unit>
    val allImagesLD: LiveData<List<ImageData>>
    val isOnClickEnabledLD: LiveData<Boolean>
    val isSoundEnabledLD: LiveData<Boolean>
    val emitSoundClickLD: LiveData<Unit>
    val emitSoundClickImageLD: LiveData<Unit>
    val emitSoundCorrectLD: LiveData<Unit>
    val emitSoundWrongLD: LiveData<Unit>
    val emitSoundWinDialogLD: LiveData<Unit>
    val emitSoundTimeOverDialogLD: LiveData<Unit>
    val openImageLD: LiveData<ImageView>
    val closePairImagesLD: LiveData<Pair<Int,Int>>
    val closeAllImagesLD: LiveData<Unit>
    val dismissImageLD: LiveData<Pair<Int, Int>>
    val shakeImageLD: LiveData<ImageView>
    val triesLD: LiveData<Int>
    val timerLD: LiveData<Long>
    val showWinnerDialogLD: LiveData<Unit>
    val showTimeOverDialogLD: LiveData<Unit>
    val showHomeSnackLD: LiveData<Pair<Int, Int>>
    val showRestartSnackLD: LiveData<Pair<Int, Int>>
    val showBackSnackLD: LiveData<Int>
    val restartGameLD: LiveData<Unit>
    val popBackStackLD: LiveData<Unit>

    fun initGame(levelEnum: LevelEnum)
    fun startTimer(levelEnum: LevelEnum)
    fun openImageOnClick(image: ImageView, imagePosition: Int)
    fun enableOnClick()
    fun shakeImageOnClick(image: ImageView)

    fun onResume()
    fun onPause()
    fun onClickRestart()
    fun restartGame()
    fun onClickHome()
    fun popBackStack()
    fun onClickBack()

}