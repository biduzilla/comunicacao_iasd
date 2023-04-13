package com.toddy.comunicacaodemandas.modelo


import android.os.Parcelable
import com.google.firebase.database.FirebaseDatabase
import kotlinx.parcelize.Parcelize

@Parcelize
data class Anuncio(
    var id: String = "",
    var titulo: String? = "",
    var descricao: String? = "",
    var tarefas: MutableList<String> = mutableListOf(),
    var prazo: String? = ""
) : Parcelable {
    init {
        FirebaseDatabase.getInstance().reference.push().key?.let {
            id = it
        }
    }
}
