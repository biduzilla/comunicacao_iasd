package com.toddy.comunicacaodemandas.ui.activity.fragments

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.toddy.comunicacaodemandas.ID_ANUNCIO
import com.toddy.comunicacaodemandas.databinding.FragmentAnuncioTarefasBinding
import com.toddy.comunicacaodemandas.extensions.iniciaActivity
import com.toddy.comunicacaodemandas.modelo.Anuncio
import com.toddy.comunicacaodemandas.notification.*
import com.toddy.comunicacaodemandas.ui.activity.DetalhesAnuncioActivity
import com.toddy.comunicacaodemandas.ui.activity.FormAnuncioActivity
import com.toddy.comunicacaodemandas.ui.adapter.AnuncioAdapter
import com.toddy.comunicacaodemandas.webClient.AnuncioFirebase
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AnuncioTarefasFragment : Fragment() {

    private var _binding: FragmentAnuncioTarefasBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy {
        AnuncioAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnuncioTarefasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configClicks()
        configRv()
        createNotificationChannel()
    }

    override fun onResume() {
        super.onResume()

        buscaAnuncios()
    }

    private fun buscaAnuncios() {
        AnuncioFirebase().recuperaAnuncios(adapter = adapter) { anunciosList ->
            anunciosList?.let {
                var anuncioTarefas = mutableListOf<Anuncio>()

                anunciosList.forEach { anuncio ->

                    if (!anuncio.finalizado)
                        anuncioTarefas.add(anuncio)
                }

                anuncioTarefas = anuncioTarefas.toSet().toMutableList()

                anuncioTarefas.forEach { anuncio ->
                    if (!anuncio.notificacao) {
                        val daysDiff = diferencaEntreDatas(anuncio)
                        marcarDatasNotificacao(daysDiff, anuncio)
                    }
                }
                adapter.atualiza(emptyList())
                adapter.atualiza(anuncioTarefas)

                binding.progressBar.visibility = View.GONE
                if (anuncioTarefas.isEmpty()) {
                    binding.llInfo.visibility = View.VISIBLE
                    binding.rv.visibility = View.GONE
                } else {
                    binding.llInfo.visibility = View.GONE
                    binding.rv.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun diferencaEntreDatas(anuncio: Anuncio): Int {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val date = formatter.parse(anuncio.prazo!!)
        date?.let {
            calendar.time = it
        }

        val msDiff: Long =
            calendar.timeInMillis - Calendar.getInstance().timeInMillis
        val daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff).toInt()
        return daysDiff + 1
    }

    private fun marcarDatasNotificacao(daysDiff: Int, anuncio: Anuncio) {
        var i: Int = if (daysDiff < 7) {
            daysDiff
        } else {
            7
        }
        repeat(i) {
            scheduleNotification(anuncio, i)
            i--
        }
    }

    private fun scheduleNotification(anuncio: Anuncio, days: Int) {
        val intent = Intent(requireActivity().applicationContext, Notification::class.java)
        val title = "Anúncio Chegando!"
        val message = "Você tem até dia ${anuncio.prazo} para finalizar a demanda: ${anuncio.titulo}"

        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            requireActivity().applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime(days, anuncio)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )

        val date = Date(time)
        val dateFormat =
            android.text.format.DateFormat.getLongDateFormat(requireActivity().applicationContext)
        val timeFormat =
            android.text.format.DateFormat.getTimeFormat(requireActivity().applicationContext)

        Log.i(
            "infoteste",
            "scheduleNotification: At:  ${dateFormat.format(date)} -- ${timeFormat.format(date)}"
        )
        anuncio.notificacao = true
        AnuncioFirebase().salvarAnuncio(anuncio)
    }

    private fun getTime(days: Int, anuncio: Anuncio): Long {
        var calendar = setTime(anuncio)!!

        Log.i("infoteste", "days: $days")

        calendar.add(Calendar.SECOND, 5)
        if (days > 3) {
            calendar.add(Calendar.DAY_OF_MONTH, -3)
        } else {
            val calendarAtual = Calendar.getInstance()
            calendar = calendarAtual
        }

        return calendar.timeInMillis

//        val calendarPrazo = Calendar.getInstance()
//        calendarPrazo.add(Calendar.SECOND, 5)
//        return calendarPrazo.timeInMillis
    }

    private fun setTime(anuncio: Anuncio): Calendar? {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val date = formatter.parse(anuncio.prazo!!)

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

    private fun createNotificationChannel() {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager =
            requireActivity().getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    private fun configRv() {
        with(binding) {
            val rv = rv
            rv.adapter = adapter
            rv.layoutManager = LinearLayoutManager(requireContext())
            adapter.onClick = {
                Intent(requireContext(), DetalhesAnuncioActivity::class.java).apply {
                    putExtra(ID_ANUNCIO, it.id)
                    startActivity(this)
                }
            }
        }
    }

    private fun configClicks() {
        binding.fabAdd.setOnClickListener {
            requireActivity().iniciaActivity(FormAnuncioActivity::class.java)
        }
    }
}
