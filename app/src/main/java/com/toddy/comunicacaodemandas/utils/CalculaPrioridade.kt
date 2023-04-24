package com.toddy.comunicacaodemandas.utils

import android.util.Log
import com.toddy.comunicacaodemandas.modelo.Anuncio
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun setPrioridade(data: String): Int {

    val formatter = SimpleDateFormat("dd/MM/yyyy")
    val date = formatter.parse(data)
    val millionSeconds: Long = date!!.time - Calendar.getInstance().timeInMillis
    return TimeUnit.MILLISECONDS.toDays(millionSeconds).toInt()
}

fun calculaPrioridade(anuncio: Anuncio): Int {

    val prioridade: Int = when (setPrioridade(anuncio.prazo!!)) {
        in 0..7 -> 0
        in 8..14 -> 1
        in -999..0 -> 0
        else -> 2
    }

    return prioridade
}

