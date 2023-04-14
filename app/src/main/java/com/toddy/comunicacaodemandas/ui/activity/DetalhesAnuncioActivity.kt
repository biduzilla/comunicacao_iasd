package com.toddy.comunicacaodemandas.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.toddy.comunicacaodemandas.ID_ANUNCIO
import com.toddy.comunicacaodemandas.IS_FINALIZADO
import com.toddy.comunicacaodemandas.databinding.ActivityDetalhesAnuncioBinding
import com.toddy.comunicacaodemandas.databinding.DialogVerificacaoTaskBinding
import com.toddy.comunicacaodemandas.modelo.Anuncio
import com.toddy.comunicacaodemandas.utils.setPrioridade
import com.toddy.comunicacaodemandas.webClient.AnuncioFirebase


class DetalhesAnuncioActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityDetalhesAnuncioBinding.inflate(layoutInflater)
    }

    private var anuncio = Anuncio()
    private var isFinalizado = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbarVoltar.tvTitulo.text = "Demanda"
        configClicks()
    }

    override fun onResume() {
        super.onResume()
        binding.llCheckout.removeAllViews()
        tentaCarregarAnuncio()
    }

    private fun configClicks() {
        binding.toolbarVoltar.btnVoltar.setOnClickListener {
            salvarFechar()
        }
        binding.toolbarVoltar.btnEdit.setOnClickListener {
            AnuncioFirebase().salvarAnuncio(anuncio)
            Intent(this, FormAnuncioActivity::class.java).apply {
                putExtra(ID_ANUNCIO, anuncio.id)
                startActivity(this)
            }
        }
    }

    private fun salvarFechar() {
        if (!isFinalizado) {
            AnuncioFirebase().salvarAnuncio(anuncio)
        }
        finish()
    }


    private fun tentaCarregarAnuncio() {
        val anuncioId = intent.getStringExtra(ID_ANUNCIO)
        Log.i("infoteste", "tentaCarregarAnuncio: $anuncioId")
        anuncioId?.let {
            AnuncioFirebase().recuperaAnuncioById(it, binding) { anuncioRecuperado ->
                anuncioRecuperado?.let { anuncio ->
                    carregaDados(anuncio)

                    gerarCheckList()
                }
            }
        }
        isFinalizado = intent.getBooleanExtra(IS_FINALIZADO, false)

        if (isFinalizado) {
            binding.toolbarVoltar.btnEdit.visibility = View.GONE
        }
    }


    private fun carregaDados(anuncio: Anuncio) {
        with(binding) {
            tvTituloAnuncio.text = anuncio.titulo
            tvDescricao.text = anuncio.descricao
            tvPrazo.text = "Prazo: ${anuncio.prazo}"
        }

        if (!isFinalizado) {
            when (setPrioridade(anuncio.prazo!!)) {
                in 0..7 -> binding.tvPrioridade.text = "Prioridade: Alta"
                in 8..14 -> binding.tvPrioridade.text = "Prioridade: Média"
                else -> binding.tvPrioridade.text = "Prioridade: Baixa"
            }
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
                if (isFinalizado) {
                    isChecked = true
                    isClickable = false
                } else {
                    isChecked = valor
                }
                minWidth = 60
                minHeight = 60
                text = anuncio.tarefas[index]
                textSize = 24f
                layoutParams = params
                setTextColor(Color.parseColor("#EBEBD3"))
                binding.llCheckout.addView(this)

                if (!isFinalizado) {
                    setOnClickListener {
                        anuncio.checkList[index] = isChecked
                        verificaCheckList(index)
                    }
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
                anuncio.finalizado = true
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

