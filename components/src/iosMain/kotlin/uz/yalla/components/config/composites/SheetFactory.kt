package uz.yalla.components.config.composites

import platform.Foundation.NSDate
import platform.UIKit.UIViewController
import uz.yalla.components.composites.item.ActionableItemModel
import uz.yalla.components.composites.item.SelectableItemModel

interface SheetFactory {
    fun createContent(
        fullHeight: Boolean,
        sheetSwipeEnabled: Boolean,
        contentController: UIViewController,
        onDismissRequest: () -> Unit
    ): ContentSheetHandle

    fun createConfirmation(
        imageResource: String,
        isDark: Boolean,
        title: String,
        description: String,
        actionText: String,
        dismissEnabled: Boolean,
        onAction: () -> Unit,
        onDismissRequest: () -> Unit
    ): ConfirmationSheetHandle

    fun createSelection(
        title: String,
        items: List<SelectableItemModel>,
        selectedId: String?,
        onSelect: (id: String) -> Unit,
        onDismissRequest: () -> Unit
    ): SelectionSheetHandle

    fun createAction(
        title: String,
        items: List<ActionableItemModel>,
        onAction: (id: String) -> Unit,
        onDismissRequest: () -> Unit
    ): ActionSheetHandle

    fun createDatePicker(
        startDate: NSDate,
        minDate: NSDate?,
        maxDate: NSDate?,
        title: String?,
        dismissEnabled: Boolean,
        onSelect: (NSDate) -> Unit,
        onDismissRequest: () -> Unit
    ): DatePickerSheetHandle

    fun createVerification(
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
        onCodeChange: (String) -> Unit,
        onConfirm: () -> Unit,
        onResend: () -> Unit,
        onCodeComplete: (String) -> Unit,
        onDismissRequest: () -> Unit
    ): VerificationSheetHandle
}

class ContentSheetHandle(
    val viewController: UIViewController,
    val present: (parent: UIViewController) -> Unit,
    val dismiss: () -> Unit,
    val updateContentHeight: (height: Double) -> Unit
)

class ConfirmationSheetHandle(
    val viewController: UIViewController,
    val present: (parent: UIViewController) -> Unit,
    val dismiss: () -> Unit
)

class SelectionSheetHandle(
    val viewController: UIViewController,
    val present: (parent: UIViewController) -> Unit,
    val dismiss: () -> Unit
)

class ActionSheetHandle(
    val viewController: UIViewController,
    val present: (parent: UIViewController) -> Unit,
    val dismiss: () -> Unit
)

class DatePickerSheetHandle(
    val viewController: UIViewController,
    val present: (parent: UIViewController) -> Unit,
    val dismiss: () -> Unit
)

class VerificationSheetHandle(
    val viewController: UIViewController,
    val present: (parent: UIViewController) -> Unit,
    val update: (
        code: String,
        description: String,
        isError: Boolean,
        isLoading: Boolean,
        resendText: String,
        resendEnabled: Boolean
    ) -> Unit,
    val dismiss: () -> Unit
)
