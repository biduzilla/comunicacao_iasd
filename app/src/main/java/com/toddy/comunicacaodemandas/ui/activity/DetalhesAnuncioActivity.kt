package com.toddy.comunicacaodemandas.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.toddy.comunicacaodemandas.ID_ANUNCIO
import com.toddy.comunicacaodemandas.utils.setPrioridade
import com.toddy.comunicacaodemandas.databinding.ActivityDetalhesAnuncioBinding
import com.toddy.comunicacaodemandas.modelo.Anuncio
import com.toddy.comunicacaodemandas.webClient.AnuncioFirebase

class DetalhesAnuncioActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityDetalhesAnuncioBinding.inflate(layoutInflater)
    }

    private var anuncio = Anuncio()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        tentaCarregarAnuncio()
        configClicks()
    }

    private fun configClicks() {
        binding.toolbarVoltar.btnVoltar.setOnClickListener {
            finish()
        }
    }


    private fun tentaCarregarAnuncio() {
        val anuncioId = intent.getStringExtra(ID_ANUNCIO)
        anuncioId?.let {
            AnuncioFirebase().recuperaAnuncioById(binding, it) { anuncioRecuperado ->
                anuncioRecuperado?.let { anuncio ->
                    carregaDados(anuncio)
                }
            }
        }
    }


    private fun carregaDados(anuncio: Anuncio) {
        with(binding) {
            tvTituloAnuncio.text = anuncio.titulo
            tvDescricao.text = anuncio.descricao
            tvPrazo.text = anuncio.prazo
        }
        when (setPrioridade(anuncio.prazo!!)) {
            in 0..7 -> binding.tvPrioridade.text = "Alta"
            in 8..14 -> binding.tvPrioridade.text = "Média"
            else -> binding.tvPrioridade.text = "Baixa"
        }
        this.anuncio = anuncio
    }
}
