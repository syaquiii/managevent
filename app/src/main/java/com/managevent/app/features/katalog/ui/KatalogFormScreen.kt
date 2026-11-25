package com.managevent.app.features.katalog.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.managevent.app.core.theme.ActiveFilterChipBackground
import com.managevent.app.core.theme.Background
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.theme.PrimaryContainer
import com.managevent.app.core.util.UiState
import com.managevent.app.features.katalog.data.EventModel
import kotlin.math.roundToInt

@Composable
fun KatalogFormScreen(
    modifier: Modifier = Modifier,
    initialEvent: EventModel? = null,
    formState: UiState<String> = UiState.Idle,
    onBackClick: () -> Unit = {},
    onSubmitEvent: (EventModel, ByteArray?) -> Unit = { _, _ -> }
) {
    var title by remember(initialEvent) { mutableStateOf(initialEvent?.title ?: "") }
    var category by remember(initialEvent) { mutableStateOf(initialEvent?.category?.ifBlank { "Pernikahan" } ?: "Pernikahan") }
    var date by remember(initialEvent) { mutableStateOf(initialEvent?.date ?: "") }
    var location by remember(initialEvent) { mutableStateOf(initialEvent?.location ?: "") }
    var priceInput by remember(initialEvent) { mutableStateOf(initialEvent?.price?.roundToInt()?.toString() ?: "") }
    var description by remember(initialEvent) { mutableStateOf(initialEvent?.description ?: "") }
    var localErrorMessage by remember { mutableStateOf<String?>(null) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }

    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImageUri = uri
            if (uri != null) {
                try {
                    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    selectedImageBytes = bytes
                    localErrorMessage = null
                } catch (e: Exception) {
                    localErrorMessage = "Gagal memuat gambar: ${e.message}"
                }
            }
        }
    )

    val serverError = (formState as? UiState.Error)?.message
    val displayError = localErrorMessage ?: serverError

    KatalogFormContent(
        isEditMode = initialEvent != null,
        title = title,
        onTitleChange = { title = it; localErrorMessage = null },
        category = category,
        onCategoryChange = { category = it },
        date = date,
        onDateChange = { date = it; localErrorMessage = null },
        location = location,
        onLocationChange = { location = it; localErrorMessage = null },
        priceInput = priceInput,
        onPriceChange = { priceInput = it; localErrorMessage = null },
        description = description,
        onDescriptionChange = { description = it },
        errorMessage = displayError,
        existingImageUrl = initialEvent?.imageUrl,
        selectedImageUri = selectedImageUri,
        isSubmitting = formState is UiState.Loading,
        onPickImageClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onBackClick = onBackClick,
        onSubmit = {
            val priceValue = priceInput.trim().toDoubleOrNull()
            when {
                title.isBlank() || date.isBlank() || location.isBlank() || priceInput.isBlank() -> {
                    localErrorMessage = "Harap isi semua kolom wajib!"
                }
                priceValue == null || priceValue < 0.0 -> {
                    localErrorMessage = "Masukkan nominal harga yang valid."
                }
                else -> {
                    localErrorMessage = null
                    val newEvent = EventModel(
                        id = initialEvent?.id ?: "",
                        vendorId = initialEvent?.vendorId,
                        title = title.trim(),
                        category = category,
                        date = date.trim(),
                        location = location.trim(),
                        price = priceValue,
                        imageUrl = initialEvent?.imageUrl?.takeIf { it.isNotBlank() },
                        description = description.trim(),
                        isFavorite = initialEvent?.isFavorite ?: false
                    )
                    onSubmitEvent(newEvent, selectedImageBytes)
                }
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KatalogFormContent(
    isEditMode: Boolean,
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
    description: String,
    onDescriptionChange: (String) -> Unit,
    errorMessage: String?,
    existingImageUrl: String?,
    selectedImageUri: Uri?,
    isSubmitting: Boolean,
    onPickImageClick: () -> Unit,
    onBackClick: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("Pernikahan", "Konser", "Seminar", "Ulang Tahun", "Festival", "Pameran")
    var categoryExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Event / Paket" else "Tambah Event / Paket Baru",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.ime)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
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
                    label = { Text("Judul Event / Paket *") },
                    placeholder = { Text("Contoh: Pameran Pernikahan Nusantara") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryContainer)
                )

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
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryContainer)
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
                    label = { Text("Tanggal Pelaksanaan *") },
                    placeholder = { Text("Contoh: 18 Juli 2026") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryContainer)
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = onLocationChange,
                    label = { Text("Lokasi Tempat *") },
                    placeholder = { Text("Contoh: Jakarta Convention Center") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryContainer)
                )

                OutlinedTextField(
                    value = priceInput,
                    onValueChange = onPriceChange,
                    label = { Text("Harga (Rp) *") },
                    placeholder = { Text("Contoh: 150000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryContainer)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Poster Gambar",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .height(120.dp)
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            val imageToLoad = selectedImageUri ?: existingImageUrl

                            if (imageToLoad != null && imageToLoad.toString().isNotBlank()) {
                                AsyncImage(
                                    model = imageToLoad,
                                    contentDescription = "Poster",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = ActiveFilterChipBackground,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "Tidak Ada Gambar",
                                            style = MaterialTheme.typography.labelMedium.copy(color = PrimaryContainer)
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = onPickImageClick,
                            enabled = !isSubmitting,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text("Pilih Gambar")
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Deskripsi Event & Paket") },
                    placeholder = { Text("Tuliskan deskripsi dan detail keunggulan paket...") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryContainer)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onSubmit,
                    enabled = !isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer)
                ) {
                    Text(
                        text = when {
                            isSubmitting -> "Menyimpan..."
                            isEditMode -> "Simpan Perubahan"
                            else -> "Simpan Event"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KatalogFormContentPreview() {
    ManageventTheme {
        KatalogFormContent(
            isEditMode = false,
            title = "Pameran Pernikahan Nusantara 2026",
            onTitleChange = {},
            category = "Pernikahan",
            onCategoryChange = {},
            date = "18 Juli 2026",
            onDateChange = {},
            onLocationChange = {},
            location = "Jakarta Convention Center",
            priceInput = "150000",
            onPriceChange = {},
            description = "Deskripsi paket pernikahan lengkap...",
            onDescriptionChange = {},
            errorMessage = null,
            existingImageUrl = null,
            selectedImageUri = null,
            isSubmitting = false,
            onPickImageClick = {},
            onBackClick = {},
            onSubmit = {}
        )
    }
}
