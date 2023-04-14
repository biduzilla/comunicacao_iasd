package com.toddy.comunicacaodemandas.extensions

import android.content.Context
import android.content.Intent


fun Context.iniciaActivity(clazz: Class<*>) {
    Intent(this, clazz).apply {
        startActivity(this)
    }
}
