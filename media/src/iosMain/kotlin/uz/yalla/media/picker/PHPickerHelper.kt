package uz.yalla.media.picker

import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerConfigurationSelectionOrdered
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol

internal fun createPHPickerViewController(
    delegate: PHPickerViewControllerDelegateProtocol,
    selection: SelectionMode
): PHPickerViewController {
    val config =
        when (selection) {
            is SelectionMode.Multiple ->
                PHPickerConfiguration().apply {
                    setSelectionLimit(selection.maxSelection.toLong())
                    setFilter(PHPickerFilter.imagesFilter)
                    setSelection(PHPickerConfigurationSelectionOrdered)
                }
            SelectionMode.Single ->
                PHPickerConfiguration().apply {
                    setSelectionLimit(1)
                    setFilter(PHPickerFilter.imagesFilter)
                    setSelection(PHPickerConfigurationSelectionOrdered)
                }
        }

    return PHPickerViewController(configuration = config).apply {
        this.delegate = delegate
    }
}
