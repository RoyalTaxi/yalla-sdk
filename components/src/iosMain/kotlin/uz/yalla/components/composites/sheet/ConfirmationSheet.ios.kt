package uz.yalla.components.composites.sheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import uz.yalla.components.config.requireConfig
import uz.yalla.components.platform.findKeyWindowRootController
import uz.yalla.design.image.ThemedImage
import uz.yalla.design.theme.System

@Composable
public actual fun ConfirmationSheet(
    isVisible: Boolean,
    image: ThemedImage,
    title: String,
    description: String,
    actionText: String,
    onAction: () -> Unit,
    onDismissRequest: () -> Unit,
    dismissEnabled: Boolean,
    header: String?
) {
    val currentOnAction by rememberUpdatedState(onAction)
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)
    val isDark = System.isDark
    val assetName = image.assetName

    val handle =
        remember {
            requireConfig().sheet.createConfirmation(
                imageResource = assetName,
                isDark = isDark,
                header = header,
                title = title,
                description = description,
                actionText = actionText,
                dismissEnabled = dismissEnabled,
                onAction = { currentOnAction() },
                onDismissRequest = { currentOnDismissRequest() }
            )
        }

    DisposableEffect(isVisible) {
        if (!isVisible) {
            return@DisposableEffect onDispose {}
        }

        val parent = findKeyWindowRootController() ?: return@DisposableEffect onDispose {}
        handle.present(parent)

        onDispose { handle.dismiss() }
    }
}
