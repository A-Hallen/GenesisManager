package com.hallen.genesismanager.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hallen.genesismanager.Aplication.Companion.prefs
import com.hallen.genesismanager.domain.model.WorkCraft

class PreferenceViewModel : ViewModel() {
    val worksModel = MutableLiveData<MutableSet<WorkCraft>>()

    fun addWork(workCraft: WorkCraft) {
        prefs.addNewWorkCraft(workCraft)
        val works = worksModel.value
        works?.add(workCraft)
        works?.let { worksModel.value = it }
    }


    fun removeWork(workCraft: WorkCraft) {
        prefs.deleteWorkCraft(workCraft)
        val works = worksModel.value
        works?.remove(workCraft)
        works?.let { worksModel.value = it }
    }

    fun getWorks() {
        worksModel.value = prefs.getWorkCraft()
    }
}