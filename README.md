# PeerSignal: Context OS & KG-RAG Mobile Workbench

PeerSignal is a native Android application built for solo AI engineers, systems developers, and open-source researchers. It acts as an interactive mobile research lab and "collaborator signal flare". 

Unlike standard social networks, PeerSignal is strictly designed for high-signal, zero-noise architectural discussions (AST chunking, Knowledge Graphs, RAG pipelines). It bridges the gap between deep local Python architectures and a mobile workbench.

## Architecture & Tech Stack
- **Platform:** Android 14+ (API 34+)
- **UI:** Jetpack Compose (Material 3 with custom Swiss/Leica Dark Editorial Theme)
- **Language:** Kotlin 2.0+
- **Architecture:** Clean Architecture + MVVM + UDF
- **Local Storage:** Room Database (Offline-first)
- **Networking:** Ktor + Kotlinx Serialization
- **Dependency Injection:** Dagger Hilt
- **CI/CD:** GitHub Actions (Automated release APK builds)

## The Connection Layer
We bypass heavy in-app chat infrastructure. Instead, the app acts as a router:
1. **The Handshake:** Secure 12-char cryptographic tokens generated locally.
2. **The Medium:** Deep-linking directly to GitHub Discussions/PRs or WebRTC for actual architectural debates.
3. **The Proxy:** A FastAPI bridge (`test_harness/proxy.py`) running on the host machine/Termux that pipes data from advanced Python repos (`kg-factory`, `universal_parser`) directly into the Android Kotlin UI.

## Getting Started
1. Open this project in Android Studio (or run `./gradlew assembleDebug` locally if the Android SDK is installed).
2. Start the local python proxy:
   ```bash
   bash run_proxy.sh
   ```
3. Run the JVM runtime test:
   ```bash
   bash run_test.sh
   ```
