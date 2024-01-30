package com.hallen.genesismanager.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hallen.genesismanager.domain.model.Event
import com.hallen.genesismanager.domain.usecases.EventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventUseCase: EventUseCase,
): ViewModel(){

    private var actualDate: String = ""

    val eventModel = MutableLiveData<List<Event>>()
    val dayEvents  = MutableLiveData<List<Event>>()

    fun getEvents(){
        CoroutineScope(Dispatchers.IO).launch {
            val events = eventUseCase.loadAllEvents(actualDate)
            eventModel.postValue(events)
        }
    }

    fun getEvents(actualDate: String) {
        this.actualDate = actualDate
        CoroutineScope(Dispatchers.IO).launch {
            val events = eventUseCase.loadAllEvents(actualDate)
            dayEvents.postValue(events)
        }
    }

    fun saveEvent(event: Event){
        CoroutineScope(Dispatchers.IO).launch {
            eventUseCase.insertEvent(event)
            getEvents()
            getEvents(actualDate)
        }
    }

    fun deleteEvent(id: Int){
        CoroutineScope(Dispatchers.IO).launch {
            eventUseCase.deleteEvent(id)
            getEvents()
            getEvents(actualDate)
        }
    }

}