package com.toddy.comunicacaodemandas.webClient

import android.app.Activity
import android.util.Log
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.toddy.comunicacaodemandas.databinding.ActivityDetalhesAnuncioBinding
import com.toddy.comunicacaodemandas.databinding.ActivityFormAnuncioBinding
import com.toddy.comunicacaodemandas.extensions.Toast
import com.toddy.comunicacaodemandas.modelo.Anuncio
import com.toddy.comunicacaodemandas.ui.activity.DetalhesAnuncioActivity

class AnuncioFirebase {
    fun salvarAnuncio(anuncio: Anuncio, activity: Activity, binding: ActivityFormAnuncioBinding) {

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

    fun recuperaAnuncios(anunciosRecuperados: (anunciosList: List<Anuncio>?) -> Unit) {
        val anuncios = mutableListOf<Anuncio>()

        FirebaseDatabase.getInstance().reference
            .child("anuncios")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.children.forEach {
                            it.getValue(Anuncio::class.java)?.let { anuncio ->
                                anuncios.add(anuncio)
                            }
                        }
                        anunciosRecuperados(anuncios)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun recuperaAnuncioById(
        binding: ActivityDetalhesAnuncioBinding,
        idAnuncio: String,
        anunciosRecuperados: (anuncioRecuperado: Anuncio?) -> Unit
    ) {
        val anuncios = mutableListOf<Anuncio>()

        FirebaseDatabase.getInstance().reference
            .child("anuncios")
            .child(idAnuncio)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        anunciosRecuperados(snapshot.getValue(Anuncio::class.java))
                        with(binding) {
                            progressBar.visibility = View.GONE
                            scrollView.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}