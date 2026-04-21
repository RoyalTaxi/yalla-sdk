# Security Policy

## Supported Versions

Only the latest minor release line is supported for security fixes. As of `1.0.x`, that is the entire `1.0.x` line.

Once `1.1.0` ships, security fixes for `1.0.x` will be discontinued — the last `1.0.x` patch is the last supported version of that line.

## Reporting a Vulnerability

**Please do not open a public GitHub issue for a security vulnerability.**

Instead, email details to **security@yalla.uz** (or, if that address is unavailable, `i.sheraliyev@royaltaxi.uz` with "SECURITY:" in the subject line).

Include:

- A short description of the issue
- Steps to reproduce, or a proof-of-concept
- The affected version(s)
- Your assessment of severity and impact

### Response SLA

- **Acknowledgement**: within 72 hours of receipt.
- **P0 fix** (critical: active exploit, data loss, credential exposure): patch shipped within **7 days**.
- **P1 fix** (high: significant impact, no known active exploit): patch shipped within **30 days**.
- **Public disclosure**: coordinated with the reporter, after the patch ships.

### Scope

This policy covers:

- Any module published under `uz.yalla.sdk:*`.
- The repository itself (e.g., publishing pipeline compromise).

Out of scope:

- Vulnerabilities in the YallaClient consumer app — report separately to Yalla.
- Vulnerabilities in third-party dependencies — report upstream; we will bump the dependency when a fixed version ships.
