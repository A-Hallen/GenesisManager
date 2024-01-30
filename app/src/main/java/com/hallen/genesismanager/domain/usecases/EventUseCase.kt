package com.hallen.genesismanager.domain.usecases

import com.hallen.genesismanager.data.database.entities.toDataBase
import com.hallen.genesismanager.data.repository.EventRepository
import com.hallen.genesismanager.domain.model.Event
import javax.inject.Inject

class EventUseCase @Inject constructor(private val repository: EventRepository) {
    suspend fun loadAllEvents(actualDate: String): List<Event> {
        return repository.getAllEvents(actualDate)
    }

    suspend fun insertEvent(event: Event) {
        repository.insertEvent(event.toDataBase())
    }

    suspend fun deleteEvent(id: Int) {
        repository.deleteEvent(id)
    }
}