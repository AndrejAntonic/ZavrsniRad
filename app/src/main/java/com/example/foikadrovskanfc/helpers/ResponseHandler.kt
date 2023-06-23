package com.example.foikadrovskanfc.helpers

import com.example.foikadrovskanfc.entities.Personnel
import com.example.foikadrovskanfc.entities.UserResponse

class ResponseHandler {

    fun convertResponseToPersonnelList(responseBody: UserResponse): MutableList<Personnel> {
        val personnelList: MutableList<Personnel> = mutableListOf()
        for (personnel in responseBody.member) {
            val lastIndex = personnel.nameWithTitleHr.lastIndexOf('.')
            val personnelToAdd = if (lastIndex != -1) {
                Personnel(
                    personnel.id,
                    personnel.nameWithTitleHr,
                    //personnel.nameWithTitleHr.substring(0, lastIndex) + ".",
                    personnel.firstName,
                    personnel.lastName
                )
            } else {
                Personnel(
                    personnel.id,
                    "",
                    personnel.firstName,
                    personnel.lastName
                )
            }
            personnelList.add(personnelToAdd)
        }

        return personnelList
    }

}