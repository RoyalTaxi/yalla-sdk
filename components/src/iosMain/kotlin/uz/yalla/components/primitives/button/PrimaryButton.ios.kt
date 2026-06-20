package uz.yalla.components.primitives.button

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import uz.yalla.design.theme.YallaTheme
import uz.yalla.foundation.theme.rememberIsDarkTheme

// TODO(quality, needs-decision): H4/M18 — PrimaryButton is bridged twice: this free
//  `PrimaryButtonViewController` function AND the `PrimaryButtonController` class (the idiom every
//  other component uses). Collapsing to the Controller and deleting this function is a BREAKING
//  removal from the committed `components.klib.api` (and a live Swift consumer in ConfirmationSheet.swift
//  must migrate). Blocked on owner sign-off for the breaking removal + Swift migration.
@OptIn(ExperimentalComposeUiApi::class)
public fun PrimaryButtonViewController(
    title: String,
    onClick: () -> Unit
): UIViewController =
    ComposeUIViewController(
        configure = { opaque = false }
    ) {
        YallaTheme(isDark = rememberIsDarkTheme()) {
            PrimaryButton(
                onClick = onClick,
                modifier = Modifier.fillMaxSize()
            ) { _, _, styles ->
                Text(text = title, style = styles.textStyle)
            }
        }
    }
