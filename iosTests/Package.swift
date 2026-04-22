// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "YallaSnapshotTests",
    platforms: [.iOS(.v16)],
    products: [],
    dependencies: [
        .package(
            url: "https://github.com/pointfreeco/swift-snapshot-testing.git",
            from: "1.19.2"
        ),
    ],
    targets: [
        .testTarget(
            name: "YallaSnapshotScaffoldTests",
            dependencies: [
                .product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
            ]
        ),
    ]
)
