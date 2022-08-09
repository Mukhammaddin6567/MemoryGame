package uz.gita.memorygame.domain.repository

import uz.gita.memorygame.data.model.ImageData
import uz.gita.memorygame.data.model.LevelEnum

interface AppRepository {

    suspend fun getDataByLevel(levelEnum: LevelEnum): List<ImageData>

    suspend fun changeSoundEffectsStatus(status: Boolean)

    suspend fun getSoundEffectsStatus(): Boolean

}