package com.example.umarmasjid.data.networking.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SoftDeletePayload(
    @SerialName("deleted_at") val deletedAt: String,
    @SerialName("updated_at") val updatedAt: String
)

/***
* Remote Procedure Call
***/
@Serializable
data class SoftDeleteStudentParamsForRPC(
    @SerialName("p_student_id") val studentId: String,
    @SerialName("p_timestamp") val timestamp: String
)

