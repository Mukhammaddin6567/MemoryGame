package uz.gita.memorygame.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import uz.gita.memorygame.utils.SharedPreference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MySharedPreferences
@Inject constructor(@ApplicationContext context: Context) : SharedPreference(context) {

    var soundEffects: Boolean by BooleanPreference(true)

}