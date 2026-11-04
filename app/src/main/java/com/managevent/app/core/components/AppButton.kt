package com.managevent.app.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import com.managevent.app.core.theme.ButtonShape
import com.managevent.app.core.theme.Outline
import com.managevent.app.core.theme.Primary
import com.managevent.app.core.theme.SecondaryContainer

enum class AppButtonVariant {
    STANDARD_PRIMARY, // Deep Indigo (#3525CD)
    CONVERSION_CTA,   // Vibrant Coral (#FC6955)
    SECONDARY_OUTLINED
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppButtonVariant = AppButtonVariant.STANDARD_PRIMARY,
    enabled: Boolean = true
) {
    val buttonHeight = if (variant == AppButtonVariant.CONVERSION_CTA) 56.dp else 48.dp

    when (variant) {
        AppButtonVariant.STANDARD_PRIMARY -> {
            Button(
                onClick = onClick,
                enabled = enabled,
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = Color.White
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .height(buttonHeight)
            ) {
                Text(text = text)
            }
        }
        AppButtonVariant.CONVERSION_CTA -> {
            Button(
                onClick = onClick,
                enabled = enabled,
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryContainer,
                    contentColor = Color.White
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .height(buttonHeight)
            ) {
                Text(text = text)
            }
        }
        AppButtonVariant.SECONDARY_OUTLINED -> {
            OutlinedButton(
                onClick = onClick,
                enabled = enabled,
                shape = ButtonShape,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Primary
                ),
                border = BorderStroke(1.dp, Outline),
                modifier = modifier
                    .fillMaxWidth()
                    .height(buttonHeight)
            ) {
                Text(text = text)
            }
        }
    }
}
