package com.money.randing.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.money.randing.constant.K
import com.money.randing.data.db.dao.MovementDao
import com.money.randing.data.db.dao.PersonDao
import com.money.randing.data.db.entities.DBMovement
import com.money.randing.data.db.entities.DBPerson

@Database(
    version = 1,
    exportSchema = false,
    entities = [DBPerson::class, DBMovement::class],
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun personDao(): PersonDao

    abstract fun movementDao(): MovementDao

    companion object {
        fun create(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            K.DATABASE_NAME
        ).build()
    }
}