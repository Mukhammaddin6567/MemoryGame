package uz.gita.memorygame.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.gita.memorygame.domain.usecase.AllImagesUseCase
import uz.gita.memorygame.domain.usecase.SoundEffectsUC
import uz.gita.memorygame.domain.usecase.impl.AllImagesUseCaseImpl
import uz.gita.memorygame.domain.usecase.impl.SoundEffectsUCImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface UseCaseModule {

    @[Binds Singleton]
    fun bindAllImagesUseCase(impl: AllImagesUseCaseImpl): AllImagesUseCase

    @[Binds Singleton]
    fun bindSoundEffectsUseCase(impl: SoundEffectsUCImpl): SoundEffectsUC

}