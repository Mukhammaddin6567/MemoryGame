package uz.gita.memorygame.domain.repository.impl

import uz.gita.memorygame.R
import uz.gita.memorygame.data.MySharedPreferences
import uz.gita.memorygame.data.model.ImageData
import uz.gita.memorygame.data.model.LevelEnum
import uz.gita.memorygame.domain.repository.AppRepository
import javax.inject.Inject

class AppRepositoryImpl
@Inject constructor(
    private val mySharedPreferences: MySharedPreferences
) : AppRepository {

    private val imageList: MutableList<ImageData> = ArrayList()

    init {
        load()
    }

    override suspend fun getDataByLevel(levelEnum: LevelEnum): List<ImageData> {
        val count = levelEnum.x * levelEnum.y
        imageList.shuffle()
        return imageList.subList(0, count / 2)
    }

    override suspend fun changeSoundEffectsStatus(status: Boolean) {
        mySharedPreferences.soundEffects = !status
    }

    override suspend fun getSoundEffectsStatus(): Boolean = mySharedPreferences.soundEffects

    private fun load() {
        imageList.add(ImageData(id = 1, image = R.drawable.image_1))
        imageList.add(ImageData(id = 2, image = R.drawable.image_2))
        imageList.add(ImageData(id = 3, image = R.drawable.image_3))
        imageList.add(ImageData(id = 4, image = R.drawable.image_4))
        imageList.add(ImageData(id = 5, image = R.drawable.image_5))
        imageList.add(ImageData(id = 6, image = R.drawable.image_6))
        imageList.add(ImageData(id = 7, image = R.drawable.image_7))
        imageList.add(ImageData(id = 8, image = R.drawable.image_8))
        imageList.add(ImageData(id = 9, image = R.drawable.image_9))
        imageList.add(ImageData(id = 10, image = R.drawable.image_10))
        imageList.add(ImageData(id = 11, image = R.drawable.image_11))
        imageList.add(ImageData(id = 12, image = R.drawable.image_12))
        imageList.add(ImageData(id = 13, image = R.drawable.image_13))
        imageList.add(ImageData(id = 14, image = R.drawable.image_14))
        imageList.add(ImageData(id = 15, image = R.drawable.image_15))
        imageList.add(ImageData(id = 16, image = R.drawable.image_16))
        imageList.add(ImageData(id = 17, image = R.drawable.image_17))
        imageList.add(ImageData(id = 18, image = R.drawable.image_18))
        imageList.add(ImageData(id = 19, image = R.drawable.image_19))
        imageList.add(ImageData(id = 20, image = R.drawable.image_20))
        imageList.add(ImageData(id = 21, image = R.drawable.image_21))
        imageList.add(ImageData(id = 22, image = R.drawable.image_22))
        imageList.add(ImageData(id = 23, image = R.drawable.image_23))
        imageList.add(ImageData(id = 24, image = R.drawable.image_24))
        imageList.add(ImageData(id = 25, image = R.drawable.image_25))
    }
}