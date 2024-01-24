package com.hallen.genesismanager.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hallen.genesismanager.ui.fragments.CalendarFragment

class PagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle

): FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int = 1

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> CalendarFragment()
            1 -> CalendarFragment()
            2 -> CalendarFragment()
            else -> CalendarFragment()
        }

    }


}