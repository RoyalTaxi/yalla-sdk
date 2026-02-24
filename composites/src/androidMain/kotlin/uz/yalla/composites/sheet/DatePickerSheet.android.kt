package uz.yalla.composites.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.darkokoa.datetimewheelpicker.WheelDatePicker
import dev.darkokoa.datetimewheelpicker.core.WheelPickerDefaults
import org.jetbrains.compose.resources.stringResource
import uz.yalla.design.theme.System
import uz.yalla.platform.button.SheetIconButton
import uz.yalla.platform.model.IconType
import uz.yalla.resources.Res
import uz.yalla.resources.register_input_birthdate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("FunctionName")
actual fun DatePickerSheet(
    state: DatePickerSheetState,
    onEffect: (DatePickerSheetEffect) -> Unit,
) {
    var snappedDate by remember { mutableStateOf(state.startDate) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    AnimatedSheet(
        isVisible = state.isVisible,
        sheetState = sheetState,
        onDismissRequest = { onEffect(DatePickerSheetEffect.Dismiss) },
        colors = AnimatedSheetDefaults.colors(container = System.color.backgroundBase),
        dimens = AnimatedSheetDefaults.dimens(shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
        dragHandle = null,
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
                    onClick = { onEffect(DatePickerSheetEffect.Select(snappedDate)) }
                )
            }

            WheelDatePicker(
                startDate = state.startDate,
                size = DpSize(360.dp, height = 280.dp),
                modifier =
                    Modifier.padding(
                        vertical = 20.dp,
                        horizontal = 16.dp
                    ),
                maxDate = state.maxDate,
                rowCount = 5,
                textStyle = System.font.title.base,
                textColor = System.color.textBase,
                onSnappedDate = { snappedDate = it },
                selectorProperties = WheelPickerDefaults.selectorProperties(false)
            )
        }
    }
}
