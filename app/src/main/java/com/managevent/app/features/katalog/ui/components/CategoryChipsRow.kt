package com.managevent.app.features.katalog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.managevent.app.core.theme.ManageventTheme

@Composable
fun CategoryChipsRow(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    categories: List<String> = listOf("Semua", "Pernikahan", "Konser", "Seminar", "Ulang Tahun")
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = categories,
            key = { it }
        ) { category ->
            val isSelected = category.equals(selectedCategory, ignoreCase = true)
            val backgroundColor = if (isSelected) Color(0xFFEDE7F6) else Color(0xFFF0F2F6)
            val textColor = if (isSelected) Color(0xFF3F51B5) else Color(0xFF5A5C6E)
            val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(backgroundColor)
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = textColor,
                        fontWeight = fontWeight,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryChipsRowPreview() {
    ManageventTheme {
        CategoryChipsRow(
            selectedCategory = "Semua",
            onCategorySelected = {}
        )
    }
}
