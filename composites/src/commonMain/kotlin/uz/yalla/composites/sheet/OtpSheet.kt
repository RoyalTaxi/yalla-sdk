package uz.yalla.composites.sheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.primitives.otp.PinRow

/**
 * Color configuration for [OtpSheet].
 */
@Immutable
data class OtpSheetColors(
    val headline: Color,
    val description: Color,
)

/**
 * Dimension configuration for [OtpSheet].
 */
@Immutable
data class OtpSheetDimens(
    val headlineDescriptionSpacing: Dp,
    val descriptionPinSpacing: Dp,
)

/**
 * Default values for [OtpSheet].
 *
 * Provides theme-aware defaults for [colors] and layout [dimens] that can be overridden.
 */
object OtpSheetDefaults {

    /**
     * Creates theme-aware default colors.
     *
     * @param headline Headline text color, defaults to [System.color.text.base].
     * @param description Description text color, defaults to [System.color.text.subtle].
     */
    @Composable
    fun colors(
        headline: Color = System.color.text.base,
        description: Color = System.color.text.subtle,
    ): OtpSheetColors =
        OtpSheetColors(
            headline = headline,
            description = description,
        )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        headlineDescriptionSpacing: Dp = 10.dp,
        descriptionPinSpacing: Dp = 32.dp,
    ): OtpSheetDimens =
        OtpSheetDimens(
            headlineDescriptionSpacing = headlineDescriptionSpacing,
            descriptionPinSpacing = descriptionPinSpacing,
        )
}

/**
 * OTP / verification-code entry sheet.
 *
 * Composes [FormSheet] + [PinRow] with a headline/description layout. Replaces
 * the duplicate validation sheet pattern found in auth and billing features.
 *
 * ## Usage
 *
 * ```kotlin
 * OtpSheet(
 *     isVisible = state.showOtp,
 *     onDismissRequest = { viewModel.dismiss() },
 *     code = state.code,
 *     onCodeChange = { viewModel.onCodeChange(it) },
 *     codeLength = 5,
 *     onCodeComplete = { code -> viewModel.verify(code) },
 *     headline = "Verify your phone",
 *     description = "Enter the 5-digit code sent to +998 90 123 45 67",
 *     title = "Verification",
 *     isError = state.isCodeError,
 *     resendButton = {
 *         TextButton(onClick = { viewModel.resend() }) {
 *             Text("Resend code")
 *         }
 *     },
 *     confirmButton = {
 *         PrimaryButton(
 *             onClick = { viewModel.verify() },
 *             modifier = Modifier.fillMaxWidth(),
 *         ) { Text("Confirm") }
 *     },
 * )
 * ```
 *
 * @param colors Color configuration, defaults to [OtpSheetDefaults.colors].
 * @param dimens Dimension configuration, defaults to [OtpSheetDefaults.dimens].
 * @param confirmButton Optional bottom-anchored confirm button (passed as [FormSheet]'s action).
 *
 * @see FormSheet for the underlying form sheet layout
 * @see PinRow for the PIN input primitive
 * @see OtpSheetDefaults for default values
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    code: String,
    onCodeChange: (String) -> Unit,
    codeLength: Int,
    onCodeComplete: (String) -> Unit,
    headline: String,
    description: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    isError: Boolean = false,
    focusRequester: FocusRequester? = null,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onFullyExpanded: (() -> Unit)? = null,
    colors: OtpSheetColors = OtpSheetDefaults.colors(),
    dimens: OtpSheetDimens = OtpSheetDefaults.dimens(),
    resendButton: @Composable (() -> Unit)? = null,
    confirmButton: @Composable (() -> Unit)? = null,
) {
    FormSheet(
        isVisible = isVisible,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        title = title,
        sheetState = sheetState,
        onFullyExpanded = onFullyExpanded,
        action = confirmButton,
    ) {
        Text(
            text = headline,
            style = System.font.title.xLarge,
            color = colors.headline,
        )
        Spacer(Modifier.height(dimens.headlineDescriptionSpacing))
        Text(
            text = description,
            style = System.font.body.small.regular,
            color = colors.description,
        )
        Spacer(Modifier.height(dimens.descriptionPinSpacing))
        PinRow(
            value = code,
            onValueChange = onCodeChange,
            length = codeLength,
            onComplete = onCodeComplete,
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            focusRequester = focusRequester ?: remember { FocusRequester() },
        )
        Spacer(Modifier.height(8.dp))
        resendButton?.let {
            Box(Modifier.align(Alignment.CenterHorizontally)) { it() }
        }
    }
}
