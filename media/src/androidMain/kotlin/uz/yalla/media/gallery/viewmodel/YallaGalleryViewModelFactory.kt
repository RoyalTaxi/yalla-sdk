@file:Suppress("UNCHECKED_CAST")

package uz.yalla.media.gallery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.yalla.media.gallery.repository.YallaGalleryRepository

internal class YallaGalleryViewModelFactory(
    private val yallaGalleryRepository: YallaGalleryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(YallaGalleryViewModel::class.java)) {
            YallaGalleryViewModel(this.yallaGalleryRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
}
