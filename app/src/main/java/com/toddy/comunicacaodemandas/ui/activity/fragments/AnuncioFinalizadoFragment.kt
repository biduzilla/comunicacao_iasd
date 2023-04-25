package com.toddy.comunicacaodemandas.ui.activity.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.toddy.comunicacaodemandas.ID_ANUNCIO
import com.toddy.comunicacaodemandas.IS_FINALIZADO
import com.toddy.comunicacaodemandas.databinding.FragmentAnuncioFinalizadoBinding
import com.toddy.comunicacaodemandas.extensions.iniciaActivity
import com.toddy.comunicacaodemandas.modelo.Anuncio
import com.toddy.comunicacaodemandas.ui.activity.DetalhesAnuncioActivity
import com.toddy.comunicacaodemandas.ui.activity.FormAnuncioActivity
import com.toddy.comunicacaodemandas.ui.adapter.AnuncioFinalizadoAdapter
import com.toddy.comunicacaodemandas.webClient.AnuncioFirebase

class AnuncioFinalizadoFragment : Fragment() {

    private var _binding: FragmentAnuncioFinalizadoBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy {
        AnuncioFinalizadoAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnuncioFinalizadoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configRv()
    }

    override fun onResume() {
        super.onResume()
        buscaAnuncios()
    }

    private fun buscaAnuncios() {

        AnuncioFirebase().recuperaAnuncios(adapterFinalizado = adapter) { anunciosList ->
            anunciosList?.let {
                val anuncioFinalizados = mutableListOf<Anuncio>()

                anunciosList.forEach { anuncio ->

                    if (anuncio.finalizado)
                        anuncioFinalizados.add(anuncio)
                }
                adapter.atualiza(anuncioFinalizados)
                binding.progressBar.visibility = View.GONE
                if (anuncioFinalizados.isEmpty()){
                    binding.llInfo.visibility = View.VISIBLE
                    binding.rv.visibility = View.GONE
                }else{
                    binding.llInfo.visibility = View.GONE
                    binding.rv.visibility = View.VISIBLE
                }

            }
        }
    }


    private fun configRv() {
        with(binding) {
            val rv = rv
            rv.adapter = adapter
            rv.layoutManager = LinearLayoutManager(requireContext())
            adapter.onClick = {
                Intent(requireContext(), DetalhesAnuncioActivity::class.java).apply {
                    putExtra(IS_FINALIZADO, true)
                    putExtra(ID_ANUNCIO, it.id)
                    startActivity(this)
                }
            }
        }
    }
}