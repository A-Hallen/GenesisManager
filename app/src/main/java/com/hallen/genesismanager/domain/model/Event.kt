package com.hallen.genesismanager.domain.model

import com.hallen.genesismanager.data.database.entities.EventEntity


data class Event(
    var id: Int = 0,
    var time: String,
    var hora: String,
    var title: String = "",
    var details: String = "",
    var tipo: String,
    var notificar: Boolean
)

fun EventEntity.toDomain() = Event(
    id,
    time,
    hora,
    title,
    details,
    tipo,
    notificar
)
