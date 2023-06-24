package com.example.foikadrovskanfc.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "personnel")
data class Personnel(
    @PrimaryKey val id : Int,
    val title : String,
    val firstName : String,
    val lastName : String
) : Serializable
