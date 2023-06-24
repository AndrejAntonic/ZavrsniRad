package com.example.foikadrovskanfc.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.foikadrovskanfc.entities.Personnel

@Dao
interface PersonnelDAO {
    @Query("SELECT * FROM personnel")
    fun getAllPersonnel(): List<Personnel>

    @Insert(onConflict = REPLACE)
    fun insertPersonnel(personnelList: List<Personnel>): List<Long>

    @Query("DELETE FROM personnel")
    fun deleteAllPersonnel()
}