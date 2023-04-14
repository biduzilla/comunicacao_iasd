package com.toddy.comunicacaodemandas.ui.activity.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.toddy.comunicacaodemandas.ID_ANUNCIO
import com.toddy.comunicacaodemandas.database.AppDatabase
import com.toddy.comunicacaodemandas.databinding.FragmentAnuncioTarefasBinding
import com.toddy.comunicacaodemandas.extensions.Toast
import com.toddy.comunicacaodemandas.extensions.iniciaActivity
import com.toddy.comunicacaodemandas.modelo.Anuncio
import com.toddy.comunicacaodemandas.ui.activity.DetalhesAnuncioActivity
import com.toddy.comunicacaodemandas.ui.activity.FormAnuncioActivity
import com.toddy.comunicacaodemandas.ui.adapter.AnuncioAdapter
import com.toddy.comunicacaodemandas.webClient.AnuncioFirebase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.log

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

    }

    override fun onResume() {
        super.onResume()
        buscaAnuncios()
    }

    private fun buscaAnuncios() {
        binding.progressBar.visibility = View.GONE
        AnuncioFirebase().recuperaAnuncios { anunciosList ->
            anunciosList?.let {
                val anuncioTarefas = mutableListOf<Anuncio>()

                anunciosList.forEach { anuncio ->

                    if (!anuncio.finalizado)
                        anuncioTarefas.add(anuncio)
                }
                adapter.atualiza(anuncioTarefas)
                binding.rv.visibility = View.VISIBLE
            }
        }
    }


    private fun configRv() {
        with(binding) {
            val rv = rv
            rv.adapter = adapter
            rv.layoutManager = LinearLayoutManager(requireContext())
            adapter.onClick = {
                Intent(requireContext(),DetalhesAnuncioActivity::class.java).apply {
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
