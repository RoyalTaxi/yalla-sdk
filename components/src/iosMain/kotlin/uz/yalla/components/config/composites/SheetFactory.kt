package uz.yalla.components.config.composites

import platform.Foundation.NSDate
import platform.UIKit.UIViewController
import uz.yalla.components.composites.item.ActionableItemModel
import uz.yalla.components.composites.item.SelectableItemModel

public interface SheetFactory {
    public fun createShell(
        fullHeight: Boolean,
        sheetSwipeEnabled: Boolean,
        contentController: UIViewController,
        onDismissRequest: () -> Unit
    ): ContentSheetHandle

    public fun createContent(
        fullHeight: Boolean,
        sheetSwipeEnabled: Boolean,
        title: String?,
        showClose: Boolean,
        contentController: UIViewController,
        onClose: (() -> Unit)?,
        onDismissRequest: () -> Unit
    ): ContentSheetHandle

    public fun createConfirmation(
        imageResource: String,
        isDark: Boolean,
        header: String?,
        title: String,
        description: String,
        actionText: String,
        dismissEnabled: Boolean,
        onAction: () -> Unit,
        onDismissRequest: () -> Unit
    ): ConfirmationSheetHandle

    public fun createSelection(
        title: String,
        items: List<SelectableItemModel>,
        selectedId: String?,
        onSelect: (id: String) -> Unit,
        onDismissRequest: () -> Unit
    ): SelectionSheetHandle

    public fun createAction(
        title: String,
        items: List<ActionableItemModel>,
        onAction: (id: String) -> Unit,
        onDismissRequest: () -> Unit
    ): ActionSheetHandle

    public fun createDatePicker(
        startDate: NSDate,
        minDate: NSDate?,
        maxDate: NSDate?,
        title: String?,
        dismissEnabled: Boolean,
        onSelect: (NSDate) -> Unit,
        onDismissRequest: () -> Unit
    ): DatePickerSheetHandle

    public fun createVerification(
        code: String,
        codeLength: Int,
        headline: String,
        description: String,
        confirmText: String,
        resendText: String,
        title: String?,
        isError: Boolean,
        isLoading: Boolean,
        resendEnabled: Boolean,
        dismissEnabled: Boolean,
        alphanumeric: Boolean,
        onCodeChange: (String) -> Unit,
        onConfirm: () -> Unit,
        onResend: () -> Unit,
        onCodeComplete: (String) -> Unit,
        onDismissRequest: () -> Unit
    ): VerificationSheetHandle

    // TODO(quality, needs-decision): H5 — createPromoCode/createNotificationDetail/createAddCard bake
    //  product features into the generic design-system bridge (no Android/common counterpart; Android
    //  composes these in the app from `createContent`). They should be removed from the SDK and built
    //  in the iOS app, but that is a BREAKING removal from the committed `components.klib.api` plus a
    //  Swift migration. Blocked on owner sign-off for the breaking removal.
    public fun createPromoCode(
        code: String,
        title: String,
        headline: String,
        placeholder: String,
        hint: String,
        confirmText: String,
        isLoading: Boolean,
        onCodeChange: (String) -> Unit,
        onSubmit: () -> Unit,
        onDismissRequest: () -> Unit
    ): PromoCodeSheetHandle

    public fun createNotificationDetail(
        title: String,
        date: String,
        body: String,
        imageUrl: String?,
        onDismissRequest: () -> Unit
    ): NotificationDetailSheetHandle

    public fun createAddCard(
        cardNumber: String,
        cardExpiry: String,
        title: String,
        cardNumberPlaceholder: String,
        expiryPlaceholder: String,
        confirmText: String,
        isError: Boolean,
        isLoading: Boolean,
        onCardNumberChange: (String) -> Unit,
        onExpiryChange: (String) -> Unit,
        onSubmit: () -> Unit,
        onDismissRequest: () -> Unit
    ): AddCardSheetHandle
}

public class ContentSheetHandle(
    public val viewController: UIViewController,
    public val present: (parent: UIViewController) -> Unit,
    public val dismiss: () -> Unit,
    public val updateContentHeight: (height: Double) -> Unit
)

public class ConfirmationSheetHandle(
    public val viewController: UIViewController,
    public val present: (parent: UIViewController) -> Unit,
    public val dismiss: () -> Unit
)

public class SelectionSheetHandle(
    public val viewController: UIViewController,
    public val present: (parent: UIViewController) -> Unit,
    public val dismiss: () -> Unit
)

public class ActionSheetHandle(
    public val viewController: UIViewController,
    public val present: (parent: UIViewController) -> Unit,
    public val dismiss: () -> Unit
)

public class DatePickerSheetHandle(
    public val viewController: UIViewController,
    public val present: (parent: UIViewController) -> Unit,
    public val dismiss: () -> Unit
)

public class VerificationSheetHandle(
    public val viewController: UIViewController,
    public val present: (parent: UIViewController) -> Unit,
    public val update: (
        code: String,
        description: String,
        isError: Boolean,
        isLoading: Boolean,
        resendText: String,
        resendEnabled: Boolean
    ) -> Unit,
    public val dismiss: () -> Unit
)

public class PromoCodeSheetHandle(
    public val viewController: UIViewController,
    public val present: (parent: UIViewController) -> Unit,
    public val update: (code: String, isLoading: Boolean) -> Unit,
    public val dismiss: () -> Unit
)

public class NotificationDetailSheetHandle(
    public val viewController: UIViewController,
    public val present: (parent: UIViewController) -> Unit,
    public val dismiss: () -> Unit
)

public class AddCardSheetHandle(
    public val viewController: UIViewController,
    public val present: (parent: UIViewController) -> Unit,
    public val update: (cardNumber: String, cardExpiry: String, isError: Boolean, isLoading: Boolean) -> Unit,
    public val dismiss: () -> Unit
)
