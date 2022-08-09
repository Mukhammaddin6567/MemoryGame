package uz.gita.memorygame.presentation.viewmodel.impl

import android.os.CountDownTimer
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import uz.gita.memorygame.R
import uz.gita.memorygame.data.model.ImageData
import uz.gita.memorygame.data.model.LevelEnum
import uz.gita.memorygame.domain.usecase.AllImagesUseCase
import uz.gita.memorygame.domain.usecase.SoundEffectsUC
import uz.gita.memorygame.presentation.viewmodel.GameVM
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class GameVMImpl
@Inject constructor(
    private val allImagesUseCase: AllImagesUseCase,
    private val soundEffectsUC: SoundEffectsUC
) : ViewModel(), GameVM {

    override val loadDefaultImagesLD: MutableLiveData<Unit> by lazy { MutableLiveData<Unit>() }
    override val allImagesLD: MutableLiveData<List<ImageData>> by lazy { MutableLiveData<List<ImageData>>() }
    override val isOnClickEnabledLD: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    override val isSoundEnabledLD: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    override val emitSoundClickLD: MutableLiveData<Unit> by lazy { MutableLiveData<Unit>() }
    override val emitSoundClickImageLD: MutableLiveData<Unit> by lazy { MutableLiveData<Unit>() }
    override val emitSoundCorrectLD: MutableLiveData<Unit> by lazy { MutableLiveData<Unit>() }
    override val emitSoundWrongLD: MutableLiveData<Unit> by lazy { MutableLiveData<Unit>() }
    override val emitSoundWinDialogLD: MutableLiveData<Unit> by lazy { MutableLiveData<Unit>() }
    override val emitSoundTimeOverDialogLD: MutableLiveData<Unit> by lazy { MutableLiveData<Unit>() }
    override val openImageLD: MutableLiveData<ImageView> by lazy { MutableLiveData<ImageView>() }
    override val closePairImagesLD: MutableLiveData<Pair<Int, Int>> by lazy { MutableLiveData<Pair<Int, Int>>() }
    override val closeAllImagesLD: MutableLiveData<Unit> by lazy { MutableLiveData<Unit>() }
    override val dismissImageLD: MutableLiveData<Pair<Int, Int>> by lazy { MutableLiveData<Pair<Int, Int>>() }
    override val shakeImageLD: MutableLiveData<ImageView> by lazy { MutableLiveData<ImageView>() }
    override val triesLD: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    override val timerLD: MutableLiveData<Long> by lazy { MutableLiveData<Long>() }
    override val showWinnerDialogLD: MutableLiveData<Unit> by lazy { MutableLiveData<Unit>() }
    override val showTimeOverDialogLD: MutableLiveData<Unit> by lazy { MutableLiveData<Unit>() }
    override val showHomeSnackLD: MutableLiveData<Pair<Int, Int>> by lazy { MutableLiveData<Pair<Int, Int>>() }
    override val showRestartSnackLD: MutableLiveData<Pair<Int, Int>> by lazy { MutableLiveData<Pair<Int, Int>>() }
    override val showBackSnackLD: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    override val restartGameLD: MutableLiveData<Unit> by lazy { MutableLiveData<Unit>() }
    override val popBackStackLD: MutableLiveData<Unit> by lazy { MutableLiveData<Unit>() }

    private var countDownTimer: CountDownTimer? = null
    private var currentTimer: Long = 0

    private var oldImageId: Int = -1
    private var oldImagePosition: Int = -1
    private var checkImages: Boolean = false

    private var counterCheckedImages = 0
    private var counterTries = 0

    private var soundStatus by Delegates.notNull<Boolean>()

    private var job: Job? = null

    init {
        soundEffectsUC
            .soundEffectsStatus()
            .onEach { status ->
                soundStatus = status
                isSoundEnabledLD.value = status
            }
            .launchIn(viewModelScope)
    }

    override fun initGame(levelEnum: LevelEnum) {
        counterCheckedImages = (levelEnum.x * levelEnum.y) / 2
        Timber.d("counter: $counterCheckedImages")
        if (countDownTimer != null) {
            countDownTimer?.cancel()
            countDownTimer = null
        }
        loadDefaultImagesLD.value = Unit
        allImagesUseCase
            .allImagesByLevel(levelEnum)
            .onEach {
                allImagesLD.value = it
                timerLD.value = when (levelEnum) {
                    LevelEnum.EASY -> 120000
                    LevelEnum.MEDIUM -> 240000
                    else -> 360000
                }
            }
            .launchIn(viewModelScope)
    }

    override fun startTimer(levelEnum: LevelEnum) {
        if (countDownTimer != null) return
        setTimer(levelEnum)
    }

    override fun openImageOnClick(image: ImageView, imagePosition: Int) {
        Timber.d("openImageOnClick checkImages: $checkImages")
        Timber.d("openImageOnClick oldImageId: $oldImageId")
        Timber.d("openImageOnClick oldImagePosition: $oldImagePosition")
        if (soundStatus) emitSoundClickImageLD.value = Unit
        isOnClickEnabledLD.value = false
        val imageData = image.tag as ImageData
        Timber.d("openImageOnClick newImageId: ${imageData.id}")
        Timber.d("openImageOnClick newImagePosition: $imagePosition")
        Timber.d("openImageOnClick ----------------------------------")
        if (checkImages) {
            openImageLD.value = image
            job?.cancel()
            job = viewModelScope.launch(Dispatchers.IO) {
                delay(500)
                if (oldImageId == imageData.id) {
                    Timber.d("openImageOnClick oldImageId == imageData.id")
                    if (soundStatus) emitSoundCorrectLD.postValue(Unit)
                    dismissImageLD.postValue(Pair(oldImagePosition, imagePosition))
                    counterCheckedImages--
                    if (counterCheckedImages == 0) {
                        if (soundStatus) emitSoundWinDialogLD.postValue(Unit)
                        showWinnerDialogLD.postValue(Unit)
                        countDownTimer?.cancel()
                        countDownTimer = null
                    }
                } else {
                    Timber.d("openImageOnClick oldImageId != imageData.id")
                    emitSoundWrongLD.postValue(Unit)
                    closePairImagesLD.postValue(Pair(oldImagePosition, imagePosition))
                    triesLD.postValue(++counterTries)
                }
            }
            checkImages = false
            return
        }
        checkImages = true
        oldImageId = imageData.id
        oldImagePosition = imagePosition
        openImageLD.value = image
    }

    override fun enableOnClick() {
        isOnClickEnabledLD.value = true
    }

    override fun shakeImageOnClick(image: ImageView) {
        if (soundStatus) emitSoundWrongLD.value = Unit
        shakeImageLD.value = image
    }

    override fun onResume() {
        if (countDownTimer != null) countDownTimer?.start()
    }

    override fun onPause() {
        if (countDownTimer != null) {
            countDownTimer?.cancel()
            countDownTimer = null
            countDownTimer = object : CountDownTimer(currentTimer, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    currentTimer = millisUntilFinished
                    timerLD.value = currentTimer
                }

                override fun onFinish() {
                    Timber.d("onFinish : onPause")
                    if (oldImageId == -1) return
                    if (soundStatus) emitSoundTimeOverDialogLD.value = Unit
                    showTimeOverDialogLD.value = Unit
                    isOnClickEnabledLD.value = false
                }
            }
        }
    }

    override fun onClickRestart() {
        if (soundStatus) emitSoundClickLD.value = Unit
        showRestartSnackLD.value = Pair(R.string.text_restart_game, R.string.text_restart_button)
    }

    override fun restartGame() {
        if (soundStatus) emitSoundClickLD.value = Unit
        oldImageId = -1
        oldImagePosition = -1
        counterTries = 0
        triesLD.value = counterTries
        isOnClickEnabledLD.value = true
        checkImages = false
        restartGameLD.value = Unit
        job = null
        countDownTimer = null
    }

    override fun onClickHome() {
        if (soundStatus) emitSoundClickLD.value = Unit
        showHomeSnackLD.value = Pair(R.string.text_leave_game, R.string.text_leave_button)
    }

    override fun popBackStack() {
        if (soundStatus) emitSoundClickLD.value = Unit
        popBackStackLD.value = Unit
    }

    override fun onClickBack() {
        if (soundStatus) emitSoundClickLD.value = Unit
        showBackSnackLD.value = R.string.text_back_pressed
    }

    private fun setTimer(levelEnum: LevelEnum) {
        val time: Long = when (levelEnum) {
            LevelEnum.EASY -> 120000
            LevelEnum.MEDIUM -> 240000
            else -> 360000
        }
        Timber.d("time: $time")
        countDownTimer = object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentTimer = millisUntilFinished
                timerLD.value = currentTimer
            }

            override fun onFinish() {
                Timber.d("onFinish : setTimer")
                if (soundStatus) emitSoundTimeOverDialogLD.value = Unit
                showTimeOverDialogLD.value = Unit
                isOnClickEnabledLD.value = false
            }
        }
        countDownTimer?.start()
    }

}