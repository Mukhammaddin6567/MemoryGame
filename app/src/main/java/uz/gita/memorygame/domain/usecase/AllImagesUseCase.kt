package uz.gita.memorygame.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.gita.memorygame.data.model.ImageData
import uz.gita.memorygame.data.model.LevelEnum

interface AllImagesUseCase {

    fun allImagesByLevel(levelEnum: LevelEnum): Flow<List<ImageData>>

}