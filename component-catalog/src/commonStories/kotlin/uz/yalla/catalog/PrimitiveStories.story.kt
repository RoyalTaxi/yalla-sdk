package uz.yalla.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.storytale.story
import uz.yalla.core.profile.GenderKind
import uz.yalla.design.theme.System
import uz.yalla.primitives.button.BottomSheetButton
import uz.yalla.primitives.button.GenderButton
import uz.yalla.primitives.button.IconButton
import uz.yalla.primitives.button.NavigationButton
import uz.yalla.primitives.button.PrimaryButton
import uz.yalla.primitives.button.SecondaryButton
import uz.yalla.primitives.button.SensitiveButton
import uz.yalla.primitives.button.TextButton
import uz.yalla.primitives.dialog.LoadingDialog
import uz.yalla.primitives.field.DateField
import uz.yalla.primitives.field.NumberField
import uz.yalla.primitives.field.PrimaryField
import uz.yalla.primitives.field.SearchField
import uz.yalla.primitives.indicator.DotsIndicator
import uz.yalla.primitives.indicator.LoadingIndicator
import uz.yalla.primitives.indicator.SplashOverlay
import uz.yalla.primitives.indicator.StripedProgressBar
import uz.yalla.primitives.otp.PinRow
import uz.yalla.primitives.pin.LocationPin
import uz.yalla.primitives.pin.SearchPin
import uz.yalla.primitives.rating.RatingRow
import uz.yalla.primitives.skeleton.SkeletonBox
import uz.yalla.primitives.skeleton.SkeletonText
import uz.yalla.primitives.topbar.LargeTopBar
import uz.yalla.primitives.topbar.TopBar
import uz.yalla.resources.icons.Calendar
import uz.yalla.resources.icons.Check
import uz.yalla.resources.icons.Places
import uz.yalla.resources.icons.Warning
import uz.yalla.resources.icons.YallaIcons

val `Primitives / Buttons / states and interaction` by story {
    CatalogFrame(width = 460) {
        var clicks by remember { mutableIntStateOf(0) }

        CatalogStack {
            CatalogTitle(
                title = "Button states",
                description = "Default, loading, disabled, secondary, tertiary, and click feedback.",
            )
            PrimaryButton(onClick = { clicks++ }, modifier = Modifier.fillMaxWidth()) {
                Text("Create order")
            }
            PrimaryButton(onClick = {}, loading = true, modifier = Modifier.fillMaxWidth()) {
                Text("Creating order")
            }
            PrimaryButton(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
                Text("Unavailable")
            }
            SecondaryButton(onClick = { clicks++ }, modifier = Modifier.fillMaxWidth()) {
                Text("Choose another route")
            }
            TextButton(onClick = { clicks++ }, modifier = Modifier.fillMaxWidth()) {
                Text("Skip for now")
            }
            InteractionCounter(label = "Click events", count = clicks)
        }
    }
}

val `Primitives / Buttons / icon, navigation, gender` by story {
    CatalogFrame(width = 480) {
        var gender by remember { mutableStateOf(GenderKind.Male) }

        CatalogStack(spacing = 16) {
            CatalogTitle("Icon buttons", "Navigation, sheet action, and selectable gender buttons.")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = {}) { CatalogIcon(YallaIcons.Places) }
                IconButton(onClick = {}, enabled = false) { CatalogIcon(YallaIcons.Warning) }
                NavigationButton(onClick = {})
                BottomSheetButton(
                    onClick = {},
                    icon = rememberVectorPainter(YallaIcons.Check),
                ) {
                    Text("Confirm")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                GenderButton(
                    gender = GenderKind.Male,
                    isSelected = gender == GenderKind.Male,
                    onClick = { gender = GenderKind.Male },
                    modifier = Modifier.weight(1f),
                )
                GenderButton(
                    gender = GenderKind.Female,
                    isSelected = gender == GenderKind.Female,
                    onClick = { gender = GenderKind.Female },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

val `Primitives / Buttons / sensitive hold` by story {
    CatalogFrame(width = 440) {
        CatalogStack {
            CatalogTitle("SensitiveButton", "Press and hold to expose destructive confirmation behavior.")
            SensitiveButton(
                onClick = {},
                confirmText = "Cancel ride",
                countdownText = "Hold to cancel %s",
                countdownSeconds = 3,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

val `Primitives / Fields / editing states` by story {
    CatalogFrame(width = 480) {
        val address = rememberTextFieldState("Amir Temur Avenue")
        val search = rememberTextFieldState("Tashkent")

        CatalogStack {
            CatalogTitle("Fields", "Primary, search, number, and date variants with live editable state.")
            PrimaryField(
                state = address,
                placeholder = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
            )
            SearchField(
                state = search,
                placeholder = "Where to?",
                leadingIcon = { CatalogIcon(YallaIcons.Places) },
                modifier = Modifier.fillMaxWidth(),
            )
            NumberField(
                value = "901234567",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
            )
            DateField(
                date = LocalDate(2026, 5, 8),
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

val `Primitives / Indicators / loading, dots, progress` by story {
    CatalogFrame(width = 480) {
        var page by remember { mutableIntStateOf(1) }

        CatalogStack(spacing = 18) {
            CatalogTitle("Progress primitives", "Inline spinner, page dots, striped progress, and state transitions.")
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                LoadingIndicator()
                DotsIndicator(pageCount = 4, currentPage = page)
                PrimaryButton(onClick = { page = (page + 1) % 4 }) {
                    Text("Next")
                }
            }
            StripedProgressBar(progress = 0.62f, modifier = Modifier.fillMaxWidth())
        }
    }
}

val `Primitives / Indicators / splash overlay` by story {
    CatalogFrame(width = 420, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(360.dp),
        ) {
            SplashOverlay(
                message = "Preparing your ride",
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

val `Primitives / Dialogs / loading` by story {
    CatalogFrame(width = 360) {
        CatalogTitle("LoadingDialog", "Modal blocking spinner with platform loading indicator.")
        LoadingDialog(dismissOnBackPress = false, dismissOnClickOutside = false)
    }
}

val `Primitives / Top bars` by story {
    CatalogFrame(width = 500) {
        CatalogStack(spacing = 18) {
            TopBar(
                title = { Text("Components") },
                onNavigationClick = {},
                actions = {
                    IconButton(onClick = {}) { CatalogIcon(YallaIcons.Places) }
                },
            )
            LargeTopBar(
                title = { Text("Yalla SDK") },
                actions = {
                    IconButton(onClick = {}) { CatalogIcon(YallaIcons.Calendar) }
                },
            )
        }
    }
}

val `Primitives / OTP and rating / interaction` by story {
    CatalogFrame(width = 420) {
        var pin by remember { mutableStateOf("1234") }
        var rating by remember { mutableIntStateOf(4) }

        CatalogStack(spacing = 18) {
            CatalogTitle("Input primitives", "OTP and rating controls keep their own local story state.")
            PinRow(
                value = pin,
                onValueChange = { pin = it },
                length = 6,
                onComplete = {},
                modifier = Modifier.fillMaxWidth(),
            )
            RatingRow(rating = rating, onRatingChange = { rating = it })
        }
    }
}

val `Primitives / Pins / location and search` by story {
    CatalogFrame(width = 560) {
        var jumping by remember { mutableStateOf(false) }

        CatalogStack(spacing = 18) {
            CatalogTitle("Map pins", "Address, ETA, animated jump, and Lottie search pin variants.")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
            ) {
                LocationPin(address = "Tashkent City", jumping = jumping)
                LocationPin(timeout = 5)
                SearchPin(modifier = Modifier.size(96.dp))
            }
            PrimaryButton(onClick = { jumping = !jumping }, modifier = Modifier.fillMaxWidth()) {
                Text(if (jumping) "Stop jump" else "Start jump")
            }
        }
    }
}

val `Primitives / Skeleton / shimmer loading` by story {
    CatalogFrame(width = 460) {
        CatalogStack(spacing = 14) {
            CatalogTitle("Skeletons", "Shimmering content placeholders for list and card loading states.")
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                SkeletonBox(modifier = Modifier.size(56.dp), shape = CircleShape)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    SkeletonText(modifier = Modifier.fillMaxWidth(0.92f))
                    SkeletonText(modifier = Modifier.fillMaxWidth(0.64f), height = 12.dp)
                }
            }
            SkeletonBox(modifier = Modifier.fillMaxWidth().height(96.dp))
        }
    }
}
