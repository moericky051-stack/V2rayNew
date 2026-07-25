# VPS Server Deployment Guide for Secure V2Ray VPN

This guide provides step-by-step instructions to set up an Ubuntu VPS server with Xray-core and 3x-UI Panel to serve as a node in the Secure V2Ray VPN ecosystem.

---

## 1. System Requirements
- **OS**: Ubuntu 22.04 / 24.04 LTS (64-bit)
- **CPU**: 1 vCPU or higher
- **RAM**: 1 GB or higher
- **Ports**: 80, 443, 2053 (Panel), 8388

---

## 2. Step 1 — Update VPS System Packages
```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y curl wget git socat ufw
```

---

## 3. Step 2 — Install 3x-UI Panel & Xray Core
Execute the official 1-click bash installation script:
```bash
bash <(curl -Ls https://raw.githubusercontent.com/mhsanaei/3x-ui/master/install.sh)
```
During installation:
1. Choose a custom admin username & password.
2. Choose a web panel port (default: `2053`).
3. Set panel URL path root (e.g. `/admin`).

---

## 4. Step 3 — Issue Free SSL Certificate (Let's Encrypt)
Generate TLS/Reality SSL certificates for your domain:
```bash
x-ui cert --domain vps.yourdomain.com
```

---

## 5. Step 4 — Firewall Port Configuration
Allow essential V2Ray & Admin ports:
```bash
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 2053/tcp
sudo ufw enable
```

---

## 6. Step 5 — Protocol Setup in 3x-UI
Log in to your 3x-UI web panel at `http://your-vps-ip:2053` and create inbounds:

1. **VLESS + WS + TLS**:
   - Port: `443`
   - Network: `ws`
   - Security: `tls`
   - Path: `/vless-ws`

2. **VLESS + Reality (Stealth Mode)**:
   - Port: `443`
   - Network: `tcp`
   - Security: `reality`
   - Target SNI: `www.microsoft.com` or `www.apple.com`
   - Flow: `xtls-rprx-vision`

3. **Trojan + WS**:
   - Port: `8443`
   - Network: `ws`
   - Security: `tls`

---

## 7. Connecting Server to Android App & Web Admin
Copy the generated `vless://` or `vmess://` configuration share link and paste it into the **Import Config** modal in the Secure V2Ray Android app or add it directly to the Web Admin Panel.
