package uz.yalla.platform.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation

/**
 * Android actual for [NativeNavHost].
 *
 * Uses Decompose [Children] with a slide animation. Each child renders its own
 * [Scaffold] + [TopAppBar] / [LargeTopAppBar] inside the animation block so the
 * toolbar transitions together with the screen content (no flicker).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun <C : Route> NativeNavHost(
    rootComponent: NativeRootComponent<C>,
    screenProvider: ScreenProvider<C>,
    modifier: Modifier,
) {
    val navigator = rootComponent.navigator
    val canGoBack by navigator.canGoBack.collectAsState()

    CompositionLocalProvider(LocalNavigator provides navigator) {
        Children(
            stack = rootComponent.childStack,
            modifier = modifier,
            animation = stackAnimation(slide()),
        ) { child ->
            val route = child.configuration
            val config = remember(route) { screenProvider.configFor(route) }
            val toolbarState = remember(route) { ToolbarState() }

            if (config.showsNavigationBar) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {},
                            navigationIcon = { BackButton(canGoBack, navigator) },
                            actions = { ToolbarActions(toolbarState) },
                        )
                    },
                ) { paddingValues ->
                    SideEffect {
                        if (toolbarState.contentPadding != paddingValues) {
                            toolbarState.contentPadding = paddingValues
                        }
                    }
                    Box(modifier = Modifier.consumeWindowInsets(paddingValues)) {
                        screenProvider.Content(route, navigator, toolbarState)
                    }
                }
            } else {
                val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
                SideEffect {
                    if (toolbarState.contentPadding != systemBarsPadding) {
                        toolbarState.contentPadding = systemBarsPadding
                    }
                }
                Box(modifier = Modifier.consumeWindowInsets(systemBarsPadding)) {
                    screenProvider.Content(route, navigator, toolbarState)
                }
            }
        }
    }
}

@Composable
private fun BackButton(canGoBack: Boolean, navigator: Navigator) {
    if (canGoBack) {
        IconButton(onClick = { navigator.pop() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
            )
        }
    }
}

@Composable
private fun RowScope.ToolbarActions(toolbarState: ToolbarState) {
    toolbarState.actions.forEach { action ->
        when (action) {
            is ToolbarAction.Text -> TextButton(onClick = action.onClick) {
                Text(action.label)
            }

            is ToolbarAction.Icon -> IconButton(onClick = action.onClick) {
                Text(action.icon.name)
            }
        }
    }
}
