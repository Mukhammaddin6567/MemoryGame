package uz.gita.memorygame.domain.usecase.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import uz.gita.memorygame.data.model.ImageData
import uz.gita.memorygame.data.model.LevelEnum
import uz.gita.memorygame.domain.repository.AppRepository
import uz.gita.memorygame.domain.usecase.AllImagesUseCase
import javax.inject.Inject

class AllImagesUseCaseImpl
@Inject constructor(
    private val repository: AppRepository
) : AllImagesUseCase {

    override fun allImagesByLevel(level: LevelEnum) = flow<List<ImageData>> {
        val list =  repository.getDataByLevel(level)
        val result = ArrayList<ImageData>()
        result.addAll(list)
        result.addAll(list)
        result.shuffle()
        emit(result)
    }.flowOn(Dispatchers.Default)

}