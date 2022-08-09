package uz.gita.memorygame.domain.usecase

import kotlinx.coroutines.flow.Flow

interface SoundEffectsUC {

    fun soundEffectsStatus(): Flow<Boolean>
    fun setSoundEffectsStatus(status: Boolean): Flow<Boolean>

}