package uz.yalla.media.picker

import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerConfigurationSelectionOrdered
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol

/**
 * Creates a [PHPickerViewController] configured for the given [selection] mode.
 *
 * The picker is set to images-only with ordered selection. For [SelectionMode.Multiple]
 * the limit is taken from [SelectionMode.Multiple.maxSelection]; for [SelectionMode.Single]
 * the limit is hard-coded to 1.
 *
 * @param delegate  Delegate receiving the picker results.
 * @param selection Single or multiple image selection mode.
 * @return A configured [PHPickerViewController] ready for presentation.
 * @since 0.0.1
 */
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
