package com.example.umarmasjid.data.local_database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["student_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("student_id")]
)
data class PaymentEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "payment_date")
    val paymentDate: Long,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "note")
    val note: String? = null
)
