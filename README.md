# Secure V2Ray VPN — Complete Monorepo System

A high-performance V2Ray VPN system featuring a native Android client app built with Kotlin and Jetpack Compose, Express Node.js Backend API, Web Admin Dashboard, Firebase Firestore rules, Xray VPS installation scripts, and CI/CD GitHub Actions workflows.

---

## Ecosystem Architecture

- **`app/`**: Native Android application (Kotlin, Jetpack Compose, Material 3 Cyber Dark UI, Android `VpnService` implementation).
- **`backend/`**: Express / Node.js REST API for VPS server nodes management and webhook handlers.
- **`web-admin/`**: HTML/CSS/JS Web Admin Dashboard for server nodes and system settings.
- **`VPS_DEPLOYMENT_GUIDE.md`**: Ubuntu VPS installation guide with Xray-Core, 3x-UI panel, SSL certificate generation, and UFW firewall configuration.
- **`.github/workflows/android-apk-build.yml`**: GitHub Actions automated APK compilation and release publishing.

---

## Setup & Deployment Instructions

### 1. How to Clone Repository
```bash
git clone https://github.com/your-username/Secure-V2Ray-VPN.git
cd Secure-V2Ray-VPN
```

### 2. How to Configure Firebase
1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/).
2. Add an Android app with package name `com.example`.
3. Download `google-services.json` and place it in the `app/` directory.
4. Deploy the Security Rules provided in `firestore.rules` to Firebase Firestore.

### 3. How to Connect VPS Server & 3x-UI API
1. Provision a VPS running Ubuntu 20.04 / 22.04 / 24.04 LTS.
2. Run the 3x-UI panel installation script:
   ```bash
   bash <(curl -Ls https://raw.githubusercontent.com/mhsanaei/3x-ui/master/install.sh)
   ```
3. Issue an SSL certificate:
   ```bash
   x-ui cert --domain vps.yourdomain.com
   ```
4. Configure inbounds for VLESS, VMess, Trojan, or Reality protocols in 3x-UI Web Panel (`http://YOUR_VPS_IP:2053`).

### 4. How to Add Xray Server to App & Admin Panel
- **From App**: Navigate to **Servers** -> tap **+ (FAB)** -> select **URL Link**, **QR Code**, or **Manual** to import `vless://`, `vmess://`, `trojan://`, or `ss://` configs.
- **From Web Admin / Backend**: Use `POST /api/admin/servers` on the Node.js backend API.

### 5. How to Build Release APK
```bash
# Grant execute permissions
chmod +x gradlew

# Build Debug or Release APK
./gradlew assembleDebug
# or
./gradlew assembleRelease
```
The compiled APK will be located at `app/build/outputs/apk/debug/app-debug.apk` (or `release/app-release.apk`).

### 6. How to Publish on Google Play Store
1. Generate a Release Signing Key (Keystore):
   ```bash
   keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-alias
   ```
2. Build Android App Bundle (AAB):
   ```bash
   ./gradlew bundleRelease
   ```
3. Upload `app/build/outputs/bundle/release/app-release.aab` to Google Play Console under Internal Testing / Production Release track.

---

## License
MIT License - See [LICENSE](LICENSE) for details.
