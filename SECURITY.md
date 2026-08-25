# Security Policy

## Supported Versions

| Version | Supported          |
|---------|--------------------|
| 1.x     | :white_check_mark: |

## Reporting a Vulnerability

If you discover a security vulnerability in ApiHub, please do **not** open a public issue.

Instead, send a private report to the project maintainers by opening a
[GitHub Security Advisory](https://github.com/blrain3/ApiHub/security/advisories/new)
or contacting the repository owner directly.

We will respond within 48 hours and work with you to:
1. Confirm the vulnerability
2. Develop and test a fix
3. Release a patch and disclose responsibly

## Scope

- API gateway authentication & authorization
- Open API signature verification (HMAC, nonce replay)
- Database credential handling
- JWT token management
- Third-party API key storage (e.g. Amap weather key)

## Out of Scope

- Dependencies with known CVEs (please report upstream)
- The frontend build toolchain (Vite, npm)