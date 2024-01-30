package com.hallen.genesismanager.data.repository

import com.hallen.genesismanager.data.database.dao.EventDao
import com.hallen.genesismanager.data.database.entities.EventEntity
import com.hallen.genesismanager.domain.model.Event
import com.hallen.genesismanager.domain.model.toDomain
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val eventDao: EventDao
) {
    suspend fun getAllEvents(): List<Event>{
        val response = eventDao.getAllEvents()
        return response.map { it.toDomain() }
    }

    suspend fun getAllEvents(fecha: String): List<Event> {
        val response = eventDao.getAllEvents(fecha)
        return response.map { it.toDomain() }
    }

    suspend fun insertEvent(event: EventEntity){
        if (event.id != 0) eventDao.update(event) else eventDao.insert(event)
    }

    suspend fun insertAllEvents(events: List<EventEntity>){
        eventDao.insertAll(events)
    }

    suspend fun updateEvent(event: EventEntity) = eventDao.update(event)

    suspend fun deleteEvent(id: Int) = eventDao.delete(id)
}