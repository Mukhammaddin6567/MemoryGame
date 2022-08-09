package uz.gita.memorygame.presentation.ui.screen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.memorygame.R
import uz.gita.memorygame.presentation.viewmodel.SplashVM
import uz.gita.memorygame.presentation.viewmodel.impl.SplashVMImpl

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreen : Fragment(R.layout.screen_splash) {

    private val viewModel: SplashVM by viewModels<SplashVMImpl>()
//    private val viewBinding by viewBinding(ScreenSplashBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) = with(viewModel) {
        super.onCreate(savedInstanceState)
        navigateMainScreenLD.observe(this@SplashScreen) {
            findNavController().navigate(R.id.action_splashScreen_to_mainScreen)
        }
    }

}