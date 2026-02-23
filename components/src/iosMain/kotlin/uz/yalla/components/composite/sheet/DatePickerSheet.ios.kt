package uz.yalla.components.composite.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import uz.yalla.design.theme.System
import uz.yalla.platform.button.SheetIconButton
import uz.yalla.platform.model.IconType
import uz.yalla.platform.picker.NativeWheelDatePicker
import uz.yalla.platform.sheet.NativeSheet
import uz.yalla.resources.Res
import uz.yalla.resources.register_input_birthdate

@Composable
@Suppress("FunctionName")
actual fun DatePickerSheet(
    state: DatePickerSheetState,
    onEffect: (DatePickerSheetEffect) -> Unit,
) {
    var selectedDate by remember { mutableStateOf(state.startDate) }

    NativeSheet(
        isVisible = state.isVisible,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = System.color.backgroundBase,
        onDismissRequest = { onEffect(DatePickerSheetEffect.Dismiss) }
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                SheetIconButton(
                    onClick = { onEffect(DatePickerSheetEffect.Dismiss) },
                    iconType = IconType.CLOSE
                )

                Text(
                    text = stringResource(Res.string.register_input_birthdate),
                    color = System.color.textBase,
                    style = System.font.body.large.medium
                )

                SheetIconButton(
                    iconType = IconType.DONE,
                    onClick = { onEffect(DatePickerSheetEffect.Select(selectedDate)) }
                )
            }

            NativeWheelDatePicker(
                startDate = state.startDate,
                maxDate = state.maxDate,
                modifier = Modifier.fillMaxWidth().height(280.dp).padding(vertical = 20.dp),
                onDateChanged = { selectedDate = it }
            )
        }
    }
}
