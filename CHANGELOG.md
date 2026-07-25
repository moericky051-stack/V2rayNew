# Changelog - Secure V2Ray VPN

All notable changes to this project will be documented in this file.

## [1.0.0] - 2026-07-25

### Added
- **Native Android App (Jetpack Compose & Kotlin)**:
  - Cyber Dark theme UI with live speed gauges (download/upload) and session duration timer.
  - Multi-protocol V2Ray support: VLESS, VMess, Trojan, Shadowsocks, Reality protocol.
  - V2Ray Config Import Wizard supporting URL links (`vless://`, `vmess://`, `trojan://`, `ss://`), QR Code scanner simulation, JSON config paste, and manual setup.
  - Built-in Ping Tester with ping speed ratings.
  - In-App Admin Mode for VPS Server Management and user settings.
  - Foreground VPN Service with Android `BIND_VPN_SERVICE` integration.
- **Backend REST API (Node.js & Express)**:
  - Endpoints for VPS nodes listing, server node addition, and subscription management.
- **Web Admin Panel**:
  - Interactive dashboard stats, server management table, and user management interface.
- **Xray VPS Deployment Guide & Automation**:
  - Step-by-step setup script for Ubuntu 22.04/24.04 LTS, 3x-UI Panel, Let's Encrypt SSL, and UFW firewall rules.
- **CI/CD & GitHub Actions**:
  - Automated APK build workflow `.github/workflows/android-apk-build.yml`.
