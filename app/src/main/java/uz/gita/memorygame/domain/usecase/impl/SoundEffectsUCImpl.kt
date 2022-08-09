package uz.gita.memorygame.domain.usecase.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import uz.gita.memorygame.domain.repository.AppRepository
import uz.gita.memorygame.domain.usecase.SoundEffectsUC
import javax.inject.Inject

class SoundEffectsUCImpl @Inject constructor(
    private val repository: AppRepository
) : SoundEffectsUC {

    override fun soundEffectsStatus() = flow<Boolean> {
        emit(repository.getSoundEffectsStatus())
    }.flowOn(Dispatchers.IO)

    override fun setSoundEffectsStatus(status: Boolean) = flow<Boolean> {
        repository.changeSoundEffectsStatus(status)
        emit(repository.getSoundEffectsStatus())
    }.flowOn(Dispatchers.IO)

}