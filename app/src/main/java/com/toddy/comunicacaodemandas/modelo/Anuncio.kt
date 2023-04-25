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
    var checkList: MutableList<Boolean> = mutableListOf(),
    var prazo: String? = "",
    var prioridade: Int? = 0,
    var finalizado: Boolean = false,
    var notificacao:Boolean = false,
) : Parcelable {
    init {
        FirebaseDatabase.getInstance().reference.push().key?.let {
            id = it
        }
    }
}
