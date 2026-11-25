package com.managevent.app.core.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.managevent.app.core.theme.ActiveFilterChipBackground
import com.managevent.app.core.theme.InactiveFilterChipBackground
import com.managevent.app.core.theme.OnSurfaceVariant
import com.managevent.app.core.theme.PrimaryContainer

@Composable
fun AppFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
        },
        shape = CircleShape,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = InactiveFilterChipBackground,
            labelColor = OnSurfaceVariant,
            selectedContainerColor = ActiveFilterChipBackground,
            selectedLabelColor = PrimaryContainer
        ),
        border = null,
        modifier = modifier.height(32.dp)
    )
}
