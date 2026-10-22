package com.managevent.app.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ManageventColorScheme = lightColorScheme(
    primary = CoralPrimary,
    secondary = TealSecondary,
    background = WhiteNeutral,
    surface = WhiteNeutral,

    // Warna teks yang kontras untuk di atas warna primary/secondary
    onPrimary = WhiteNeutral,
    onSecondary = WhiteNeutral
)

@Composable
fun ManageventTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ManageventColorScheme,
        typography = Typography,
        content = content
    )
}
