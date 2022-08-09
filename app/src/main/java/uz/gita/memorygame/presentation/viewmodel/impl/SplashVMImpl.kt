package uz.gita.memorygame.presentation.viewmodel.impl

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.gita.memorygame.presentation.viewmodel.SplashVM
import javax.inject.Inject

@HiltViewModel
class SplashVMImpl
@Inject constructor(

) : ViewModel(), SplashVM {
    override val navigateMainScreenLD: MutableLiveData<Unit> by lazy { MutableLiveData<Unit>() }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            delay(2500)
            navigateMainScreenLD.postValue(Unit)
        }
    }

}