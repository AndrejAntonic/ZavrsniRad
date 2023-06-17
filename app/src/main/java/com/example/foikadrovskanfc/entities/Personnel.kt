package com.example.foikadrovskanfc.entities

data class Personnel(
    val id : Int,
    val title : String,
    val firstName : String,
    val lastName : String,
    var isChecked: Boolean = false
)
