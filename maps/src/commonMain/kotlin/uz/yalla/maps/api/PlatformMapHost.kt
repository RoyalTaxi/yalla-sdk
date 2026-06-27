package uz.yalla.maps.api

/**
 * Opaque, framework-agnostic handle to the platform-rendered map surface.
 *
 * commonMain never inspects it; each platform's `MapHost` actual narrows it to its own subtype
 * ([AndroidMapController] on Android, `IosMapController` on iOS). This is the *only* platform seam
 * type — it replaces the unsafe `MapController as AndroidMapController` / `as IosMapController` casts
 * that made "any [MapController] renders in [MapView]" a render-time lie enforced by `ClassCastException`.
 *
 * A controller exposes its current host via [MapController.platformHost]; switching controllers
 * re-emit the active backend's host when the provider changes.
 */
public interface PlatformMapHost
