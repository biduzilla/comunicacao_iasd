package com.toddy.comunicacaodemandas.dao

import android.app.Activity
import android.util.Log
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import com.toddy.comunicacaodemandas.databinding.ActivityFormAnuncioBinding
import com.toddy.comunicacaodemandas.extensions.Toast
import com.toddy.comunicacaodemandas.modelo.Anuncio

class AnuncioDao {
    fun salvarAnuncio(anuncio: Anuncio, activity: Activity, binding: ActivityFormAnuncioBinding) {
        Log.i("infoteste", "salvarAnuncio: ${anuncio.toString()}")
        FirebaseDatabase.getInstance().reference
            .child("anuncios")
            .child(anuncio.id)
            .setValue(anuncio)
            .addOnCompleteListener {
                activity.finish()
            }.addOnFailureListener {
                binding.btnSalvar.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE

                Toast(activity.baseContext, "Error ao salvar anúncio!")
            }
    }
}