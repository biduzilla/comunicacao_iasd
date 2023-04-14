package com.toddy.comunicacaodemandas.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.toddy.comunicacaodemandas.R
import com.toddy.comunicacaodemandas.databinding.ItemAnuncioBinding
import com.toddy.comunicacaodemandas.modelo.Anuncio
import com.toddy.comunicacaodemandas.utils.setPrioridade

class AnuncioAdapter(
    anuncios: List<Anuncio> = emptyList(),
    var onClick: (anuncio: Anuncio) -> Unit = {}
) : RecyclerView.Adapter<AnuncioAdapter.ViewHolder>() {

    private val anuncios = anuncios.toMutableList()

    inner class ViewHolder(private val binding: ItemAnuncioBinding) :
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
            val prioridade: Int

            with(binding) {
                tvTitulo.text = anuncio.titulo
                tvPrazo.text = anuncio.prazo

                prioridade = when (setPrioridade(anuncio.prazo!!)) {
                    in 0..7 -> 0
                    in 8..14 -> 1
                    else -> 2
                }

                when(prioridade){
                    0->imgPrioridade.setImageResource(R.drawable.placeholder_vermelho)
                    1->imgPrioridade.setImageResource(R.drawable.placeholder_laranja)
                    2->imgPrioridade.setImageResource(R.drawable.placeholder_verde)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnuncioAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAnuncioBinding.inflate(inflater, parent, false)
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