package uz.yalla.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.storytale.story
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.primitives.button.BottomSheetButton
import uz.yalla.primitives.button.IconButton
import uz.yalla.primitives.button.NavigationButton
import uz.yalla.primitives.button.PrimaryButton
import uz.yalla.primitives.button.SecondaryButton
import uz.yalla.primitives.button.SensitiveButton
import uz.yalla.primitives.button.TextButton
import uz.yalla.primitives.field.DateField
import uz.yalla.primitives.field.NumberField
import uz.yalla.primitives.field.PrimaryField
import uz.yalla.primitives.field.SearchField
import uz.yalla.primitives.indicator.DotsIndicator
import uz.yalla.primitives.indicator.LoadingIndicator
import uz.yalla.primitives.indicator.StripedProgressBar
import uz.yalla.primitives.otp.PinRow
import uz.yalla.primitives.rating.RatingRow
import uz.yalla.primitives.topbar.LargeTopBar
import uz.yalla.primitives.topbar.TopBar
import uz.yalla.resources.icons.ArrowLeft
import uz.yalla.resources.icons.Check
import uz.yalla.resources.icons.Places
import uz.yalla.resources.icons.YallaIcons

val `Buttons / Primary, Secondary, Text` by story {
    Frame {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PrimaryButton(onClick = {}) { Text("Create order") }
            PrimaryButton(onClick = {}, loading = true) { Text("Creating") }
            SecondaryButton(onClick = {}) { Text("Choose another route") }
            TextButton(onClick = {}) { Text("Skip for now") }
        }
    }
}

val `Buttons / Icon and navigation` by story {
    Frame {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(onClick = {}) { CatalogIcon(YallaIcons.Places) }
            NavigationButton(onClick = {})
            BottomSheetButton(
                onClick = {},
                icon = rememberVectorPainter(YallaIcons.Check),
            ) { Text("Confirm") }
        }
    }
}

val `Buttons / Sensitive (hold to confirm)` by story {
    Frame {
        SensitiveButton(
            onClick = {},
            confirmText = "Cancel ride",
            countdownText = "Hold to cancel %s",
            countdownSeconds = 3,
        )
    }
}

val `Fields / Primary, Search, Number, Date` by story {
    Frame(width = 420) {
        val name = rememberTextFieldState("Amir Temur Avenue")
        val search = rememberTextFieldState("Tashkent")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PrimaryField(
                state = name,
                placeholder = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
            )
            SearchField(
                state = search,
                placeholder = "Where to?",
                leadingIcon = { CatalogIcon(YallaIcons.Places) },
                modifier = Modifier.fillMaxWidth(),
            )
            NumberField(value = "901234567", onValueChange = {}, modifier = Modifier.fillMaxWidth())
            DateField(date = LocalDate(2026, 5, 7), onClick = {}, modifier = Modifier.fillMaxWidth())
        }
    }
}

val `Indicators / Loading, dots, progress` by story {
    Frame {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            LoadingIndicator()
            DotsIndicator(pageCount = 4, currentPage = 1)
            StripedProgressBar(progress = 0.62f, modifier = Modifier.fillMaxWidth())
        }
    }
}

val `App bars / TopBar, LargeTopBar` by story {
    Frame(width = 460) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            TopBar(
                title = { Text("Components") },
                onNavigationClick = {},
            )
            LargeTopBar(title = { Text("Yalla SDK") })
        }
    }
}

val `OTP / PinRow` by story {
    Frame(width = 380) {
        PinRow(
            value = "1234",
            onValueChange = {},
            length = 6,
            onComplete = {},
        )
    }
}

val `Rating / RatingRow` by story {
    Frame(width = 320) {
        RatingRow(rating = 4, onRatingChange = {})
    }
}

@Composable
private fun Frame(width: Int = 360, content: @Composable () -> Unit) {
    YallaTheme(isDark = false) {
        Column(
            modifier = Modifier
                .width(width.dp)
                .background(System.color.background.base)
                .padding(24.dp),
        ) {
            content()
        }
    }
}

@Composable
private fun CatalogIcon(imageVector: ImageVector) {
    Icon(
        painter = rememberVectorPainter(imageVector),
        contentDescription = null,
    )
}
