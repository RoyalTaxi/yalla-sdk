# Publishing

> How to release a new SDK version.

## Version Scheme

```
0.0.7-alpha02
│ │ │ │
│ │ │ └── Pre-release increment (01, 02, ...)
│ │ │
│ │ └──── Patch (bug fixes)
│ └────── Minor (new features, non-breaking)
└──────── Major (breaking API changes)
```

Current: **alpha** — not yet production-stamped. Beta comes when both iOS and Android
UX are verified smooth in YallaClient.

## How to Publish

### 1. Bump Version
Edit `gradle.properties`:
```properties
yalla.sdk.version=0.0.7-alpha03
```

### 2. Push to Main
```bash
git add gradle.properties
git commit -m "chore: bump SDK version to 0.0.7-alpha03"
git push origin main
```

GitHub Actions will automatically:
- Build all modules
- Run tests
- Publish to GitHub Packages

### 3. Update Consumer
In YallaClient's `gradle/libs.versions.toml`:
```toml
yalla-sdk = "0.0.7-alpha03"
```

## Important Rules

- **GitHub Packages is immutable** — you cannot overwrite a published version. Always bump before publishing.
- **Never run `./gradlew publish` locally** — always push to main and let CI handle it.
- **JDK 21 required** — CI uses JDK 21. Local builds should too (`JAVA_HOME` set globally).
- **Credentials** — CI uses `GITHUB_ACTOR` and `GITHUB_TOKEN` secrets. Local publishing needs these as env vars.

## When to Bump

| Change type | Version part | Example |
|-------------|-------------|---------|
| Bug fix | Pre-release | alpha02 → alpha03 |
| New component | Pre-release | alpha03 → alpha04 |
| Breaking API change | Minor + reset pre-release | 0.0.7-alpha → 0.0.8-alpha01 |
| Production release | Drop pre-release | 0.0.8-alpha → 0.0.8 |
