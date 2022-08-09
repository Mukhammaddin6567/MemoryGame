package uz.gita.memorygame.utils

import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

fun myLog(message: String, tag: String = "TTT") {
    Timber.tag(tag).d(message)
}

fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("mm:ss", Locale.getDefault())
    return format.format(date)
}