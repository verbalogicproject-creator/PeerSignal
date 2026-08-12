# System Context for Google Stitch
You are designing the UI/UX for **PeerSignal**, a highly technical Android application that acts as an "Edge Specialist Factory." The app trains 1-5M parameter machine learning models (Specialists) locally on the phone's CPU/GPU overnight.

# Aesthetic Requirements (MANDATORY)
1. **Swiss / Leica Dark Editorial:** The design must be anti-hype, minimalist, and brutalist. Zero rounded corners (or extremely tight 2px radiuses), zero drop shadows, zero social media clichés. 
2. **Color Palette:** Pure black (`#000000`), stark white (`#FFFFFF`), and technical accents like Leica Red (`#E22639`) or Terminal Amber (`#FFB000`) for active states.
3. **Typography:** Use strict monospace for data/logs (e.g., JetBrains Mono, Roboto Mono) and a highly legible geometric sans-serif for UI (e.g., Inter, Helvetica, Roboto).
4. **Data-Dense:** Do not hide information behind unnecessary tabs. Assume the user is an expert systems engineer who wants to see hashes, loss graphs, and thermal temperatures at a glance.

# Required Screens to Design & Export

## 1. The Global Hub (Main Dashboard)
*   **Header:** Displays the current Active Engine (`[TERMINAL] Companion Python` vs `[NATIVE] C++ NDK Factory`).
*   **Telemetry Bar:** Live Android Thermal Headroom (`THERMAL_STATUS_NORMAL`, `LIGHT`, `SEVERE`), Battery %, and RAM usage.
*   **Quick Actions:** "Import Graph Snapshot", "Build New Specialist", "View Pack Registry".

## 2. The Training Forge (Active Training View)
*   **Status Panel:** Large progress indicator for the current Epoch/Step. 
*   **Live Metrics:** A minimalist line chart showing training loss vs. validation loss.
*   **Controls:** Brutalist, mechanical buttons to **[ PAUSE ]**, **[ RESUME ]**, or **[ CANCEL ]** the training loop.
*   **Event Log:** A scrolling monospace terminal view at the bottom showing exact framework outputs (e.g., `Epoch 14: loss 0.4322 | Thermal limit reached, cooling...`).

## 3. The Qualification Lab (Evaluation View)
*   **Comparison Matrix:** A table comparing the newly trained model (`MiniULTRA-SKG-v0`) against baselines (`BM25`, `Graph Heuristics`). 
*   **Metrics:** Clear readouts of `Hits@10` and `MRR` (Mean Reciprocal Rank). 
*   **Gate Decision:** A massive **[ PROMOTE TO SPECIALIST ]** or **[ DISCARD ]** button based on whether it passed the hurdle rate.

## 4. Specialist Pack Registry (The Output)
*   A list of completed models. 
*   Each card must show the Model Architecture, Parameter Count, Dataset Hash, and Thermal Receipt.
*   **Action:** A button to generate the Offline Handshake QR/Token to share the model with a peer.

# Output Format
Please generate the complete Jetpack Compose-compatible design system and UI screens, and export the entire package as a `.zip` file containing the Compose Kotlin files and resources.
