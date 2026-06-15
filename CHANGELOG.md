# Changelog

All notable changes to the Yalla SDK (core) are documented here. Versions map to
the unified `yalla.sdk.version` published to GitHub Packages.

> Releases publish only from commits whose message starts with `release(` — see
> `.github/workflows/publish.yml`. A non-`release(` commit (e.g. `chore(release):`)
> will NOT publish, even though the workflow run reports success.

## 1.0.0-alpha17

- `components`: ellipsize the single-title `LocationButton` title to one line (#45).
- `components`: ellipsize each destination title in the multi-destination
  `LocationButton` flow to one line so a long address no longer wraps and grows
  the taxi sheet height (#46).
