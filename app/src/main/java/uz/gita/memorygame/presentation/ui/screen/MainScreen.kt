package uz.gita.memorygame.presentation.ui.screen

import android.graphics.Color
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.gita.memorygame.R
import uz.gita.memorygame.data.model.LevelEnum
import uz.gita.memorygame.databinding.ScreenMainBinding
import uz.gita.memorygame.presentation.viewmodel.MainVM
import uz.gita.memorygame.presentation.viewmodel.impl.MainVMImpl
import kotlin.properties.Delegates

@AndroidEntryPoint
class MainScreen : Fragment(R.layout.screen_main) {

    private val viewBinding by viewBinding(ScreenMainBinding::bind)
    private val viewModel: MainVM by viewModels<MainVMImpl>()
    private val navController: NavController by lazy(LazyThreadSafetyMode.NONE) { findNavController() }
    private var soundPool: SoundPool? = null
    private var soundClick by Delegates.notNull<Int>()

    private var onBackPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) = with(viewModel) {
        super.onCreate(savedInstanceState)
        navigateGameScreenLD.observe(this@MainScreen) {
            navController.navigate(MainScreenDirections.actionMainScreenToGameScreen(it))
        }
        popBackStackLD.observe(this@MainScreen) { requireActivity().finish() }
        showBackSnackLD.observe(this@MainScreen,showBackSnackLDObserver)
        isSoundEnabledLD.observe(this@MainScreen, isSoundEnabledLDObserver)
        emitSoundClickLD.observe(this@MainScreen, emitSoundClickLDObserver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeViewBinding(viewBinding)
        subscribeViewModel(viewModel)
    }

    private fun subscribeViewBinding(viewBinding: ScreenMainBinding) = with(viewBinding) {
        registerOnBackPressed()
        buttonSound.setOnClickListener { viewModel.onClickSound() }
        easy.setOnClickListener { viewModel.onClickLevel(LevelEnum.EASY) }
        medium.setOnClickListener { viewModel.onClickLevel(LevelEnum.MEDIUM) }
        hard.setOnClickListener { viewModel.onClickLevel(LevelEnum.HARD) }
    }

    private fun subscribeViewModel(viewModel: MainVM) = with(viewModel) {
        viewModel.soundIconLD.observe(viewLifecycleOwner) { image ->
            viewBinding.buttonSound.setImageResource(image)
        }
    }

    private val showBackSnackLDObserver = Observer<Int> { message ->
        Snackbar.make(requireView(), resources.getString(message), Snackbar.LENGTH_SHORT)
            .apply {
                setBackgroundTint(Color.BLACK)
                show()
            }
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
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
        soundClick = soundPool!!.load(requireContext(), R.raw.click, 1)
    }

    private val emitSoundClickLDObserver = Observer<Unit> {
        soundPool?.play(soundClick, 1f, 1f, 1, 0, 1f)
    }

    private fun registerOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (onBackPressedTime + 2000 > System.currentTimeMillis()) {
                        Timber.d("AAA: $onBackPressedTime")
                        viewModel.popBackStack()
                        return
                    } else viewModel.onClickBack()
                    onBackPressedTime = System.currentTimeMillis()
                }
            })
    }

    override fun onResume() {
        super.onResume()
        viewModel.init()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        soundPool = null
    }

}