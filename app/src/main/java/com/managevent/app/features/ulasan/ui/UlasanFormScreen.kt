package com.managevent.app.features.ulasan.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.managevent.app.core.theme.Background
import com.managevent.app.core.theme.InputFieldBackground
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.theme.OnBackground
import com.managevent.app.core.theme.OnSurfaceVariant
import com.managevent.app.core.theme.PrimaryContainer
import com.managevent.app.core.theme.SecondaryContainer

@Composable
fun UlasanFormScreen(
    eventTitle: String,
    isEditMode: Boolean,
    ratingInput: Int,
    commentInput: String,
    commentError: String?,
    isSubmitting: Boolean,
    onRatingChange: (Int) -> Unit,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    UlasanFormContent(
        eventTitle = eventTitle,
        isEditMode = isEditMode,
        ratingInput = ratingInput,
        commentInput = commentInput,
        commentError = commentError,
        isSubmitting = isSubmitting,
        onRatingChange = onRatingChange,
        onCommentChange = onCommentChange,
        onSubmit = onSubmit,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UlasanFormContent(
    eventTitle: String,
    isEditMode: Boolean,
    ratingInput: Int,
    commentInput: String,
    commentError: String?,
    isSubmitting: Boolean,
    onRatingChange: (Int) -> Unit,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Ulasan" else "Beri Ulasan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Background
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = eventTitle.ifBlank { "Ulasan Event" },
                    color = OnBackground,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Seberapa puas kamu?", style = MaterialTheme.typography.bodyLarge, color = OnSurfaceVariant)
                Row(modifier = Modifier.padding(vertical = 8.dp)) {
                    repeat(5) { index ->
                        val starValue = index + 1
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Bintang $starValue",
                            tint = if (starValue <= ratingInput) SecondaryContainer else Color.LightGray,
                            modifier = Modifier
                                .size(48.dp)
                                .clickable(enabled = !isSubmitting) { onRatingChange(starValue) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = commentInput,
                    onValueChange = onCommentChange,
                    label = { Text("Tuliskan pengalamanmu...") },
                    isError = commentError != null,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryContainer,
                        focusedLabelColor = PrimaryContainer,
                        unfocusedContainerColor = InputFieldBackground,
                        focusedContainerColor = InputFieldBackground
                    )
                )
                if (commentError != null) {
                    Text(
                        text = commentError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onSubmit,
                    enabled = !isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                    } else {
                        Text(
                            if (isEditMode) "Simpan Perubahan" else "Kirim Ulasan",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UlasanFormContentPreview() {
    ManageventTheme {
        UlasanFormContent(
            eventTitle = "Jakarta Jazz Symphony 2026",
            isEditMode = false,
            ratingInput = 5,
            commentInput = "Penampilan orkestra luar biasa!",
            commentError = null,
            isSubmitting = false,
            onRatingChange = {},
            onCommentChange = {},
            onSubmit = {},
            onBackClick = {}
        )
    }
}
