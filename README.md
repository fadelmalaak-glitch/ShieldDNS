# ShieldDNS 0.1-test
Baseline Android test build for OPPO Reno 15 / ColorOS 16.

Purpose: verify that local-VPN DNS filtering works without breaking normal connectivity before enabling strict/anti-tamper controls.

Default upstream: CleanBrowsing Family Filter (185.228.168.168).

## Build
Open the project in a current Android Studio, let Gradle sync, then Build > Build APK(s).

## Test order
1. Install APK.
2. Tap START STRONG FILTER and approve Android VPN permission.
3. Confirm ordinary sites/apps work.
4. Verify adult domains are blocked and Google/Bing SafeSearch behavior.
5. Test Wi-Fi and mobile data.
6. Reboot and note that v0.1 does not yet enforce always-on/lockdown.

## Deliberately NOT in v0.1
- Device Owner / uninstall prevention
- PIN held by trusted person
- Always-on/lockdown enforcement
- anti-tamper settings protection
- browser/app bypass hardening
These are phase 2 only after v0.1 connectivity passes.
