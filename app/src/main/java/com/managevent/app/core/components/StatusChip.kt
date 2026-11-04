package com.managevent.app.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.managevent.app.core.theme.StatusBerjalanBackground
import com.managevent.app.core.theme.StatusBerjalanText
import com.managevent.app.core.theme.StatusSelesaiBackground
import com.managevent.app.core.theme.StatusSelesaiText

enum class StatusType {
    BERJALAN,
    SELESAI
}

@Composable
fun StatusChip(
    status: StatusType,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (status == StatusType.BERJALAN) StatusBerjalanBackground else StatusSelesaiBackground
    val textColor = if (status == StatusType.BERJALAN) StatusBerjalanText else StatusSelesaiText
    val textLabel = if (status == StatusType.BERJALAN) "Berjalan" else "Selesai"

    Box(
        modifier = modifier
            .height(28.dp)
            .background(color = backgroundColor, shape = CircleShape)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = textLabel,
            color = textColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
