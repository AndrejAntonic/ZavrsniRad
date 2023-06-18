package com.example.foikadrovskanfc.entities

import java.io.Serializable

data class Personnel(
    val id : Int,
    val title : String,
    val firstName : String,
    val lastName : String,
    var isChecked: Boolean = false
) : Serializable
