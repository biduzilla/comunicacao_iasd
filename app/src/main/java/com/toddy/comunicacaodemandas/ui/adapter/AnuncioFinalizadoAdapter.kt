package com.toddy.comunicacaodemandas.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.toddy.comunicacaodemandas.R
import com.toddy.comunicacaodemandas.databinding.ItemAnuncioBinding
import com.toddy.comunicacaodemandas.databinding.ItemAnuncioFinalizadoBinding
import com.toddy.comunicacaodemandas.modelo.Anuncio
import com.toddy.comunicacaodemandas.utils.setPrioridade

class AnuncioFinalizadoAdapter(
    anuncios: List<Anuncio> = emptyList(),
    var onClick: (anuncio: Anuncio) -> Unit = {}
) : RecyclerView.Adapter<AnuncioFinalizadoAdapter.ViewHolder>() {

    private val anuncios = anuncios.toMutableList()

    inner class ViewHolder(private val binding: ItemAnuncioFinalizadoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var anuncio: Anuncio

        init {
            itemView.setOnClickListener {
                if (::anuncio.isInitialized) {
                    onClick(anuncio)
                }
            }
        }

        fun vincula(anuncio: Anuncio) {
            this.anuncio = anuncio

            with(binding) {
                tvTitulo.text = anuncio.titulo
                tvPrazo.text = anuncio.prazo
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnuncioFinalizadoAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAnuncioFinalizadoBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = anuncios.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val anuncio = anuncios[position]
        holder.vincula(anuncio)
    }

    fun atualiza(anuncios: List<Anuncio>) {
        this.anuncios.clear()
        this.anuncios.addAll(anuncios)
        notifyDataSetChanged()
    }
}