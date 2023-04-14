package com.toddy.comunicacaodemandas.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun setPrioridade(data: String):Int {

    val formatter = SimpleDateFormat("dd/MM/yyyy")
    val date = formatter.parse(data)
    val millionSeconds: Long = date!!.time - Calendar.getInstance().timeInMillis
    Log.i("infoteste", "setPrioridade: ${TimeUnit.MILLISECONDS.toDays(millionSeconds).toInt()}")
    return TimeUnit.MILLISECONDS.toDays(millionSeconds).toInt()
}