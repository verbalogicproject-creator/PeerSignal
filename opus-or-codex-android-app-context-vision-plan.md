# PeerSignal: Context Vision & Architecture Plan (For Claude-Code / Codex)

**To any AI Agent (Claude, Codex, Antigravity) reading this file:** 
This document provides the high-level philosophical context and technical boundaries of this codebase. You are operating inside the repository for **PeerSignal**, a native Android application.

## 1. The Core Philosophy
The User is a solo systems engineer building incredibly complex architectures (`Context OS`, Knowledge Graphs, RAG pipelines) across multiple environments (Ubuntu, Termux). This work is deeply isolating. 
PeerSignal is a "ham radio for elite developers". It is an anti-social-media tool. There are no followers, no algorithmic feeds, and no generic UX. It is a sterile, brutalist, high-signal workbench designed to be distributed to a maximum of 20 peers via manual cryptographic invite tokens. 

**Aesthetic:** Swiss/Leica Dark Editorial. Minimalist, high-contrast (Slate, Emerald), monospace-heavy (JetBrains Mono). Never add unnecessary drop shadows, rounded corners, or "friendly" copy. 

## 2. Technical Boundaries & Stack
*   **Android Framework:** Jetpack Compose ONLY. Do not use XML layouts.
*   **Language:** Kotlin 2.0+. Strictly enforce Coroutines and `StateFlow` for reactivity.
*   **Architecture:** Clean Architecture (UI -> Domain -> Data). 
*   **Single Source of Truth:** Room Database (`PeerDatabase.kt`). The UI strictly observes Room.
*   **Dependency Injection:** Dagger Hilt.
*   **Networking:** Ktor.

## 3. The Proxy Architecture (Crucial Context)
The Android app is just the frontend. The real heavy lifting (AST parsing, Knowledge Graph construction) happens in Python architectures living on the user's host machine or Termux environment (e.g., `kg-factory`, `universal_parser`, `codex-aware`). 
To bridge this, we use a **Local Proxy Node**. 
*   The Android app (`ParserApiClient`) fires JSON payloads to `localhost:8000/api/v1/sandbox/analyze`.
*   A Python FastAPI server (`test_harness/proxy.py`) catches these payloads, runs them through the local Python KG tools, and returns the graph nodes to the Kotlin UI.

## 4. Current State & Where You Can Help
The core MVP is structurally complete:
1. Hilt, Room, Ktor, and Compose NavGraph are wired up.
2. The `InviteScreen`, `ConnectScreen`, `SandboxScreen`, and `BeaconStreamScreen` are built.
3. GitHub Actions is configured to build the `.apk` on every push to `main`.
4. A pure Kotlin JVM test (`run_test.sh`) is wired up to test the networking without needing the Android SDK.

**Your Objective when modifying this repo:**
*   Ensure any new UI screens adhere to the Dark Editorial Theme.
*   If you expand the Sandbox to parse richer Knowledge Graphs, ensure you update both the Kotlin Data models (`BeaconSignalEntity`) and the Python FastAPI proxy simultaneously.
*   Never break the CI/CD pipeline. 
*   Always preserve the `[Discuss Architecture]` deep-link logic; we do not want to build an in-app chat. Route everything to GitHub/Matrix.
