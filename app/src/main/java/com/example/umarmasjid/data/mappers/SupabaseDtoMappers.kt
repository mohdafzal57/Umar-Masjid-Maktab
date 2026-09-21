package com.example.umarmasjid.data.mappers

import com.example.umarmasjid.data.dto.AttendanceDto
import com.example.umarmasjid.data.dto.PaymentDto
import com.example.umarmasjid.data.dto.StudentDto
import com.example.umarmasjid.data.local_database.model.AttendanceEntity
import com.example.umarmasjid.data.local_database.model.AttendanceStatus
import com.example.umarmasjid.data.local_database.model.PaymentEntity
import com.example.umarmasjid.data.local_database.model.StudentEntity
import java.time.Instant
import java.time.LocalDate

fun StudentEntity.toDto(): StudentDto {
    return StudentDto(
        id = id,
        teacherId = teacherId,
        name = name,
        phoneNumber = phoneNumber,
        enrollmentDate = enrollmentDate.toString(), // Produces "YYYY-MM-DD"
        createdAt = Instant.ofEpochMilli(createdAt).toString(), // Produces ISO-8601 UTC
        updatedAt = Instant.ofEpochMilli(updatedAt).toString(),
        deletedAt = deletedAt?.let { Instant.ofEpochMilli(it).toString() }
    )
}

fun StudentDto.toEntity(): StudentEntity {
    return StudentEntity(
        id = id,
        teacherId = teacherId,
        name = name,
        phoneNumber = phoneNumber,
        enrollmentDate = LocalDate.parse(enrollmentDate),
        createdAt = Instant.parse(createdAt).toEpochMilli(),
        updatedAt = Instant.parse(updatedAt).toEpochMilli(),
        deletedAt = deletedAt?.let { Instant.parse(it).toEpochMilli() }
    )
}

fun AttendanceEntity.toDto(): AttendanceDto {
    return AttendanceDto(
        id = id,
        studentId = studentId,
        attendanceDate = attendanceDate.toString(),
        attendanceStatus = attendanceStatus.name,
        createdAt = Instant.ofEpochMilli(createdAt).toString(), // Produces ISO-8601 UTC
        updatedAt = Instant.ofEpochMilli(updatedAt).toString(),
        deletedAt = deletedAt?.let { Instant.ofEpochMilli(it).toString() }
    )
}

fun AttendanceDto.toEntity(): AttendanceEntity {
    return AttendanceEntity(
        id = id,
        studentId = studentId,
        attendanceDate = LocalDate.parse(attendanceDate),
        attendanceStatus = AttendanceStatus.valueOf(attendanceStatus),
        createdAt = Instant.parse(createdAt).toEpochMilli(),
        updatedAt = Instant.parse(updatedAt).toEpochMilli(),
        deletedAt = deletedAt?.let { Instant.parse(it).toEpochMilli() }
    )
}

fun PaymentEntity.toDto(): PaymentDto {
    return PaymentDto(
        id = id,
        studentId = studentId,
        paymentDate = paymentDate.toString(),
        amount = amount,
        note = note,
        createdAt = Instant.ofEpochMilli(createdAt).toString(), // Produces ISO-8601 UTC
        updatedAt = Instant.ofEpochMilli(updatedAt).toString(),
        deletedAt = deletedAt?.let { Instant.ofEpochMilli(it).toString() }
    )
}

fun PaymentDto.toEntity(): PaymentEntity {
    return PaymentEntity(
        id = id,
        studentId = studentId,
        paymentDate = LocalDate.parse(paymentDate),
        amount = amount,
        note = note,
        createdAt = Instant.parse(createdAt).toEpochMilli(),
        updatedAt = Instant.parse(updatedAt).toEpochMilli(),
        deletedAt = deletedAt?.let { Instant.parse(it).toEpochMilli() }
    )
}
