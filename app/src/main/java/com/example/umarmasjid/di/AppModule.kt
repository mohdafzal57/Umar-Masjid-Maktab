package com.example.umarmasjid.di

import android.content.Context
import androidx.room.Room
import com.example.umarmasjid.core.data.networking.HttpClientFactory
import com.example.umarmasjid.data.local_database.AppDatabase
import com.example.umarmasjid.data.local_database.dao.AttendanceDao
import com.example.umarmasjid.data.local_database.dao.PaymentDao
import com.example.umarmasjid.data.local_database.dao.StudentDao
import com.example.umarmasjid.repository.AttendanceRepository
import com.example.umarmasjid.repository.PaymentRepository
import com.example.umarmasjid.repository.QuickOverviewRepository
import com.example.umarmasjid.repository.StudentRepository
import com.example.umarmasjid.util.Constants.UMAR_MASJID_DATABASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClientFactory.create(CIO.create())
    }

    // Provide Database
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext app: Context): AppDatabase =
        Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            UMAR_MASJID_DATABASE
        )
            .build()

    // Provide DAOs
    @Provides
    fun provideStudentDao(db: AppDatabase): StudentDao = db.studentDao()

    @Provides
    fun provideAttendanceDao(db: AppDatabase): AttendanceDao = db.attendanceDao()

    @Provides
    fun provideFeeDao(db: AppDatabase): PaymentDao = db.feeDao()

    // Provide Repositories
    @Provides
    @Singleton
    fun provideStudentRepository(
        studentDao: StudentDao
    ): StudentRepository =
        StudentRepository(
            studentDao = studentDao
        )

    @Provides
    @Singleton
    fun provideAttendanceRepository(dao: AttendanceDao): AttendanceRepository =
        AttendanceRepository(dao)

    @Provides
    @Singleton
    fun provideFeeRepository(dao: PaymentDao): PaymentRepository =
        PaymentRepository(dao)

    @Provides
    @Singleton
    fun provideQuickOverviewRepository(
        studentDao: StudentDao,
        attendanceDao: AttendanceDao,
        paymentDao: PaymentDao
    ): QuickOverviewRepository =
        QuickOverviewRepository(
            studentDao = studentDao,
            attendanceDao = attendanceDao,
            paymentDao = paymentDao
        )
}
