package com.example.umarmasjid

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.umarmasjid.app.UmarMasjidRoot
import com.example.umarmasjid.domain.repository.AuthState
import com.example.umarmasjid.ui.theme.UmarMasjidTheme
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    private val resolutionLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel.fetchLocationOnce()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val darkMode by viewModel.darkMode.collectAsStateWithLifecycle()
            val isLocationInitialized by viewModel.isLocationInitialized.collectAsStateWithLifecycle()
            val authState by viewModel.authState.collectAsStateWithLifecycle()

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                if (granted) {
                    checkAndEnableLocation {
                        viewModel.fetchLocationOnce()
                    }
                }
            }

            // Only request location if we know for sure it's not initialized (is false, not null)
            LaunchedEffect(isLocationInitialized) {
                if (isLocationInitialized == false) {
                    requestLocation(permissionLauncher)
                }
            }

            if (authState != AuthState.LOADING) {
                UmarMasjidTheme(darkMode) {
                    UmarMasjidRoot(
                        isLoggedIn = authState == AuthState.AUTHENTICATED,
                        onLogout = viewModel::logout
                    )
                }
            }
        }
    }

    private fun requestLocation(
        launcher: ActivityResultLauncher<Array<String>>
    ) {
        val fineGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            checkAndEnableLocation {
                viewModel.fetchLocationOnce()
            }
        } else {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkAndEnableLocation(onEnabled: () -> Unit) {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_LOW_POWER,
            1000
        ).build()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(request)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { onEnabled() }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution.intentSender).build()
                    resolutionLauncher.launch(intentSenderRequest)
                } catch (e: Exception) {
                    // Ignore or log
                }
            }
        }
    }
}