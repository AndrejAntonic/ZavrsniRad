package com.example.foikadrovskanfc.helpers

import com.example.foikadrovskanfc.entities.Personnel

object MockDataLoader {
    fun getDemoData(): List<Personnel> = listOf(
        Personnel(1, "Doc. dr. sc.", "Boris", "Tomaš"),
        Personnel(2, "Dr. sc.", "Marko", "Mijač"),
        Personnel(3, "Prof. dr. sc." , "Vjeran", "Strahonja"),
        Personnel(4, "Mag. inf.", "Elvis", "Popović"),
        Personnel(5, "Izv. prof. dr. sc.", "Zlatko", "Stapić"),
        Personnel(6, "Prof. dr. sc.", "Kornelije", "Rabuzin"),
        Personnel(7, "Mag. educ. phys. et inf.", "Miljenko", "Novaković"),
        Personnel(8, "Prof. dr. sc.", "Mirko", "Maleković"),
        Personnel(9, "Prof. dr. sc.", "Dragutin", "Kermek")
    )
}