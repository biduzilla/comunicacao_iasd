package com.toddy.comunicacaodemandas.ui.activity.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.toddy.comunicacaodemandas.ID_ANUNCIO
import com.toddy.comunicacaodemandas.databinding.FragmentAnuncioTarefasBinding
import com.toddy.comunicacaodemandas.extensions.iniciaActivity
import com.toddy.comunicacaodemandas.modelo.Anuncio
import com.toddy.comunicacaodemandas.ui.activity.DetalhesAnuncioActivity
import com.toddy.comunicacaodemandas.ui.activity.FormAnuncioActivity
import com.toddy.comunicacaodemandas.ui.adapter.AnuncioAdapter
import com.toddy.comunicacaodemandas.webClient.AnuncioFirebase

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
        AnuncioFirebase().recuperaAnuncios { anunciosList ->
            anunciosList?.let {
                val anuncioTarefas = mutableListOf<Anuncio>()

                anunciosList.forEach { anuncio ->

                    if (!anuncio.finalizado)
                        anuncioTarefas.add(anuncio)
                }
                adapter.atualiza(anuncioTarefas)

                binding.progressBar.visibility = View.GONE
                if (anuncioTarefas.isEmpty()){
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
