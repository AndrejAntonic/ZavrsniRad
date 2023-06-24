package com.example.foikadrovskanfc.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.foikadrovskanfc.entities.Personnel

@Database(version = 1, entities = [Personnel::class], exportSchema = false)
abstract class PersonnelDatabase: RoomDatabase() {
    abstract fun getPersonnelDAO(): PersonnelDAO

    companion object {
        @Volatile
        private var implementedInstance: PersonnelDatabase? = null

        fun getInstance(): PersonnelDatabase {
            if(implementedInstance == null)
                throw NullPointerException("Database instance has not yet been created!")
            return implementedInstance!!
        }

        fun buildInstance(context: Context) {
            if(implementedInstance == null) {
                val instanceBuilder =
                    Room.databaseBuilder(context, PersonnelDatabase::class.java, "personnel.db")
                instanceBuilder.fallbackToDestructiveMigration()
                instanceBuilder.allowMainThreadQueries()
                instanceBuilder.build()

                implementedInstance = instanceBuilder.build()
            }
        }
    }
}