package com.managevent.app.features.katalog.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.features.katalog.data.EventModel
import com.managevent.app.features.katalog.viewmodel.KatalogViewModel

@Composable
fun KatalogFormScreen(
    modifier: Modifier = Modifier,
    viewModel: KatalogViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSuccessSubmit: () -> Unit = {}
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Pernikahan") }
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var priceInput by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    KatalogFormContent(
        title = title,
        onTitleChange = { title = it },
        category = category,
        onCategoryChange = { category = it },
        date = date,
        onDateChange = { date = it },
        location = location,
        onLocationChange = { location = it },
        priceInput = priceInput,
        onPriceChange = { priceInput = it },
        imageUrl = imageUrl,
        onImageUrlChange = { imageUrl = it },
        description = description,
        onDescriptionChange = { description = it },
        errorMessage = errorMessage,
        onBackClick = onBackClick,
        onSubmit = {
            if (title.isBlank() || date.isBlank() || location.isBlank() || priceInput.isBlank()) {
                errorMessage = "Harap isi semua kolom wajib!"
                return@KatalogFormContent
            }
            val priceValue = priceInput.toDoubleOrNull() ?: 0.0
            val newEvent = EventModel(
                title = title,
                category = category,
                date = date,
                location = location,
                price = priceValue,
                imageUrl = if (imageUrl.isNotBlank()) imageUrl else null,
                description = description
            )
            viewModel.addEvent(newEvent) {
                onSuccessSubmit()
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KatalogFormContent(
    title: String,
    onTitleChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    date: String,
    onDateChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    priceInput: String,
    onPriceChange: (String) -> Unit,
    imageUrl: String,
    onImageUrlChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    errorMessage: String?,
    onBackClick: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("Pernikahan", "Konser", "Seminar", "Ulang Tahun")
    var categoryExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFFAFBFD),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tambah Event / Paket Baru",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Judul Event / Paket") },
                placeholder = { Text("Contoh: Pameran Pernikahan Nusantara") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF3F51B5))
            )

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kategori") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF3F51B5))
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                onCategoryChange(item)
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = date,
                onValueChange = onDateChange,
                label = { Text("Tanggal Pelaksanaan") },
                placeholder = { Text("Contoh: 18 Juli 2026") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF3F51B5))
            )

            OutlinedTextField(
                value = location,
                onValueChange = onLocationChange,
                label = { Text("Lokasi Tempat") },
                placeholder = { Text("Contoh: Jakarta Convention Center") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF3F51B5))
            )

            OutlinedTextField(
                value = priceInput,
                onValueChange = onPriceChange,
                label = { Text("Harga (Rp)") },
                placeholder = { Text("Contoh: 150000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF3F51B5))
            )

            OutlinedTextField(
                value = imageUrl,
                onValueChange = onImageUrlChange,
                label = { Text("URL Poster Gambar (Opsional)") },
                placeholder = { Text("https://example.com/poster.jpg") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF3F51B5))
            )

            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Deskripsi Event & Paket") },
                placeholder = { Text("Tuliskan deskripsi dan detail keunggulan paket...") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF3F51B5))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
            ) {
                Text(
                    text = "Simpan Event",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KatalogFormContentPreview() {
    ManageventTheme {
        KatalogFormContent(
            title = "Pameran Pernikahan Nusantara 2026",
            onTitleChange = {},
            category = "Pernikahan",
            onCategoryChange = {},
            date = "18 Juli 2026",
            onDateChange = {},
            location = "Jakarta Convention Center",
            onLocationChange = {},
            priceInput = "150000",
            onPriceChange = {},
            imageUrl = "",
            onImageUrlChange = {},
            description = "Deskripsi paket pernikahan lengkap...",
            onDescriptionChange = {},
            errorMessage = null,
            onBackClick = {},
            onSubmit = {}
        )
    }
}
