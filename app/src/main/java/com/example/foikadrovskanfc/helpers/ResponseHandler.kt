package com.example.foikadrovskanfc.helpers

import com.example.foikadrovskanfc.entities.Personnel
import com.example.foikadrovskanfc.entities.UserResponse

class ResponseHandler {

    fun convertResponseToPersonnelList(responseBody: UserResponse): MutableList<Personnel> {
        val personnelList = mutableListOf<Personnel>()

        for (personnel in responseBody.member) {
            val title = extractTitle(personnel.nameWithTitleHr)
            val personnelToAdd = Personnel(personnel.id, title, personnel.firstName, personnel.lastName)
            personnelList.add(personnelToAdd)
        }

        return personnelList
    }

    private fun extractTitle(nameWithTitleHr: String): String {
        val containsComma = nameWithTitleHr.contains(",")
        return if (containsComma) {
            val lastIndex = nameWithTitleHr.lastIndexOf(',')
            val title = nameWithTitleHr.substring(lastIndex + 1).trim().replaceFirstChar { it.uppercase() }
            title
        } else {
            val lastIndex = nameWithTitleHr.lastIndexOf('.')
            val title = if (lastIndex != -1) nameWithTitleHr.substring(0, lastIndex) + "." else ""
            title
        }
    }
}
