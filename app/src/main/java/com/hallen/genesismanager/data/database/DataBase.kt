package com.hallen.genesismanager.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hallen.genesismanager.data.database.dao.EventDao
import com.hallen.genesismanager.data.database.entities.EventEntity


@Database(entities = [
        EventEntity::class,
                     ], version = 1
)
abstract class DataBase: RoomDatabase() {
    abstract fun getEventDao(): EventDao
}
