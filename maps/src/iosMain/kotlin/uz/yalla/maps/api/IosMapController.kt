package uz.yalla.maps.api

import platform.UIKit.UIViewController

public interface IosMapController : PlatformMapHost {
    public fun createViewController(): UIViewController
}
