package com.toddy.comunicacaodemandas.ui.activity

import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.toddy.comunicacaodemandas.webClient.AnuncioFirebase
import com.toddy.comunicacaodemandas.databinding.ActivityFormAnuncioBinding
import com.toddy.comunicacaodemandas.extensions.Toast
import com.toddy.comunicacaodemandas.modelo.Anuncio
import java.text.SimpleDateFormat
import java.util.*

class FormAnuncioActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityFormAnuncioBinding.inflate(layoutInflater)
    }

    private var dataSelecionada: String? = null
    private var anuncio: Anuncio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbarVoltar.tvTitulo.text = "Salvar Anúncio"
        configClicks()
    }

    private fun verificaDados() {

        with(binding) {
            val titulo: String = edtTitulo.text.toString().trim()
            val descricao: String = edtDesc.text.toString().trim()
            val tarefas: String = edtTarefas.text.toString().trim()

            when {
                titulo.isEmpty() -> {
                    edtTitulo.requestFocus()
                    edtTitulo.error = "Campo Obrigatório"
                }
                descricao.isEmpty() -> {
                    edtDesc.requestFocus()
                    edtDesc.error = "Campo Obrigatório"
                }
                tarefas.isEmpty() -> {
                    edtTarefas.requestFocus()
                    edtTarefas.error = "Campo Obrigatório"
                }
                !tarefas.contains("-") -> {
                    edtTarefas.requestFocus()
                    edtTarefas.error = "Separe as tarefas por - "
                }
                dataSelecionada == null -> Toast(baseContext, "Selecione uma data")
                else -> {
                    btnSalvar.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE

                    if (anuncio == null) anuncio = Anuncio()

                    anuncio!!.titulo = titulo
                    anuncio!!.descricao = descricao
                    anuncio!!.tarefas = tarefas.split("-").toMutableList()
                    anuncio!!.prazo = dataSelecionada

                    anuncio!!.tarefas.forEach {
                        if (it.isEmpty()) {
                            anuncio!!.tarefas.remove(it)
                        }
                    }

                    AnuncioFirebase().salvarAnuncio(anuncio = anuncio!!, this@FormAnuncioActivity, this)
                }
            }
        }
    }

    private fun ocultarTeclado() {
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.btnSalvar.windowToken, 0)
    }

    private fun configClicks() {
        binding.btnDatePicker.setOnClickListener {
            ocultarTeclado()
            abrirCalendario()
        }
        binding.btnSalvar.setOnClickListener {
            ocultarTeclado()
            verificaDados()
        }

        binding.toolbarVoltar.btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun abrirCalendario() {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)



        val dpd = DatePickerDialog(
            this, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
//                dataSelecionada = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
//                binding.btnDatePicker.text = dataSelecionada

                myCalendar.set(Calendar.YEAR, selectedYear)
                myCalendar.set(Calendar.MONTH, selectedMonth)
                myCalendar.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth)

                val myFormat = "dd/MM/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                dataSelecionada = sdf.format(myCalendar.time)
                binding.btnDatePicker.text = sdf.format(myCalendar.time)

            },
            year, month, day
        )
        dpd.show()
    }
}