# Common Patterns

> Recipe book. "I want to do X" → "Here's how."

## How do I add a new API call?

```kotlin
// In the feature's repository (not in SDK — SDK provides safeApiCall):
suspend fun getOrders(): Either<List<Order>, DataError.Network> =
    safeApiCall<ApiResponse<List<OrderDto>>> {
        httpClient.get("orders")
    }.mapSuccess { response ->
        response.result?.map { it.toOrder() }.orEmpty()
    }
```

Key points:
- Always return `Either<D, DataError.Network>`
- Use `safeApiCall` — it handles HTTP errors, timeouts, serialization, guest mode
- Use `mapSuccess` to transform the response
- Set `isIdempotent = true` for GET requests to enable retry

## How do I read/write preferences?

```kotlin
// Reading (reactive):
val userName: Flow<String> = userPreferences.firstName

// Writing (fire-and-forget):
userPreferences.setFirstName("Islom")
```

Preferences are defined as interfaces in `core/contract/preferences/` and implemented
in `data/local/`. New preference keys go in `PreferenceKeys.kt`.

## How do I handle errors in a ViewModel?

```kotlin
class MyViewModel(...) : ContainerHost<MyState, MyEffect> {
    fun loadData() = intent {
        repository.getData()
            .onSuccess { data -> reduce { state.copy(data = data) } }
            .onFailure { error -> postSideEffect(MyEffect.ShowError(error)) }
    }
}
```

## How do I use design tokens?

```kotlin
// Colors:
Text(color = System.color.text.base)
Box(modifier = Modifier.background(System.color.background.secondary))

// Fonts:
Text(style = System.font.title.base)
Text(style = System.font.body.small.medium)

// In a component's Defaults object:
object MyComponentDefaults {
    fun colors() = MyComponentColors(
        container = System.color.background.secondary,
        content = System.color.text.base,
    )
}
```

## How do I create a new UI component?

See [`02-COMPONENT-GUIDE.md`](02-COMPONENT-GUIDE.md) for the full guide. Quick checklist:

1. Decide: `primitives/` (building block) or `composites/` (composes from others)
2. Create `{Component}Colors` + `{Component}Dimens` (both `@Immutable`)
3. Create `{Component}Defaults` object with `colors()` and `dimens()` factories
4. Create the `@Composable fun {Component}(...)` with correct parameter ordering
5. Add KDoc with `@param`, `@return`, `@since`
6. Write tests (at minimum: Colors/Dimens equality)
7. Add to module's `MODULE.md`

## How do I add a map overlay?

```kotlin
// Inside an ExtendedMap content lambda:
ExtendedMap(
    controller = mapController,
    onMapReady = { /* ... */ }
) {
    // Add markers:
    Marker(
        state = MarkerState(position = LatLng(41.3, 69.3)),
        icon = rememberBitmapDescriptor(Res.drawable.ic_pin),
    )
    
    // Add polyline:
    Polyline(
        points = routePoints,
        color = MapColors.Primary,
        width = MapDimens.ROUTE_WIDTH,
    )
}
```

## How do I switch between Google Maps and MapLibre?

The switch is automatic based on user preference stored in `InterfacePreferences.mapKind`.
`SwitchingMapProvider` handles runtime switching. You never directly instantiate Google or Libre providers.

## How do I publish a new SDK version?

See [`04-PUBLISHING.md`](04-PUBLISHING.md). Quick version:

1. Bump `yalla.sdk.version` in `gradle.properties`
2. Push to `main` → GitHub Actions publishes
3. Update `yalla-sdk` version in YallaClient's `libs.versions.toml`
