package com.kodego.app.istak_inventory.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kodego.app.istak_inventory.converters.Converters

@Database(
    entities = [Item::class],
    version = 1
)
@TypeConverters(Converters::class)

abstract class IstakDatabase: RoomDatabase() {
    abstract fun getItems(): ItemDao

    companion object{
        @Volatile
        private var instance: IstakDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance?: synchronized(LOCK){
            instance?: buildDatabase(context).also{
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            IstakDatabase::class.java,
            "istak"
        ).build()
    }
}