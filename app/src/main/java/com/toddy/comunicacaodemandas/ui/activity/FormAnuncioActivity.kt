package com.toddy.comunicacaodemandas.ui.activity

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.toddy.comunicacaodemandas.ID_ANUNCIO
import com.toddy.comunicacaodemandas.databinding.ActivityFormAnuncioBinding
import com.toddy.comunicacaodemandas.extensions.Toast
import com.toddy.comunicacaodemandas.modelo.Anuncio
import com.toddy.comunicacaodemandas.notification.*
import com.toddy.comunicacaodemandas.notification.Notification
import com.toddy.comunicacaodemandas.webClient.AnuncioFirebase
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class FormAnuncioActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityFormAnuncioBinding.inflate(layoutInflater)
    }

    private var dataSelecionada: String? = null
    private var dataSelecionadaDate: Calendar? = null
    private var anuncio: Anuncio? = null
    private var isUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbarVoltar.tvTitulo.text = "Salvar Anúncio"
        configClicks()
        verificaUpdate()

    }

    private fun verificaUpdate() {
        val idAnuncio = intent.getStringExtra(ID_ANUNCIO)

        idAnuncio?.let {
            binding.toolbarVoltar.tvTitulo.text = "Atualizar Anúncio"
            binding.btnSalvar.text = "Atualizar"

            AnuncioFirebase().recuperaAnuncioById(idAnuncio) { anuncioRecuperado ->
                anuncioRecuperado?.let { anuncio ->
                    this.anuncio = anuncio
                    dataSelecionada = anuncio.prazo
                    isUpdate = true

                    preencheDados(anuncio)
                }

            }
        }
    }

    private fun preencheDados(anuncio: Anuncio) {
        with(binding) {
            edtTitulo.setText(anuncio.titulo)
            edtDesc.setText(anuncio.descricao)
            edtTarefas.setText(
                anuncio.tarefas.toString().replace("[", "").replace("]", "")
                    .replace(",", "-")
            )
            btnDatePicker.text = anuncio.prazo
        }
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

                    if (anuncio == null) {
                        anuncio = Anuncio()
                        repeat(anuncio!!.tarefas.size) {
                            anuncio!!.checkList.add(false)
                        }
                    }

                    val tarefasLst = tarefas.split("-").toMutableList()

                    tarefasLst.forEachIndexed { index, tarefa ->
                        tarefasLst[index] = tarefa.trimStart()
                        if (tarefasLst[index].isEmpty()) {
                            tarefasLst.removeAt(index)
                        }
                    }

                    anuncio!!.titulo = titulo
                    anuncio!!.descricao = descricao

                    anuncio!!.tarefas = tarefasLst
                    anuncio!!.prazo = dataSelecionada

                    if (tarefasLst.size != anuncio!!.checkList.size) {
                        repeat(tarefasLst.size - anuncio!!.checkList.size) {
                            anuncio!!.checkList.add(false)
                        }
                    }

                    AnuncioFirebase().salvarAnuncio(
                        anuncio = anuncio!!,
                        this@FormAnuncioActivity,
                        this
                    )


                    val daysDiff = diferencaEntreDatas()

                    marcarDatasNotificacao(daysDiff)

                }
            }
        }
    }

    private fun marcarDatasNotificacao(daysDiff: Int) {
        var i: Int = if (daysDiff < 7) {
            daysDiff
        } else {
            7
        }
        repeat(i) {
            scheduleNotification(anuncio!!, i)
            i--
        }
    }

    private fun diferencaEntreDatas(): Int {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val date = formatter.parse(dataSelecionada)
        date?.let {
            calendar.time = it
        }

        val msDiff: Long =
            calendar.timeInMillis - Calendar.getInstance().timeInMillis
        val daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff).toInt()
        return daysDiff + 1
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

                myCalendar.set(Calendar.YEAR, selectedYear)
                myCalendar.set(Calendar.MONTH, selectedMonth)
                myCalendar.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth)

                val myFormat = "dd/MM/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                dataSelecionada = sdf.format(myCalendar.time)
                binding.btnDatePicker.text = sdf.format(myCalendar.time)

                dataSelecionadaDate = myCalendar
            },
            year, month, day
        )
        dpd.datePicker.minDate = System.currentTimeMillis() - 1000;
        dpd.show()
    }

    private fun scheduleNotification(anuncio: Anuncio, days: Int) {
        val intent = Intent(applicationContext, Notification::class.java)
        val title = "Anúncio Chegando!"
        val message = anuncio.titulo

        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime(days)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )

        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        Log.i(
            "infoteste",
            "scheduleNotification: At:  ${dateFormat.format(date)} -- ${timeFormat.format(date)}"
        )
    }

    private fun getTime(days: Int): Long {
        val calendar = setTime()!!
        Log.i("infoteste", "days: $days")

        calendar.add(Calendar.SECOND, 5)
        return calendar.timeInMillis

//        val calendarPrazo = Calendar.getInstance()
//        calendarPrazo.add(Calendar.SECOND, 5)
//        return calendarPrazo.timeInMillis
    }

    private fun setTime(): Calendar? {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val date = formatter.parse(dataSelecionada)

        val calendar = Calendar.getInstance()
        calendar.time = date!!

        val calendarNow = Calendar.getInstance()
        val hour = calendarNow.get(Calendar.HOUR_OF_DAY)
        val minute = calendarNow.get(Calendar.MINUTE)
        val second = calendarNow.get(Calendar.SECOND)

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        calendar.set(year, month, day, hour, minute, second)
        return calendar
    }


}