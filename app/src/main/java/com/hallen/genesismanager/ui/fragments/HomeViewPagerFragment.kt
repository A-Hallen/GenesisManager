package com.hallen.genesismanager.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.genesismanager.R
import com.example.genesismanager.databinding.FragmentHomeViewPagerBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.hallen.genesismanager.ui.adapters.PagerAdapter

class HomeViewPagerFragment : Fragment() {
    private lateinit var binding: FragmentHomeViewPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeViewPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadHome()
    }

    //Carga la página de inicio
    private fun loadHome() {
        val fragmentsList = listOf("Calendario") //Crea una lista de los fragmentos

       val homeAdapter = PagerAdapter(requireActivity().supportFragmentManager, lifecycle) //Crea un adaptador para el pager
        binding.pager.offscreenPageLimit = 2
        binding.pager.adapter = homeAdapter
        //Crea un mediador para el tablayout y el pager

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.setCustomView(R.layout.tab_custom_view)
            tab.text = fragmentsList[position] //Establece el texto para el tab
        }.attach()
    }

}