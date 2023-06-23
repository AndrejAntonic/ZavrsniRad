package com.example.foikadrovskanfc.entities

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("@context")
    val context: String,
    @SerializedName("@id")
    val id: String,
    @SerializedName("@type")
    val type: String,
    @SerializedName("hydra:member")
    val member: List<User>
)

data class User(
    val id: Int,
    val username: String,
    val firstName: String,
    val lastName: String,
    val relation: Relation,
    val status: Int,
    val sex: String,
    val email: String,
    val tel: String,
    val photoUrl: String,
    val photo300Url: String?,
    val photo600Url: String?,
    val scientistId: String,
    val memberOfFacultyCouncil: Boolean,
    val memberOfAssistantsAssociation: Boolean,
    val alias: String,
    val employeeLocations: List<EmployeeLocation>,
    val employeeDepartments: List<EmployeeDepartment>,
    val nameWithTitleHr: String,
    val nameWithTitleEn: String,
    val lastContract: Contract,
    val activeEmployeeDepartments: List<EmployeeDepartment>
)

data class Relation(
    val id: Int,
    val name: String
)

data class EmployeeLocation(
    val room: Room
)

data class Room(
    val id: Int,
    val roomType: RoomType,
    val floorWing: FloorWing,
    val number: String,
    val name: String,
    val nameEng: String,
    val capacity: Int?
)

data class RoomType(
    val id: Int,
    val name: String,
    val nameEng: String
)

data class FloorWing(
    val building: Building,
    val floorNumber: Int,
    val floorName: String,
    val floorNameEng: String,
    val wing: String,
    val wingEng: String
)

data class Building(
    val id: Int,
    val name: String,
    val nameEng: String
)

data class EmployeeDepartment(
    val id: Int,
    val department: Department,
    val role: DepartmentRole,
    val startDate: String,
    val endDate: String?,
    val percentage: Int
)

data class Department(
    val id: Int,
    val name: String,
    val nameEng: String,
    val type: DepartmentType
)

data class DepartmentType(
    val id: Int,
    val name: String,
    val nameEng: String
)

data class DepartmentRole(
    val id: Int,
    val name: String,
    val nameEng: String
)

data class Contract(
    val id: Int,
    val type: Int,
    val startDate: String,
    val endDate: String?,
    val affiliation: Int,
    val contractWorkingPlaces: List<ContractWorkingPlace>,
    val contractPositions: List<Any>
)

data class ContractWorkingPlace(
    val workingPlace: WorkingPlace,
    val startDate: String,
    val endDate: String?
)

data class WorkingPlace(
    val name: String,
    val nameFemale: String,
    val nameEng: String,
    val vocation: Vocation
)

data class Vocation(
    val id: Int,
    val name: String
)
