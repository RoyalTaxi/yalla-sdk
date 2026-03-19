# Module maps

Unified Kotlin Multiplatform map library with provider-agnostic API.

This module abstracts Google Maps and MapLibre behind a single composable surface so
the host application can switch map providers at runtime via user preferences. It provides
three map composable tiers (lite, extended, static), a reactive [MapController], and
platform-specific implementations for Android and iOS.

## Architecture

The maps module follows a **provider pattern**:
- **API layer** (`uz.yalla.maps.api`) — provider-agnostic interfaces consumed by feature modules
- **Provider layer** (`uz.yalla.maps.provider`) — Google and Libre implementations of each API interface
- **Switching layer** (`uz.yalla.maps.provider.SwitchingMapProvider`) — runtime delegation based on user preference
- **Compose layer** (`uz.yalla.maps.compose`) — expect/actual map primitives (markers, polylines, circles)
- **Model layer** (`uz.yalla.maps.model`) — platform-neutral map types used by the compose layer

# Package uz.yalla.maps.api

Provider-agnostic map API surface: [MapProvider], [MapController], [LiteMap], [ExtendedMap],
[StaticMap], and [MapScope].

# Package uz.yalla.maps.api.model

Immutable data models for the API layer: [CameraPosition], [MarkerState], [MapCapabilities],
and [MapStyle].

# Package uz.yalla.maps.model

Platform-neutral map primitives used by the compose layer: [LatLng], [LatLngBounds],
[CameraPosition], [MapType], [MapProperties], [MapUiSettings], [Cap], and [JointType].

# Package uz.yalla.maps.compose

Expect/actual Compose map composables: [GoogleMap], [Marker], [Polyline], [Circle],
[CameraPositionState], [MarkerState], [BitmapDescriptor], and [GoogleMapComposable].

# Package uz.yalla.maps.config

Map constants (zoom levels, bounding box, style URLs) and platform-specific configuration
factories for MapLibre gesture, ornament, and render options.

# Package uz.yalla.maps.config.platform

Platform-specific MapLibre configuration: [getPlatformGestures], [getDisabledGestures],
[getPlatformOrnamentOptions], and [getPlatformRenderOptions].

# Package uz.yalla.maps.di

Koin dependency injection module and [MapDependencies] contract for host-app integration.

# Package uz.yalla.maps.util

Geo-math utilities: coordinate conversions, [haversineDistance], heading normalization,
bounding-box construction, and [PaddingValues] arithmetic.

# Package uz.yalla.maps.provider

Runtime-switching map provider and controller that delegates to Google or Libre based on
user preference.

# Package uz.yalla.maps.provider.google

Google Maps implementation of the API interfaces: [GoogleMapProvider], [GoogleMapController],
[GoogleLiteMap], [GoogleExtendedMap], [GoogleStaticMap], and shared composable helpers.

# Package uz.yalla.maps.provider.google.component

Google Maps overlay components: [RouteLayer], [LocationsLayer], and [LocationIndicator].

# Package uz.yalla.maps.provider.libre

MapLibre implementation of the API interfaces: [LibreMapProvider], [LibreMapController],
[LibreLiteMap], [LibreExtendedMap], [LibreStaticMap], and shared composable helpers.

# Package uz.yalla.maps.provider.libre.component

MapLibre overlay components: [RouteLayer], [LocationsLayer], and [LocationIndicator].

# Package uz.yalla.maps.provider.common

Shared map utilities across providers: style constants, dimension tokens, initialization
state machine, camera/location effects, and [UserLocationPainter].
