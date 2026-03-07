package com.example.umarmasjid.ui.add_payment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.umarmasjid.data.local_database.model.StudentEntity

// In your navigation logic
@Composable
fun PaymentNavigationHandler(
    student: StudentEntity,
    onBack: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }

//    LaunchedEffect(Unit) {
//        // Simulate loading time
//        delay(3000)
//        isLoading = false
//    }
//
//    if (isLoading) {
//        PaymentLoadingScreen(
//            studentName = student.name,
//            onBack = onBack
//        )
//    } else {
//        AddPaymentScreen(
//            student = student,
//            // ... other parameters
//        )
//    }
}