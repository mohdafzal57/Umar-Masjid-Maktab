package com.example.umarmasjid

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.umarmasjid.ui.settings.ContactInfo
import com.example.umarmasjid.ui.settings.ImportUiState
import com.example.umarmasjid.ui.settings.SettingsUiState
import com.example.umarmasjid.ui.settings.SettingsViewModel
import com.example.umarmasjid.ui.theme.UmarMasjidTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UmarMasjidTheme {
                SettingsScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val importState by viewModel.importState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    /***
     * You use it when you need to:
     * Pick an image from gallery
     * Request runtime permissions
     * Open camera
     * Open another activity and get data back
     * Use system contracts (files, contacts, etc.)*/
    val contactPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.importStudentsFromContacts(context)
        } else {
            Toast.makeText(context, "Permission denied to read contacts", Toast.LENGTH_SHORT).show()
        }
    }

    // Generic launcher for different file types
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("*/*")
    ) { uri: Uri? ->
        uri?.let {
            val state = uiState
            if (state is SettingsUiState.Success) {
                saveDataToFile(context, it, state.data)
                viewModel.resetState()
            }
        } ?: viewModel.resetState()
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is SettingsUiState.Success -> {
                val state = uiState as SettingsUiState.Success
                createDocumentLauncher.launch(state.fileName)
            }
            is SettingsUiState.Error -> {
                Toast.makeText(context, (uiState as SettingsUiState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    LaunchedEffect(importState) {
        if (importState is ImportUiState.Success) {
            Toast.makeText(context, (importState as ImportUiState.Success).message, Toast.LENGTH_LONG).show()
            viewModel.resetImportState()
        } else if (importState is ImportUiState.Error) {
            Toast.makeText(context, (importState as ImportUiState.Error).message, Toast.LENGTH_LONG).show()
            viewModel.resetImportState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Data Management",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
            )

            SettingsItem(
                title = "Download as CSV (Excel)",
                description = "Export all students as a spreadsheet file",
                icon = Icons.Default.Description,
                onClick = { viewModel.exportAsCsv() },
                isLoading = uiState is SettingsUiState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsItem(
                title = "Download as VCF (Contacts)",
                description = "Export all students as a contact card file",
                icon = Icons.Default.Download, // Changed from Vcard which might not exist in standard Icons
                onClick = { viewModel.exportAsVcf() },
                isLoading = uiState is SettingsUiState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsItem(
                title = "Import from Contacts",
                description = "Select multiple contacts to register as students",
                icon = Icons.Default.Contacts,
                onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                        viewModel.importStudentsFromContacts(context)
                    } else {
                        contactPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                    }
                },
                isLoading = importState is ImportUiState.Loading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "App Information",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
            )

            SettingsItem(
                title = "Version",
                description = "1.0.0",
                icon = Icons.Default.Info,
                onClick = { /* Info action */ }
            )
        }
    }

    if (importState is ImportUiState.ShowContactPicker) {
        val contacts = (importState as ImportUiState.ShowContactPicker).contacts
        ContactPickerDialog(
            contacts = contacts,
            onDismiss = { viewModel.resetImportState() },
            onConfirm = { selectedContacts ->
                viewModel.addSelectedStudents(selectedContacts)
            }
        )
    }
}

@Composable
fun ContactPickerDialog(
    contacts: List<ContactInfo>,
    onDismiss: () -> Unit,
    onConfirm: (List<ContactInfo>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredContacts = remember(searchQuery, contacts) {
        contacts.filter { 
            it.name.contains(searchQuery, ignoreCase = true) || 
            it.phoneNumber.contains(searchQuery) 
        }.sortedBy { it.name }
    }
    
    val alphabet = remember(filteredContacts) {
        filteredContacts.map { it.name.take(1).uppercase() }.distinct()
    }
    
    val selectedContacts = remember { mutableStateListOf<ContactInfo>() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Contacts to Import") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    placeholder = { Text("Search") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                if (filteredContacts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (contacts.isEmpty()) "No contacts found." else "No matches found.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Box(modifier = Modifier.height(400.dp)) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize().padding(end = 24.dp)
                        ) {
                            itemsIndexed(filteredContacts) { index, contact ->
                                val isChecked = selectedContacts.contains(contact)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isChecked) selectedContacts.remove(contact)
                                            else selectedContacts.add(contact)
                                        }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = {
                                            if (it) selectedContacts.add(contact)
                                            else selectedContacts.remove(contact)
                                        }
                                    )
                                    Column(modifier = Modifier.padding(start = 8.dp)) {
                                        Text(text = contact.name, fontWeight = FontWeight.SemiBold)
                                        Text(text = contact.phoneNumber, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .width(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            alphabet.forEach { letter ->
                                Text(
                                    text = letter,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clickable {
                                            val index = filteredContacts.indexOfFirst { 
                                                it.name.startsWith(letter, ignoreCase = true) 
                                            }
                                            if (index != -1) {
                                                coroutineScope.launch {
                                                    listState.animateScrollToItem(index)
                                                }
                                            }
                                        }
                                        .padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedContacts.toList()) },
                enabled = selectedContacts.isNotEmpty()
            ) {
                Text("Import (${selectedContacts.size})")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SettingsItem(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isLoading: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = MaterialTheme.shapes.large
    ) {
        ListItem(
            headlineContent = { 
                Text(
                    text = title, 
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                ) 
            },
            supportingContent = { 
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ) 
            },
            leadingContent = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

private fun saveDataToFile(context: Context, uri: Uri, content: String) {
    try {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            OutputStreamWriter(outputStream).use { writer ->
                writer.write(content)
            }
        }
        Toast.makeText(context, "Exported successfully", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Export failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
    }
}
