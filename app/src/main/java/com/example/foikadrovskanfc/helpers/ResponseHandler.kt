package com.example.foikadrovskanfc.helpers

import androidx.compose.ui.text.capitalize
import com.example.foikadrovskanfc.entities.Personnel
import com.example.foikadrovskanfc.entities.UserResponse

class ResponseHandler {

    fun convertResponseToPersonnelList(responseBody: UserResponse): MutableList<Personnel> {
        val personnelList: MutableList<Personnel> = mutableListOf()
        var title: String
        var lastIndex: Int
        for (personnel in responseBody.member) {
            val containsComma = personnel.nameWithTitleHr.contains(",")
            if(containsComma) {
                lastIndex = personnel.nameWithTitleHr.lastIndexOf(',')
                title = personnel.nameWithTitleHr.substring(lastIndex + 1)
                title = title.removePrefix(" ")
                title = title.replaceFirstChar { it.uppercase() }
            }
            else{
                lastIndex = personnel.nameWithTitleHr.lastIndexOf('.')
                title = if(lastIndex != -1)
                        personnel.nameWithTitleHr.substring(0, lastIndex) + "."
                    else
                        ""
            }
            val personnelToAdd = Personnel(personnel.id, title, personnel.firstName, personnel.lastName)
            personnelList.add(personnelToAdd)
        }

        return personnelList
    }

}