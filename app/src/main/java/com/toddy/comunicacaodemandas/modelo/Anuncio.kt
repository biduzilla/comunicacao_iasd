package com.toddy.comunicacaodemandas.modelo


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.FirebaseDatabase
import kotlinx.parcelize.Parcelize

@Parcelize
data class Anuncio(
    @PrimaryKey
    var id: String = "",
    var titulo: String? = "",
    var descricao: String? = "",
    var tarefas: MutableList<String> = mutableListOf(),
    var prazo: String? = "",
    var prioridade: Int? = 0,
    var finalizado: Boolean = false,
) : Parcelable {
    init {
        FirebaseDatabase.getInstance().reference.push().key?.let {
            id = it
        }
    }
}
