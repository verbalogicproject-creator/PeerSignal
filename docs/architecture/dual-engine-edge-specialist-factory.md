# PeerSignal: Dual-Engine Edge Specialist Factory Architecture

*Status: PROPOSED. Awaiting User Approval.*
*Proposed Location: `docs/architecture/dual-engine-edge-specialist-factory.md`*

## 1. Executive Verdict and Feasibility
**Verdict:** `proposed` | The vision of PeerSignal as an **Edge Specialist Factory** is strictly feasible. 
However, earlier drafts conflated inference hardware (Hexagon) with training capability, overstated the performance of Termux PRoot execution, and incorrectly assumed the APK container itself accelerates arithmetic. 

**Bounded Feasibility:** An APK-native factory is valuable because it unlocks Android’s official Thermal API, raw Vulkan compute via NDK, vendor OpenCL libraries, app-private memory mapping, and WorkManager orchestration. It turns a fragile, heat-throttled shell script into a deterministic, pausing, and resuming mobile appliance. 

## 2. Evidence Ledger
*   `observed`: The repository (`17e4f48`) contains a mock FastAPI endpoint (`test_harness/proxy.py`); it does not currently invoke PyTorch, SciPy, or kg-factory.
*   `observed`: `CodeKGEmbedder` is a proven 1.36M parameter two-layer transformer encoder trained from scratch on this device's CPU in PRoot to epoch 24.
*   `observed`: The device (SM8650) publishes `libOpenCL.so` in `/vendor/etc/public.libraries.txt` making it accessible to NDK apps.
*   `documented`: ULTRA is a relation-conditioning GNN. `ultra_50g` was pretrained for 1M steps on 50 graphs.
*   `documented`: Qualcomm Hexagon and NNAPI (deprecated in API 35) are inference targets, not documented training backends.

## 3. Corrections to Prior Drafts
*   **The proxy is not "working flawlessly":** It is currently a mock returning hardcoded JSON.
*   **Termux did not train 100M+ models:** The proven training target was 1.36M parameters. Phi-4-mini (3.8B) was used strictly for *inference/SDG generation* via llama.cpp.
*   **Hexagon is not a training target:** Backpropagation will target CPU and (pending tests) GPU/Vulkan. Hexagon is strictly for inference deployment.
*   **Hilt is not a mutable switch:** We will inject a `SpecialistEngineRegistry` that routes requests based on persisted user state, not by mutating Dagger bindings.
*   **JNI Data Transfer:** We will pass `IntArray` or raw `ByteBuffer` memory maps, not boxed `List<Int>`.
*   **JNI Test Validity:** Returning `0.45f` proves linkage, not NNTrainer feasibility.

## 4. System/Context Diagram
```text
PeerSignal Android control plane
├── SpecialistEngineRegistry
│   ├── CompanionPythonEngine
│   └── NativeFactoryEngine
├── TrainingRunCoordinator
├── DeviceCapabilityProfiler
├── ThermalAndPowerGovernor
├── SpecialistPackRepository
├── QualificationService
└── SpecialistRouter

CompanionPythonEngine (optional Termux sidecar)
├── authenticated/versioned loopback protocol
├── deterministic dataset compiler
└── PyTorch CPU reference trainer

NativeFactoryEngine (standalone APK path)
├── Kotlin lifecycle orchestration
├── NDK/JNI bridge with zero-copy buffers
├── native CPU reference backend / NNTrainer
├── qualification and calibration
└── inference adapters (CPU, Vulkan, QNN)
```

## 5. Ownership Boundaries
*   **Domain:** Defines `SpecialistEngine` contracts. Agnostic to backend.
*   **Native Engine (Kotlin):** Orchestrates WorkManager, checks thermals, manages `ByteBuffer` memory.
*   **NDK Bridge (C++):** JNI layer. Purely responsible for type conversion, pointers, and crash safety.
*   **Framework (C++):** NNTrainer or Vulkan compute shaders. Does the math. Returns raw losses.

## 6. Domain Contracts & Run State Machine
```kotlin
interface SpecialistEngine {
    val engineId: EngineId
    suspend fun probe(): EngineCapabilities
    suspend fun prepare(request: TrainingRequest): PreparedDataset
    fun train(request: TrainingRequest): Flow<TrainingEvent>
    suspend fun pause(runId: RunId): PauseReceipt
    suspend fun qualify(runId: RunId, suite: QualificationSuite): QualificationReport
    suspend fun export(runId: RunId): SpecialistPackDescriptor
}
```
**State Machine:** `Queued -> Preparing -> Running -> (Cooling/Paused -> Resumed) -> Qualifying -> Completed/Failed`

## 7. Companion Protocol & Security
The current single endpoint must be replaced with a versioned, authenticated REST protocol:
*   `GET /v1/capabilities`, `GET /v1/health`
*   `POST /v1/runs`, `POST /v1/runs/{id}/pause`
*   **Security:** Loopback-only binding (`127.0.0.1`). Explicit capability token negotiation. Do not use open localhost.

## 8. Native/JNI Integration Design
*   `NNTrainer` will be evaluated for operator support (LayerNorm, GELU, Masked Attention). 
*   If NNTrainer cannot support exact weighted InfoNCE loss or relational scatter, we will fallback to a custom CPU reference loop before forcing an incompatible architecture.
*   JNI will use opaque native handles to prevent GC thrashing and memory leaks.

## 9. SpecialistPack Manifest Schema
An exported model is not just weights. It requires:
*   `content_hash`, `manifest_version`, `architecture_type`
*   `source_ledger` (hashes of the exact KG snapshot used)
*   `training_receipt` (epochs, thermal throttling events, duration)
*   `evaluation_report` (held-out MRR, Hits@10)
*   `signature`

## 10. Data Lifecycle & Provenance
*   **Storage:** Room DB holds metadata and run states. Massive datasets and weights belong in app-private File I/O (`/data/data/com.peersignal.app/files/packs/`).
*   **Determinism:** Graph snapshots must be frozen before augmentation. Held-out splits must be aggressively filtered to prevent reverse-edge leakage.

## 11. Separation of Concerns
1.  **Training:** Emits gradients and checkpoints.
2.  **Qualification:** Evaluates checkpoints against frozen validation queries.
3.  **Inference:** Compiles the qualified model to QNN/Hexagon or Vulkan.
4.  **Routing:** Decides if a user query goes to the Specialist, the generic KG traversal, or abstains entirely.

## 12. Thermal, Lifecycle, and Resource Policy
*   **Stage Isolation:** Any llama.cpp/SDG generation processes MUST be fully terminated before training begins.
*   **Governor:** Monitor Android `ThermalManager`. If status is `THERMAL_STATUS_SEVERE`, trigger an explicit `pause()`, dump checkpoint, and wait for `THERMAL_STATUS_LIGHT`.
*   **Concurrency:** Use a dedicated native Worker pool. Never train on `Dispatchers.Default`.

## 13. Phased Implementation Checklist

*   [ ] **Phase 0 (Baseline Freeze):** Define domain contracts. Freeze validation logic. (Current Phase)
*   [ ] **Phase 1 (First-Model Python):** Build `MiniULTRA-SKG-v0` in pure PyTorch as a reference. Prove it beats non-neural baselines.
*   [ ] **Phase 2 (MVP Companion Engine):** Wire the Android UI to the Python protocol. Start, pause, resume, and qualify from the app.
*   [ ] **Phase 3 (JNI Spike):** Build NDK arm64 JNI library. Verify zero-copy buffer passing.
*   [ ] **Phase 4 (Native Framework Spike):** Compile NNTrainer. Execute actual backward pass on CPU. Evaluate unsupported operators.
*   [ ] **Phase 5 (Standalone Native):** Port the 1.36M SpecialistKG. Compare exact fixed-seed loss against Python.
*   [ ] **Phase 6 (GPU Training):** Benchmark Vulkan/OpenCL backward pass vs CPU.
*   [ ] **Phase 7 (Inference):** Export/quantize variants for QNN/Hexagon deployment.
*   [ ] **Phase 8 (Factory UX):** Build the full routing, pack registry, and visualization UX.

## 14. Test / Evaluation Matrix
*   **Host CI:** Compiles Kotlin, compiles CMake/NDK arm64, tests deterministic data splitting.
*   **Physical Device Proof:** Measures Thermal Headroom, OpenCL vendor library availability, QNN execution, JNI peak memory. 

## 15. CI Plan
The current `.github/workflows/android_build.yml` will be updated to include NDK compilation. It cannot prove runtime GPU behavior; that requires a physical `adb` test report.

## 16. Risk Register
*   **Relational Scatter in C++:** `MiniULTRA-SKG-v0` requires graph message passing. NNTrainer may lack PyG-style `scatter` primitives, forcing a custom kernel or falling back to `MicroRetriever-v0`.
*   **Numerical Parity:** PyTorch CPU and NNTrainer CPU may diverge on FP32 accumulation or AdamW epsilon, causing the qualification gate to fail natively.
*   **Thermal Crash:** The OS may kill the foreground service instantly at `THERMAL_STATUS_CRITICAL` before a checkpoint can sync to disk.

## 17. Decisions Requiring User Approval
1.  **Approval of the 8-Phase Checklist** and strict Stop/Go gates.
2.  **Approval to freeze `MiniULTRA-SKG-v0`** as the leading candidate, but ONLY if it beats simple graph heuristics on the Python reference before porting to NDK.

## 18. Recommended Next Action
**Proceed to Phase 0 and Phase 1.** We must finalize the Kotlin Domain Contracts (`SpecialistEngine`) and immediately shift to the Ubuntu Termux environment to build the `MiniULTRA-SKG-v0` Python reference trainer to establish our loss and qualification baseline.

## 19. First-Model Decision Record
| Candidate | Utility | Dependency Risk | Native Port Risk | Verdict |
| :--- | :--- | :--- | :--- | :--- |
| **Non-Neural Graph Baseline** | Fast, high precision | Low | Zero | Must implement first as the hurdle rate. |
| **MiniULTRA-SKG-v0** | High (Proposes missing architecture edges) | Medium (Needs graph sampling) | High (Requires scatter/gather operators) | **Leading Candidate** (If beats baseline). |
| **MicroRetriever-v0** | High (Semantic semantic search) | Low | Low (Dense matrices only) | **Fallback Candidate** (If MiniULTRA operators fail in NDK). |
| **SpecialistKG 1.36M** | Proven | Zero | Medium | **Phase 5 Target** (Transformer Parity benchmark). |

## 20. Provenance Contract (`trained_from_scratch`)
The `SpecialistPackDescriptor` will mathematically enforce that a model is `trained_from_scratch`. 
*   No external weights may be imported.
*   No teacher logits/labels from `ultra_50g` may be used.
*   The `initialization_hash` must represent random seed generation.
*   All synthetic labels (SDG) must be explicitly flagged in the `source_ledger` as non-observed.
