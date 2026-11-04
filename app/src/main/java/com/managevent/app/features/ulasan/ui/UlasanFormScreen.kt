package com.managevent.app.features.ulasan.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.managevent.app.core.components.AppTopBar
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.features.ulasan.viewmodel.UlasanViewModel

@Composable
fun UlasanFormScreen(
    eventId: String = "dummy-event-id",
    userId: String = "dummy-user-id",
    username: String = "User Uji",
    onSuccessNavigateBack: () -> Unit = {},
    viewModel: UlasanViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    UlasanFormContent(
        eventId = eventId,
        ratingInput = viewModel.ratingInput,
        onRatingChange = viewModel::onRatingChange,
        commentInput = viewModel.commentInput,
        onCommentChange = viewModel::onCommentChange,
        onSubmit = {
            viewModel.submitUlasan(
                eventId = eventId,
                userId = userId,
                username = username,
                onSuccess = onSuccessNavigateBack
            )
        },
        modifier = modifier
    )
}

@Composable
fun UlasanFormContent(
    eventId: String,
    ratingInput: Int,
    onRatingChange: (Int) -> Unit,
    commentInput: String,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "Beri Ulasan") },
        containerColor = BackgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Beri Ulasan untuk Event ID: $eventId",
                color = Color(0xFF181445),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Seberapa puas kamu?",
                style = MaterialTheme.typography.bodyLarge,
                color = GrayTextColor
            )
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                repeat(5) { index ->
                    val starValue = index + 1
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Bintang $starValue",
                        tint = if (starValue <= ratingInput) CoralColor else Color.LightGray,
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { onRatingChange(starValue) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = commentInput,
                onValueChange = onCommentChange,
                label = { Text("Tuliskan pengalamanmu...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IndigoTextColor,
                    focusedLabelColor = IndigoTextColor,
                    unfocusedContainerColor = Color(0xFFF6F2FF),
                    focusedContainerColor = Color(0xFFF6F2FF)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoTextColor)
            ) {
                Text(
                    "Kirim Ulasan",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UlasanFormScreenPreview() {
    ManageventTheme {
        UlasanFormContent(
            eventId = "EVT-101",
            ratingInput = 5,
            onRatingChange = {},
            commentInput = "Acaranya sangat bagus dan tertata rapi!",
            onCommentChange = {},
            onSubmit = {}
        )
    }
}
