package uz.yalla.network

/**
 * A TLS certificate pin: a host (or wildcard pattern) bound to one or more **Subject Public Key Info
 * (SPKI)** hashes. Pinning constrains which server certificates the client will trust for that host,
 * defending against a forged-but-CA-valid certificate (a compromised or coerced certificate authority,
 * or a corporate/MITM proxy) — TLS validity alone does not catch those.
 *
 * ## How the host supplies pins
 * Pins are **deployment configuration, not source-controlled constants** — they describe *your* backend's
 * real keys, which this SDK cannot know or invent. The integrating app passes them in via
 * [NetworkConfig.certificatePins]. Leaving that list empty (the default) **disables pinning** and the
 * client falls back to the platform's normal CA trust, so nothing breaks for an integrator who has not
 * opted in.
 *
 * A [pin] is a base64-encoded SHA-256 hash of the certificate's SPKI, prefixed with `sha256/` — the same
 * format OkHttp and Ktor's Darwin `CertificatePinner` use, e.g.
 * `sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=`. To obtain the real value for a host, either:
 *  - compute it from the served leaf/intermediate cert
 *    (`openssl s_client -connect host:443 | openssl x509 -pubkey -noout |
 *      openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | openssl enc -base64`), or
 *  - turn pinning on with a deliberately wrong pin and read the real chain's hashes from the failure log
 *    (the Darwin `CertificatePinner` prints the peer chain on mismatch).
 *
 * **Always pin at least two keys** — the live key plus an offline backup — so a routine certificate
 * rotation does not brick every installed app. Treat the pin set as something the server's TLS owner
 * signs off on and rotates deliberately.
 *
 * @property host the host this pin applies to. Accepts an exact host (`api.example.com`), a single-label
 *   wildcard (`*.example.com`, matches exactly one left-most label), or an any-depth wildcard
 *   (`**.example.com`, matches the apex and any subdomain). Matching mirrors Ktor's Darwin engine so the
 *   two platforms agree.
 * @property pins one or more `sha256/<base64-SPKI>` hashes. Any one matching a certificate in the
 *   presented chain satisfies the pin, which is what lets a backup key cover a rotation.
 */
public data class CertificatePin(
    val host: String,
    val pins: List<String>
)

/**
 * Whether [hostname] is covered by the pin [pattern], mirroring Ktor's Darwin engine so the Android and
 * iOS engines agree on a pattern's reach. Both arguments are compared case-insensitively. Supported
 * patterns: an exact host (`api.example.com`), a single-label wildcard (`*.example.com` — matches exactly
 * one left-most label), and an any-depth wildcard (`**.example.com` — matches the apex and any subdomain).
 *
 * Lives in `commonMain` so it is expressed once and unit-tested directly; the Android engine consumes it,
 * while the Darwin engine applies the equivalent rule inside Ktor's own `CertificatePinner`.
 */
internal fun pinPatternMatchesHost(pattern: String, hostname: String): Boolean {
    val canonicalPattern = pattern.lowercase()
    val canonicalHost = hostname.lowercase()
    return when {
        // `**.` matches the apex and any depth of subdomain (no required prefix).
        canonicalPattern.startsWith("**.") -> {
            val suffix = canonicalPattern.substring(3)
            canonicalHost == suffix || canonicalHost.endsWith(".$suffix")
        }
        // `*.` matches exactly one left-most label (a prefix is required, and only one).
        canonicalPattern.startsWith("*.") -> {
            val suffix = canonicalPattern.substring(2)
            val prefixLength = canonicalHost.length - suffix.length - 1
            canonicalHost.endsWith(".$suffix") &&
                prefixLength > 0 &&
                canonicalHost.lastIndexOf('.', prefixLength - 1) == -1
        }

        else -> canonicalHost == canonicalPattern
    }
}
