package com.toddy.comunicacaodemandas.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.toddy.comunicacaodemandas.ID_ANUNCIO
import com.toddy.comunicacaodemandas.databinding.ActivityDetalhesAnuncioBinding
import com.toddy.comunicacaodemandas.databinding.DialogVerificacaoTaskBinding
import com.toddy.comunicacaodemandas.extensions.Toast
import com.toddy.comunicacaodemandas.modelo.Anuncio
import com.toddy.comunicacaodemandas.utils.setPrioridade
import com.toddy.comunicacaodemandas.webClient.AnuncioFirebase


class DetalhesAnuncioActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityDetalhesAnuncioBinding.inflate(layoutInflater)
    }

    private var anuncio = Anuncio()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbarVoltar.tvTitulo.text = "Detalhes da Demanda"
        tentaCarregarAnuncio()
        configClicks()
    }

    private fun configClicks() {
        binding.toolbarVoltar.btnVoltar.setOnClickListener {
            salvarFechar()
        }
    }

    private fun salvarFechar() {
        AnuncioFirebase().salvarAnuncio(anuncio)
        finish()
    }


    private fun tentaCarregarAnuncio() {
        val anuncioId = intent.getStringExtra(ID_ANUNCIO)
        anuncioId?.let {
            AnuncioFirebase().recuperaAnuncioById(binding, it) { anuncioRecuperado ->
                anuncioRecuperado?.let { anuncio ->
                    carregaDados(anuncio)
                    gerarCheckList()
                }
            }
        }
    }


    private fun carregaDados(anuncio: Anuncio) {
        with(binding) {
            tvTituloAnuncio.text = anuncio.titulo
            tvDescricao.text = anuncio.descricao
            tvPrazo.text = "Prazo: ${anuncio.prazo}"
        }
        when (setPrioridade(anuncio.prazo!!)) {
            in 0..7 -> binding.tvPrioridade.text = "Prioridade: Alta"
            in 8..14 -> binding.tvPrioridade.text = "Prioridade: Média"
            else -> binding.tvPrioridade.text = "Prioridade: Baixa"
        }
        this.anuncio = anuncio
    }

    private fun gerarCheckList() {
        val params: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        params.setMargins(0, 32, 0, 0)

        anuncio.checkList.forEachIndexed { index, valor ->
            CheckBox(this@DetalhesAnuncioActivity).apply {
                id = index
                isChecked = valor
                minWidth = 60
                minHeight = 60
                text = anuncio.tarefas[index]
                textSize = 24f
                layoutParams = params
                setTextColor(Color.parseColor("#FFC300"))
                binding.llCheckout.addView(this)

                setOnClickListener {
                    anuncio.checkList[index] = isChecked
                    verificaCheckList(index)
                }
            }
        }
    }

    private fun dialogFinalizarTask(index: Int) {
        DialogVerificacaoTaskBinding.inflate(layoutInflater).apply {
            val dialog = AlertDialog.Builder(this@DetalhesAnuncioActivity)
                .setView(root)
                .create()
            dialog.show()

            btnFinalizar.setOnClickListener {
                salvarFechar()
            }

            btnCancelar.setOnClickListener {
                anuncio.checkList[index] = false
                binding.llCheckout.removeAllViews()
                gerarCheckList()

                dialog.dismiss()
            }
        }
    }

    private fun verificaCheckList(index: Int) {
        var qtd = 0
        anuncio.checkList.forEach {
            if (it) {
                qtd++
            }
        }
        if (qtd == anuncio.checkList.size) {
            dialogFinalizarTask(index)
        }
    }
}

