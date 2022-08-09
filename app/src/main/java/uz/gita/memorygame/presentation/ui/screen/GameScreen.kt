package uz.gita.memorygame.presentation.ui.screen

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.gita.memorygame.R
import uz.gita.memorygame.data.model.ImageData
import uz.gita.memorygame.databinding.ScreenGameBinding
import uz.gita.memorygame.presentation.ui.dialog.TimeOverDialog
import uz.gita.memorygame.presentation.ui.dialog.WinDialog
import uz.gita.memorygame.presentation.viewmodel.GameVM
import uz.gita.memorygame.presentation.viewmodel.impl.GameVMImpl
import uz.gita.memorygame.utils.convertLongToTime
import kotlin.properties.Delegates

@AndroidEntryPoint
class GameScreen : Fragment(R.layout.screen_game) {

    private val viewBinding by viewBinding(ScreenGameBinding::bind)
    private val viewModel: GameVM by viewModels<GameVMImpl>()
    private val args: GameScreenArgs by navArgs()
    private var _height = 0
    private var _width = 0
    private var imageViews: ArrayList<ImageView>? = null
    private var onBackPressedTime: Long = 0L
    private var soundPool: SoundPool? = null
    private var soundClick by Delegates.notNull<Int>()
    private var soundClickImage by Delegates.notNull<Int>()
    private var soundCorrect by Delegates.notNull<Int>()
    private var soundWrong by Delegates.notNull<Int>()
    private var soundDialogWin by Delegates.notNull<Int>()
    private var soundDialogTimeOver by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) = with(viewModel) {
        super.onCreate(savedInstanceState)
        isSoundEnabledLD.observe(this@GameScreen, isSoundEnabledLDObserver)
        emitSoundClickLD.observe(this@GameScreen, emitSoundClickLDObserver)
        emitSoundClickImageLD.observe(this@GameScreen, emitSoundClickImageLDObserver)
        emitSoundCorrectLD.observe(this@GameScreen, emitSoundCorrectLDObserver)
        emitSoundWrongLD.observe(this@GameScreen, emitSoundWrongLDObserver)
        emitSoundWinDialogLD.observe(this@GameScreen, emitSoundWinDialogLDObserver)
        emitSoundTimeOverDialogLD.observe(this@GameScreen, emitSoundTimeOverDialogLDObserver)
        showWinnerDialogLD.observe(this@GameScreen, showWinnerDialogLDObserver)
        showTimeOverDialogLD.observe(this@GameScreen, showTimeOverDialogLDObserver)
        showHomeSnackLD.observe(this@GameScreen, showHomeSnackLDObserver)
        showRestartSnackLD.observe(this@GameScreen, showRestartSnackLDObserver)
        showBackSnackLD.observe(this@GameScreen, showBackSnackLDObserver)
        restartGameLD.observe(this@GameScreen, restartGameLDObserver)
        popBackStackLD.observe(this@GameScreen, popBackStackLDObserver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeViewBinding(viewBinding)
        subscribeViewModel(viewModel)
    }

    private fun subscribeViewBinding(viewBinding: ScreenGameBinding) = with(viewBinding) {
        registerOnBackPressed()
        buttonRestart.setOnClickListener { viewModel.onClickRestart() }
        buttonHome.setOnClickListener { viewModel.onClickHome() }
        main.post {
            _height = (main.height) / (args.level.y + 4)
            _width = (main.width) / (args.level.x + 1)
            container.layoutParams.apply {
                height = _height * args.level.y
                width = _width * args.level.x
            }
            viewModel.initGame(args.level)
        }
    }

    private fun subscribeViewModel(viewModel: GameVM) = with(viewModel) {
        loadDefaultImagesLD.observe(viewLifecycleOwner, loadDefaultImagesLDObserver)
        allImagesLD.observe(viewLifecycleOwner, allImagesLDObserver)
        isOnClickEnabledLD.observe(viewLifecycleOwner, isOnClickEnabledLDObserver)
        openImageLD.observe(viewLifecycleOwner, openImageLDObserver)
        closePairImagesLD.observe(viewLifecycleOwner, closePairImagesLDObserver)
        closeAllImagesLD.observe(viewLifecycleOwner, closeAllImagesLDObserver)
        dismissImageLD.observe(viewLifecycleOwner, dismissImageLDObserver)
        shakeImageLD.observe(viewLifecycleOwner, shakeImageLDObserver)
        triesLD.observe(viewLifecycleOwner, triesLDObserver)
        timerLD.observe(viewLifecycleOwner, timerLDObserver)
    }

    private val loadDefaultImagesLDObserver = Observer<Unit> {
        imageViews = ArrayList()
        for (i in 0 until args.level.x) {
            for (j in 0 until args.level.y) {
                val image = ImageView(requireContext())
                viewBinding.container.addView(image)
                image.layoutParams.apply {
                    height = _height
                    width = _width
                }
                image.x = i * _width * 1f
                image.y = j * _height * 1f
                image.setPadding(4, 4, 4, 4)
                image.animate().setDuration(250).alpha(1f)
                image.setImageResource(R.drawable.image_back)
                imageViews?.add(image)
            }
        }
    }

    private val allImagesLDObserver = Observer<List<ImageData>> { list ->
        for (i in list.indices) {
            imageViews!![i].tag = list[i]
            imageViews!![i].setOnClickListener {
                viewModel.startTimer(args.level)
                if (imageViews!![i].rotationY == 360f) viewModel.shakeImageOnClick(it as ImageView)
                else viewModel.openImageOnClick(it as ImageView, i)
            }
        }
    }

    private val isOnClickEnabledLDObserver = Observer<Boolean> { status ->
        for (i in 0 until imageViews!!.size) imageViews?.get(i)?.isEnabled = status
    }

    private val isSoundEnabledLDObserver = Observer<Boolean> { status ->
        if (!status) return@Observer
        val audioAttributes = AudioAttributes
            .Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        soundPool = SoundPool
            .Builder()
            .setMaxStreams(6)
            .setAudioAttributes(audioAttributes)
            .build()
        soundClick = soundPool!!.load(requireContext(), R.raw.click, 1)
        soundClickImage = soundPool!!.load(requireContext(), R.raw.click_image, 1)
        soundCorrect = soundPool!!.load(requireContext(), R.raw.correct, 1)
        soundWrong = soundPool!!.load(requireContext(), R.raw.wrong, 1)
        soundDialogWin = soundPool!!.load(requireContext(), R.raw.dialog_win, 1)
        soundDialogTimeOver = soundPool!!.load(requireContext(), R.raw.dialog_time_over, 1)
    }

    private val emitSoundClickLDObserver = Observer<Unit> {
        soundPool?.play(soundClick, 1f, 1f, 1, 0, 1f)
    }

    private val emitSoundClickImageLDObserver = Observer<Unit> {
        soundPool?.play(soundClickImage, 1f, 1f, 1, 0, 1f)
    }

    private val emitSoundCorrectLDObserver = Observer<Unit> {
        soundPool?.play(soundCorrect, 1f, 1f, 1, 0, 1f)
    }

    private val emitSoundWrongLDObserver = Observer<Unit> {
        soundPool?.play(soundWrong, 1f, 1f, 1, 0, 1f)
    }

    private val emitSoundWinDialogLDObserver = Observer<Unit> {
        soundPool?.play(soundDialogWin, 1f, 1f, 1, 0, 1f)
    }

    private val emitSoundTimeOverDialogLDObserver = Observer<Unit> {
        soundPool?.play(soundDialogTimeOver, 1f, 1f, 1, 0, 1f)
    }

    private val openImageLDObserver = Observer<ImageView> { image -> openImage(image) }

    private val closePairImagesLDObserver = Observer<Pair<Int, Int>> { positions ->
        closeImage(positions.first)
        closeImage(positions.second, true)
    }

    private val closeAllImagesLDObserver = Observer<Unit> {
        /*for (i in 0..imageViews!!.size - 2) {
            closeImage(i)
        }
        closeImage(imageViews!!.size - 1, true)*/
    }

    private val dismissImageLDObserver = Observer<Pair<Int, Int>> { dismiss ->
        dismissImage(dismiss.first)
        dismissImage(dismiss.second)
    }

    private val shakeImageLDObserver = Observer<ImageView> { image -> shakeImage(image) }

    private val triesLDObserver = Observer<Int> { triesCount ->
        viewBinding.textTries.text = resources.getString(R.string.text_tries_count, triesCount)
    }

    private val timerLDObserver = Observer<Long> { timer ->
        viewBinding.textTimer.text = convertLongToTime(timer)
    }

    private val showWinnerDialogLDObserver = Observer<Unit> {
        val dialog = WinDialog()
        dialog.setOnClickMenuListener { viewModel.popBackStack() }
        dialog.setOnClickRestartListener { viewModel.restartGame() }
        dialog.show(childFragmentManager, "WinDialog")
    }

    private val showTimeOverDialogLDObserver = Observer<Unit> {
        Timber.d("showTimeOverDialogLDObserver")
        val dialog = TimeOverDialog()
        dialog.setOnClickMenuListener { viewModel.popBackStack() }
        dialog.setOnClickRestartListener { viewModel.restartGame() }
        dialog.show(childFragmentManager, "TimeOverDialog")
    }

    private val showRestartSnackLDObserver = Observer<Pair<Int, Int>> { data ->
        Snackbar.make(requireView(), resources.getString(data.first), Snackbar.LENGTH_SHORT)
            .apply {
                setAction(resources.getString(data.second)) {
                    viewModel.restartGame()
                }
                setBackgroundTint(Color.BLACK)
                setActionTextColor(Color.GREEN)
                show()
            }
    }

    private val showHomeSnackLDObserver = Observer<Pair<Int, Int>> { data ->
        Snackbar.make(requireView(), resources.getString(data.first), Snackbar.LENGTH_SHORT)
            .apply {
                setAction(resources.getString(data.second)) {
                    viewModel.popBackStack()
                }
                setBackgroundTint(Color.BLACK)
                setActionTextColor(Color.GREEN)
                show()
            }
    }

    private val showBackSnackLDObserver = Observer<Int> { message ->
        Snackbar.make(requireView(), resources.getString(message), Snackbar.LENGTH_SHORT)
            .apply {
                setBackgroundTint(Color.BLACK)
                show()
            }
    }

    private val popBackStackLDObserver = Observer<Unit> {
        findNavController().popBackStack()
    }

    private val restartGameLDObserver = Observer<Unit> {
        imageViews?.clear()
        imageViews = null
        viewBinding.container.removeAllViews()
        viewModel.initGame(args.level)
    }

    private fun openImage(image: ImageView) {
        image
            .animate()
            .setDuration(250)
            .rotationY(90f)
            .withEndAction {
                image.rotationY = 270f
                image.setImageResource((image.tag as ImageData).image)
                image
                    .animate()
                    .setDuration(250)
                    .rotationY(360f)
                    .setInterpolator(DecelerateInterpolator())
                    .withEndAction {
                        viewModel.enableOnClick()
                    }
            }
            .start()
    }

    private fun closeImage(position: Int, setEnabled: Boolean = false) {
        Timber.d("TTT closeImage: $position")
//        if (imageViews!![position].rotationY != 360f) return
        imageViews!![position]
            .animate()
            .setDuration(250)
            .rotationY(270f)
            .withEndAction {
                imageViews!![position].rotationY = 90f
                imageViews!![position].setImageResource(R.drawable.image_back)
                imageViews!![position]
                    .animate()
                    .setDuration(250)
                    .rotationY(0f)
                    .setInterpolator(DecelerateInterpolator())
                    .withEndAction {
                        if (setEnabled) viewModel.enableOnClick()
                    }
            }
            .start()
    }

    private fun dismissImage(position: Int) {
        imageViews!![position].animate().setDuration(250).alpha(0f).withEndAction {
            imageViews!![position].apply {
                visibility = INVISIBLE
                isEnabled = false
            }
        }
        viewModel.enableOnClick()
    }

    private fun shakeImage(image: ImageView) {
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(
                image,
                "rotation",
                0f,
                25f,
                -25f,
                25f,
                -25f,
                15f,
                -15f,
                6f,
                -6f,
                0f
            )
        )
        animatorSet.start()
    }

    private fun registerOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (onBackPressedTime + 2000 > System.currentTimeMillis()) {
                        viewModel.popBackStack()
                        return
                    } else viewModel.onClickBack()
                    onBackPressedTime = System.currentTimeMillis()
                }
            })
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        Timber.d("onPause")
        viewModel.onPause()
    }

    override fun onDestroyView() {
        soundPool = null
        super.onDestroyView()
    }

}