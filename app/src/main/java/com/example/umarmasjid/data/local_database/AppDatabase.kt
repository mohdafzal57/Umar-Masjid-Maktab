package com.example.umarmasjid.data.local_database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.umarmasjid.data.local_database.dao.AttendanceDao
import com.example.umarmasjid.data.local_database.dao.PaymentDao
import com.example.umarmasjid.data.local_database.dao.StudentDao
import com.example.umarmasjid.data.local_database.dao.SyncCursorDao
import com.example.umarmasjid.data.local_database.dao.SyncOperationDao
import com.example.umarmasjid.data.local_database.model.AttendanceEntity
import com.example.umarmasjid.data.local_database.model.PaymentEntity
import com.example.umarmasjid.data.local_database.model.StudentEntity
import com.example.umarmasjid.data.local_database.model.SyncCursorEntity
import com.example.umarmasjid.data.local_database.model.SyncOperationEntity

@Database(
    entities = [
        StudentEntity::class,
        AttendanceEntity::class,
        PaymentEntity::class,
        SyncOperationEntity::class,
        SyncCursorEntity::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun feeDao(): PaymentDao
    abstract fun syncOperationDao(): SyncOperationDao
    abstract fun syncCursorDao(): SyncCursorDao
}
