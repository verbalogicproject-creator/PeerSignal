# Antigravity Prompt: Final Comprehensive Alignment for PeerSignal's Dual-Engine Edge Specialist Factory

You are Antigravity working inside the PeerSignal Android repository. Your immediate task is **architecture-plan alignment only**. Do not implement the plan, add dependencies, modify application source, download SDKs, build native libraries, or change CI during this task. First inspect the cited evidence, reconcile the existing plans with the actual repository and device state, and produce a decision-complete, evidence-labelled plan for user review.

## 1. Primary objective

Align PeerSignal around a realistic **Edge Specialist Factory** that can use two execution environments without pretending they have identical capabilities:

1. **Companion Python Engine** — PRoot-Distro Ubuntu running NumPy, SciPy, PyTorch CPU training, KG/data tooling, evaluation, and optional llama.cpp/OpenCL inference or synthetic-data generation.
2. **Native APK Factory Engine** — Kotlin orchestration plus Android NDK/JNI, initially using a proven native training framework such as NNTrainer, with Android lifecycle/thermal controls and separate CPU/GPU/NPU inference targets.

The aligned architecture must preserve the useful dual-engine idea while correcting unsupported claims in the current draft. The goal is not “put an LLM in an APK.” The goal is to manufacture, qualify, package, route, and safely run narrow, reproducible specialist models. The first product gate is not UI completeness or a mock training animation: **the app must initiate, survive, and verify from-scratch training of one objectively useful model**. Use a deliberately reduced ULTRA-inspired KG link predictor, `MiniULTRA-SKG-v0`, as the leading first-model candidate; retain the already-proven approximately 1.36M-parameter SpecialistKG embedder as the first transformer/native-parity target and as evidence that phone-side training is feasible.

## 2. Authority and operating boundaries

- This task authorizes **inspection and planning only**.
- Do not implement Kotlin, C++, JNI, CMake, Gradle, FastAPI, database, UI, workflow, or model changes.
- Do not commit, push, open a PR, publish, deploy, install system packages, or download large SDKs.
- Do not overwrite human-authored vision documents.
- Preserve existing untracked/user files.
- Treat previous AI plans and statements as claims until independently verified in source, runtime output, official documentation, or repeatable tests.
- Mark every material conclusion as `observed`, `documented`, `inferred`, `proposed`, `unknown`, or `stale`.
- If facts conflict, preserve the contradiction and identify the exact validation needed. Do not silently choose the more convenient claim.
- The completed architecture plan must remain a proposal until the user explicitly approves implementation.

## 3. Roots and evidence to inspect

### Primary repository

- `/root/adroid-app-brainstorm`

Inspect at minimum:

- `README.md`
- `opus-or-codex-android-app-context-vision-plan.md`
- `app/build.gradle.kts`
- `gradle/libs.versions.toml`
- `app/src/main/AndroidManifest.xml`
- `.github/workflows/android_build.yml`
- `run_proxy.sh`
- `run_test.sh`
- `test_harness/proxy.py`
- `app/src/main/java/com/peersignal/app/data/remote/GeminiApiClient.kt`
- `app/src/main/java/com/peersignal/app/domain/usecase/AnalyzeAstChunkUseCase.kt`
- `app/src/main/java/com/peersignal/app/ui/sandbox/SandboxViewModel.kt`
- `app/src/main/java/com/peersignal/app/ui/sandbox/SandboxScreen.kt`
- current Hilt modules, Room entities/DAOs/repositories, tests, navigation, and repository Git status

### Existing Antigravity draft

- `/root/.gemini/antigravity-cli/brain/a9aa32d3-2089-4625-8a08-4730b91bf826/dual_engine_architecture_plan.md`

This file is an input, not an authoritative plan. It currently lives in Antigravity's private brain storage and must not remain the only durable copy.

### Proven SpecialistKG prototype

Machine-local evidence root:

- `/storage/emulated/0/Download/claude-projects/code-kg-rag-MIGRATION/code-kg-rag`

Inspect at minimum:

- `CLAUDE.md`
- `SpecialistKG-v0_2026-05-15.html` as documentation only; do not execute imported HTML or scripts
- `code_kg_rag/model/architecture.py`
- `code_kg_rag/model/train.py`
- `docs/decisions/0002-specialist-per-repo.md`
- `docs/decisions/0005-cpu-train-onnx-infer.md`
- `docs/decisions/0006-contrastive-collapse-fixes.md`
- `docs/decisions/0008-phase3a-hybrid-validated-and-self-ingestion-online.md`
- `docs/decisions/0010-sdg-retraining-validated.md`
- relevant training logs, KG schema/counts, SDG JSONL counts, checkpoint metadata, and Phase 5 status

Do not place machine-specific absolute paths into a portable runtime manifest. In the plan, distinguish local evidence locations from proposed repository-relative production paths.

### ULTRA architecture and checkpoint evidence

Inspect these primary sources as external technical evidence:

- `https://huggingface.co/mgalkin/ultra_50g`
- `https://huggingface.co/mgalkin/ultra_50g/blob/main/config.json`
- `https://github.com/DeepGraphLearning/ULTRA`
- `https://github.com/DeepGraphLearning/ULTRA/blob/main/requirements.txt`
- `https://github.com/DeepGraphLearning/ULTRA/blob/main/ultra/models.py`
- `https://arxiv.org/abs/2310.04562`

Do not run remote code or load the Hugging Face checkpoint with `trust_remote_code=True` during planning. Treat the paper, official repository, model card, configuration, and weights as separate artifacts with separate proof limits. The user explicitly requires from-scratch training; the pretrained weights are not an initialization source.

## 4. Ground truths already observed; revalidate before relying on them

### 4.1 Current PeerSignal repository

- `observed`: The repository is currently at initial commit `17e4f48` on `main`, with an untracked `suggestions/` directory at the time of the latest inspection.
- `observed`: The current FastAPI endpoint is a mock. `test_harness/proxy.py` returns a fixed success payload and one fabricated graph node. It does not invoke kg-factory, SpecialistKG, NumPy, SciPy, PyTorch, or llama.cpp.
- `observed`: `GeminiApiClient` is misnamed; it posts to `http://localhost:8000/api/v1/sandbox/analyze` and returns raw response text. It does not call Gemini.
- `observed`: `AnalyzeAstChunkUseCase` directly imports a data-layer client, so the current source does not fully obey the documented UI -> Domain -> Data dependency boundary.
- `observed`: `run_proxy.sh` creates/activates `venv`, installs only FastAPI/Uvicorn/Pydantic, and starts the mock proxy.
- `observed`: The existing `venv` cannot import NumPy, SciPy, or PyTorch even though Ubuntu's global Python can.
- `documented but not fully proven`: README/vision claims that the MVP, proxy bridge, tests, and CI are structurally complete. Verify build/test evidence before carrying this claim forward.
- `observed`: Existing JVM test coverage verifies only that a running proxy response contains `success`; it does not validate training, graph construction, checkpointing, model export, JNI, native loading, or hardware acceleration.

### 4.2 PRoot Ubuntu scientific/ML environment on this device

The following was directly executed successfully in PRoot Ubuntu; independently revalidate with read-only smoke tests:

- Python `3.14.4`
- NumPy `2.4.6`
- SciPy `1.16.3`
- PyTorch `2.12.0+cu130`
- NumPy dense matrix multiplication succeeded.
- SciPy CSR sparse matrix multiplication succeeded.
- PyTorch forward pass, scalar loss, backward pass, and gradient computation succeeded.
- NumPy reports AArch64 OpenBLAS with NEON, NEON FP16, ASIMDHP, ASIMDDP, and ASIMDFHM support.
- PyTorch reports 8 intra-op threads, 8 inter-op threads, OpenMP, and oneDNN/MKLDNN availability.
- `torch.cuda.is_available()` is false. The `+cu130` label does not give a Qualcomm Android phone CUDA capability.
- Upstream PyTorch in this environment does not automatically use Adreno OpenCL, Vulkan, or Hexagon. Treat it as a CPU reference/training engine unless a separately proven backend exists.
- PRoot intercepts syscalls through `ptrace`; this can materially affect filesystem-heavy work but does not mean every dense math kernel is emulated.

Plan an ML-specific Python environment without duplicating huge wheels unnecessarily. Evaluate a dedicated venv created with `--system-site-packages`, version locking, an import/ABI smoke test, and a reproducible environment receipt. Do not implement or recreate that environment during this planning task.

### 4.3 SpecialistKG prototype

- `observed`: `CodeKGEmbedder` is a real two-layer transformer encoder with approximately 1,362,432 parameters, `d_model=256`, four attention heads, FFN size 512, GELU, pre-layer normalization, masked mean pooling, a 256-dimensional projection, and L2-normalized embeddings.
- `observed`: The actual tokenizer vocabulary was approximately 947 tokens despite a larger configured ceiling.
- `observed`: The training pipeline combined 1,314 structural KG edges with bidirectional augmentation and 450 SDG pairs with reverse augmentation/weighting, producing approximately 3,528 training pairs.
- `observed`: The checkpoint was approximately 5.4 MB.
- `observed`: The SDG-augmented run reached epoch 24, with loss and positive/negative separation improving materially and in-batch accuracy reaching approximately 0.37.
- `observed`: The run ended after overheating while CPU training continued with a Phi-4-mini 3.8B llama-server resident on the Adreno GPU. This is a stage-isolation failure, not a clean measure of the phone's training limit.
- `observed`: Graph/hybrid retrieval passed a small four-query validation, but the planned Phase 5 real-query benchmark was not completed.
- `inferred`: The prototype proves that small specialist training is feasible on the device. It does not yet prove production reliability, superiority over a generic model, or generative specialist quality.
- `proposed`: This exact embedder remains the first **transformer/native-parity** target—not a new 5M-20M decoder. It need not be the first factory model if the smaller graph specialist passes its predeclared utility gate first.

### 4.4 ULTRA and the from-scratch graph-specialist connection

- `documented`: ULTRA is a knowledge-graph link-prediction model. Given `(head, relation, ?)`, it ranks graph nodes as candidate tails. It is not a text embedder and cannot replace semantic query-to-code retrieval.
- `documented`: `ultra_50g` is approximately 169K FP32 parameters. Its published configuration contains a six-layer, 64-dimensional relation GNN and a six-layer, 64-dimensional entity GNN using DistMult messages, sum aggregation, shortcuts, and layer normalization.
- `documented`: The published `ultra_50g` weights were pretrained on 50 KGs for one million steps. Small parameter count therefore does not imply cheap reproduction of the pretrained checkpoint.
- `documented`: ULTRA obtains relative relation representations rather than maintaining a downstream-specific entity/relation embedding table. This is the key architectural connection to a reusable specialist factory.
- `documented`: The reference implementation targets PyTorch 2.1/PyG 2.4 and depends on `torch-scatter`, `torch-sparse`, `torch-geometric`, `ninja`, `easydict`, and YAML. It also JIT-compiles a custom `rspmm` kernel for efficient relational message passing.
- `observed`: The current PRoot Python environment does not have `torch_geometric`, `torch_scatter`, `torch_sparse`, `ninja`, or `easydict`. Do not assume the upstream ULTRA repository runs on this phone without a dependency/build spike.
- `proposed`: Borrow the **task formulation and architecture principles**, not the pretrained weights. Create a randomly initialized, reduced `MiniULTRA-SKG-v0` trained only on governed local graph snapshots.
- `proposed`: Keep `ultra_50g` optional and quarantined as an external zero-shot comparison after the from-scratch gate is frozen. It must never supply initialization weights, synthetic labels, hidden teacher scores, or promotion criteria.

### 4.5 Android/Qualcomm hardware boundary

- `observed`: Device is Nubia NX779J, Snapdragon 8 Gen 3 / SM8650, Adreno 750, Android 15/API 35, arm64-v8a, approximately 16 GB RAM.
- `observed`: CPU feature flags include FP16, dot product, I8MM, and BF16-related capabilities.
- `observed`: `/vendor/etc/public.libraries.txt` explicitly publishes `libOpenCL.so`. On this firmware, a modern APK can request it with `<uses-native-library android:name="libOpenCL.so" android:required="false" />` and probe it dynamically.
- `observed`: Vendor OpenCL reports Adreno 750, FP16, BF16-related operations, integer dot products, shared virtual memory, unified memory, and Qualcomm extensions. These advertised capabilities still require kernel-level benchmarks.
- `observed`: Android system Vulkan loader and Qualcomm `vulkan.adreno.so` exist. The Termux `vulkaninfo` probe used Mesa Turnip, so it does not measure the native APK/vendor-driver path.
- `documented`: Vulkan is an official Android NDK API. OpenCL is vendor-specific and must remain optional.
- `documented`: NNAPI was designed for inference and is deprecated in Android 15. Do not base a new architecture on NNAPI.
- `documented`: QNN/LiteRT/ExecuTorch Qualcomm delegates are primarily model deployment/inference paths. Do not claim Hexagon supports general-purpose training without direct official proof.
- `proposed`: Use CPU or GPU for training; qualify, quantize, and compile models separately for CPU, Vulkan/OpenCL GPU, or QNN/Hexagon inference.
- `proposed`: WebGL/WebView is not a primary compute route. Native Vulkan is the portable GPU route; published vendor OpenCL may be an SM8650 optimization plugin.

## 5. Required corrections to the existing dual-engine draft

The aligned plan must explicitly correct these issues:

1. **Do not call the Termux proxy “working flawlessly” for ML training.** The current endpoint is a mock. Separate proven Python runtime capability from unimplemented proxy capability.
2. **Do not claim that Termux currently trains heavy 100M+ models in PyTorch.** The proven specialist training was approximately 1.36M parameters on CPU. Phi-4-mini 3.8B was llama.cpp/OpenCL inference used for SDG generation.
3. **Do not imply that APK packaging itself accelerates arithmetic.** Gains must come from native/fused kernels, supported vendor drivers, reduced Python/PRoot overhead where relevant, memory control, thermal scheduling, and deployable runtimes.
4. **Do not describe Hexagon/QNN as a training backend.** Treat it as an inference/compiled-runtime target unless official evidence demonstrates otherwise.
5. **Do not use Hilt as a mutable runtime switch.** Hilt bindings are resolved when components are created. Inject both engines or a multibound registry/factory, and place runtime selection in an explicit selector/router whose state is persisted and observable.
6. **Do not use `data.native` as a package.** `native` is a language keyword and a poor interop/package choice. Prefer a neutral path such as `data.engine.nativeimpl`.
7. **Do not pass `List<Int>` token-by-token across JNI.** Avoid boxing and excessive copies. Prefer `IntArray`, direct `ByteBuffer`, file descriptors, shared/memory-mapped tensor shards, or opaque native dataset handles.
8. **Do not define the domain contract around one epoch.** The boundary must represent durable jobs, capabilities, progress/events, cancellation, pause/resume, checkpoint recovery, qualification, artifact export, and failure receipts.
9. **Do not claim a JNI stub proves NNTrainer integration.** A constant `0.45f` validates only Java/Kotlin -> native symbol loading and calling. Actual NNTrainer proof requires headers, library/source integration, ABI/STL linkage, an executed model/training operation, and a result derived from native computation.
10. **Do not estimate “two extra CI minutes” without measurement.** NDK/NNTrainer build cost, cache behavior, ABI matrix, and binary size are unknown until a spike produces evidence.
11. **Do not assume an APK can bootstrap or depend on Termux.** Treat the Python engine as an optional companion/developer mode requiring separate installation, explicit start, health checks, authentication, and restart/recovery behavior.
12. **Do not use localhost without a security contract.** Any app may attempt connections. Plan per-session capability tokens, protocol version negotiation, loopback-only binding, request limits, and explicit user-visible pairing. Never send secrets in logs.
13. **Do not run training on `Dispatchers.Default` without resource isolation.** Plan a dedicated orchestration dispatcher/native worker pool, a foreground service for user-visible long jobs, WorkManager only where appropriate, and cooperative cancellation.
14. **Do not conflate training and serving.** Native factory architecture must contain distinct dataset, trainer, qualifier, packager, and runtime responsibilities.
15. **Do not make generated models factual authorities.** KG/source evidence remains canonical; learned specialists rank, route, classify, or generate bounded proposals and must abstain outside qualification boundaries.
16. **Do not conflate ULTRA-inspired from-scratch training with reproducing `ultra_50g`.** The published checkpoint is small in parameters but represents one million pretraining steps across 50 graphs. Use its task/architecture as research grounding; keep its weights and outputs outside the from-scratch lineage.
17. **Do not make the native port block proof of model value.** First establish a useful, repeatable model and valid evaluation in the working PyTorch reference environment; then port only an architecture whose operators and backward pass are supported. The installed app's companion-engine MVP and its later standalone-native milestone must have distinct receipts.

## 6. Target conceptual architecture to refine

Use this as a starting hypothesis, not an instruction to force unsupported components:

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

CompanionPythonEngine (optional Termux/PRoot sidecar)
├── authenticated/versioned loopback protocol
├── deterministic dataset compiler
├── NumPy/SciPy KG and evaluation tooling
├── PyTorch CPU reference trainer
├── optional llama.cpp/OpenCL SDG generator
├── checkpoint/resume
└── SpecialistPack exporter

NativeFactoryEngine (standalone APK path)
├── Kotlin lifecycle and foreground-job orchestration
├── NDK/JNI bridge with coarse-grained buffers/handles
├── native CPU reference backend
├── NNTrainer feasibility backend
├── optional Vulkan training backend if proven
├── optional vendor OpenCL backend if proven
├── qualification and calibration
└── inference adapters
    ├── CPU portable fallback
    ├── Vulkan GPU
    └── QNN/Hexagon where supported
```

The “dual engine” refers to companion-versus-native execution environments. Within the native engine, training, qualification, packaging, and inference must remain separate stages.

## 7. Domain contracts the plan must specify

Do not finalize exact Kotlin syntax prematurely, but plan contracts equivalent to:

```kotlin
interface SpecialistEngine {
    val engineId: EngineId
    suspend fun probe(): EngineCapabilities
    suspend fun prepare(request: PreparationRequest): PreparedDataset
    fun train(request: TrainingRequest): Flow<TrainingEvent>
    suspend fun pause(runId: RunId): PauseReceipt
    fun resume(runId: RunId): Flow<TrainingEvent>
    suspend fun cancel(runId: RunId): CancellationReceipt
    suspend fun qualify(runId: RunId, suite: QualificationSuite): QualificationReport
    suspend fun export(runId: RunId): SpecialistPackDescriptor
}
```

The plan must define, at a minimum:

- `EngineCapabilities`: available/healthy, engine version, protocol version, CPU/GPU/NPU availability, supported model templates/operators/dtypes, maximum tested memory, training/inference support, and reasoned fallback.
- `TrainingRequest`: immutable dataset ID/hash, template ID/version, seed, optimizer/configuration, budget, thermal/power policy, checkpoint cadence, validation split, and privacy classification.
- `TrainingEvent`: queued, preparing, running, checkpointed, cooling, paused, resumed, qualifying, completed, failed, cancelled; include monotonic progress and structured error codes.
- `SpecialistPackDescriptor`: content hash, manifest version, tokenizer if applicable, weights, architecture/template, ontology/task contract, source ledger and graph-snapshot hashes, split/negative-sampling policy, initialization provenance, external-teacher usage declaration, training receipt, evaluation report, calibration/abstention policy, supported runtimes, compatibility, signature, and rollback lineage.
- Engine selection policy: explicit user preference plus capability/health checks and deterministic fallback; no invisible backend swap during an active run.
- State ownership: Room should hold run metadata, selected engine, progress snapshots, receipts, and pack registry metadata; large datasets/weights belong in app-private files with stable IDs and hashes rather than Room blobs.

## 8. Companion protocol requirements

Plan a versioned job protocol rather than extending the single raw-string analysis endpoint. At minimum consider:

- `GET /v1/capabilities`
- `GET /v1/health`
- `POST /v1/datasets/prepare`
- `POST /v1/runs`
- `GET /v1/runs/{runId}`
- progress streaming via SSE or another deliberately chosen mechanism
- `POST /v1/runs/{runId}/pause`
- `POST /v1/runs/{runId}/resume`
- `POST /v1/runs/{runId}/cancel`
- `POST /v1/runs/{runId}/qualify`
- `GET /v1/packs/{packId}` or an explicit content-transfer alternative

Specify idempotency, protocol/version mismatch behavior, reconnect/replay semantics, path and payload validation, maximum request sizes, error schema, capability token rotation, and how the app behaves when Termux is absent or killed.

The existing `/api/v1/sandbox/analyze` endpoint may remain temporarily as a compatibility/debug route, but must not be presented as the specialist-factory protocol.

## 9. Specialist model and data strategy

Plan a template registry with conservative initial scope and two complementary model families:

1. **`MiniULTRA-SKG-v0` — first factory/MVP gate.** A from-scratch structural KG link predictor inspired by ULTRA's relation-conditioning and entity-independent transfer principles, but intentionally implemented as a small auditable model using ordinary PyTorch operations. Proposed starting envelope: a two-layer relation GNN and two- or three-layer entity/path GNN, 32-dimensional hidden state, approximately 20K-80K trainable parameters. These dimensions and parameter counts are hypotheses to confirm, not requirements to fake.
2. **`MicroRetriever-v0` — semantic companion.** A from-scratch query/code dual encoder with a code-aware 1K-4K vocabulary, 96-dimensional embeddings, masked mean pooling, small projection MLP, L2 normalization, and approximately 120K-400K parameters. It maps natural-language/code queries to known KG nodes; it does not predict missing structure.
3. **Exact SpecialistKG encoder parity template.** The existing approximately 1.36M-parameter transformer is the first proven reference and first transformer/native-parity target after the MVP gate.
4. Router/classifier templates below approximately 1M parameters.
5. Reranker/embedding variants in the approximately 1M-10M range only after the first two specialists establish value.
6. A 5M-20M narrow decoder only as a later experiment with explicit feasibility and quality gates.

The roles must remain distinct:

```text
user text/code query
        |
        v
MicroRetriever-v0 ----> observed KG seed nodes
                                |
                                v
MiniULTRA-SKG-v0 -----> ranked candidate tails/relations
                                |
                                v
KG validator + bounded traversal + provenance
                                |
                                v
observed evidence, predicted proposal, or abstention
```

`MiniULTRA-SKG-v0` has immediate utility even without language generation: missing-edge proposals, dependency or likely-call suggestions, `implements_concept`/type-link completion, structural anomaly triage, and learned candidate ordering for bounded graph expansion. Every predicted edge must be labelled `predicted`, carry model/graph snapshot hashes and a score, and remain separate from canonical observed edges until source verification or explicit human acceptance.

For the first dataset, compile deterministic train/validation/test snapshots from the existing code KG's 555 nodes and 1,314 typed directed edges. Begin only with relations that have enough independent examples to split meaningfully—such as `accepts_type`, `implements_concept`, `contains`, `returns_type`, `imports`, and `calls` after verifying counts and semantics. Exclude or report `not_observed` for extremely sparse relations such as the currently observed three `inherits_from` edges. Split original canonical edges before reverse-edge augmentation. Avoid source/target or near-duplicate leakage according to the declared evaluation population.

The initial implementation should not vendor or port the complete PyG/ULTRA stack merely to reproduce its shape. First write a small reference implementation with standard PyTorch tensor operations and an explicit correctness oracle. Record the computational cost of edge materialization/scatter. Only investigate a custom sparse/scatter kernel, PyG build, or native port after profiling shows it is necessary. Dynamic relational message passing is likely harder to port to NNTrainer/QNN than the dense retriever; treat native operator coverage as an open decision, not an assumed phase outcome.

Do not label broad 100M+ from-scratch training as an MVP target. Reliability must come from a narrow contract, deterministic data, held-out evaluation, calibration, abstention, provenance, and KG-grounded evidence—not parameter count.

Correct existing data portability/privacy issues in the plan:

- use stable source-relative IDs rather than private absolute source paths;
- create immutable dataset snapshots and source-ledger hashes;
- define deletion/redaction and rebuild semantics;
- keep synthetic examples distinguishable from observed source facts;
- record generator model/prompt/configuration without treating generated labels as truth;
- split training/validation/test deterministically before augmentation where appropriate;
- prevent exact reverse pairs or near-duplicates from leaking across held-out boundaries;
- generate filtered negatives that exclude all known positive triples in the frozen graph snapshot;
- preserve separate edge-restoration, held-out-node, and semantic-query populations rather than reporting one blended score;
- store training-run records and receipts—the existing SpecialistKG `training_runs` table was present but empty during inspection.

## 10. Reliability and qualification gates

The plan must reject “loss went down” as sufficient proof. Define qualification around user-facing contracts:

- exact-seed loss/gradient parity during porting;
- retrieval Recall@k and MRR on held-out queries;
- comparison with BM25, graph-only, and existing hybrid baselines;
- citation/evidence correctness where applicable;
- calibration and explicit abstention performance;
- contradiction and unsupported-query behavior;
- model collapse/embedding diversity checks;
- checkpoint/resume determinism within defined tolerance;
- repeat-run variance;
- deletion/drift/rebuild behavior;
- latency, peak RSS, APK/pack size, energy, and thermal headroom;
- backend result tolerance across Python CPU, native CPU, GPU, and quantized inference variants.

For `MiniULTRA-SKG-v0`, freeze the dataset snapshot, split policy, baselines, metrics, thresholds, tie-breaking, and sample-sufficiency rule **after baseline measurement but before neural training**. At minimum:

- deterministic edge-hiding/restoration evaluation with filtered MRR and filtered Hits@1/3/10;
- per-relation results plus macro aggregation so frequent relations cannot hide failure on smaller qualified relations;
- three fixed seeds with mean, spread, failures, and wall-clock/thermal receipts;
- baselines on the same snapshot: random, relation-frequency, degree/popularity, a reasonable graph heuristic such as common-neighbor/path score where semantically valid, and a small from-scratch DistMult model;
- proposed initial promotion gate, adjustable only before training after baseline evidence: at least `+0.05` absolute macro MRR and `+0.10` absolute macro Hits@10 over the strongest eligible simple baseline, with no required relation below its declared floor;
- checkpoint/save/reload must reproduce rankings within a predeclared tolerance;
- the Android workflow must train the model, recover from a checkpoint, render one correctly restored held-out edge with its observed supporting paths, and clearly distinguish that restoration test from an unverified real-world prediction;
- if the graph is too small for statistically meaningful thresholds, return `not_observed` or `insufficient_sample`; do not lower the gate after seeing neural results.

For `MicroRetriever-v0`, use an independently authored human query suite and a completely held-out-node population in addition to paraphrases of known nodes. A proposed starting gate is Recall@5 at least 0.70 and MRR at least 0.45 on the frozen human suite, plus at least `+0.10` absolute Recall@5 contribution over the strongest no-model retrieval baseline in the declared hybrid ablation. Freeze or revise these numbers only before neural training, based on measured baseline difficulty and sample sufficiency.

The existing SpecialistKG SDG tests overlap training targets and are weak evidence for generalization. Preserve their regression value, but do not reuse them as the sole promotion set for any new model.

Keep claims bounded. The existing approximately 37% in-batch accuracy and four example queries do not prove production reliability or superiority over generic models.

## 11. Thermal, lifecycle, and resource plan

The aligned plan must include:

- mandatory stage isolation: unload/terminate SDG-generation LLM resources before sustained training;
- device capability and backend probe at run creation;
- foreground service with persistent user-visible notification for active training;
- explicit pause/cancel controls;
- checkpoint-before-cooldown behavior;
- Android thermal-status/headroom monitoring and threshold policy;
- charging, battery, screen/interaction, and user opt-in constraints;
- bounded thread counts and CPU affinity/performance-hint experiments only after measurement;
- memory budgets and low-memory recovery;
- app/process death recovery from durable run state;
- WorkManager used for durable orchestration where suitable, not as an assumption of unlimited background execution;
- benchmark mode separated from normal sustained mode;
- no simultaneous heavy GPU inference and full CPU training unless a later experiment proves it thermally sustainable.

## 12. Native integration decisions the plan must resolve

Before choosing NNTrainer as committed architecture, use current primary/official sources and repository code to validate:

- active Android/NDK support and license;
- supported arm64 ABI and minimum API;
- how NNTrainer is built and linked into an APK;
- current transformer layers/operators required by SpecialistKG;
- masked multi-head attention, pre-layer normalization, GELU, embedding, mean pooling, L2 normalization, AdamW, gradient clipping, and checkpoint support;
- whether the exact weighted InfoNCE objective can be expressed or requires a custom layer/training loop;
- which operators support backward propagation on CPU and OpenCL;
- whether advertised OpenCL/NPU support applies to training, inference, or both;
- mixed precision behavior and need for FP32 optimizer master weights;
- export/interchange formats and numerical fidelity;
- binary size, transitive dependencies, STL choice, exceptions/RTTI policy, and security/update implications.

If NNTrainer cannot reproduce the exact model/loss or does not accelerate its backward pass, keep it as a rejected or partial candidate and compare MNN/custom C++/Vulkan or a CPU-native reference. Do not force the architecture to fit a named framework.

The JNI design must include:

- lazy, failure-safe library loading and surfaced capability errors;
- stable native handle ownership and lifecycle;
- zero/low-copy data boundaries;
- explicit cancellation and callback/event threading;
- exception/error translation;
- native memory accounting;
- ABI compatibility and version negotiation;
- deterministic teardown and resource release;
- tests for invalid handles, malformed datasets, cancellation, process recreation, and native crashes where feasible.

## 13. Inference architecture

Plan inference as a separate adapter layer. The first portable fallback should be CPU. Vulkan and QNN are optimizations gated by operator coverage and device tests.

- Do not use deprecated NNAPI as the new foundation.
- QNN/Hexagon models may need device/backend-specific compilation and quantization.
- Unsupported operators must have an explicit fallback or make a pack incompatible; never silently change semantics.
- Keep per-backend artifacts under one logical SpecialistPack lineage with clear hashes and qualification reports.
- Quantized inference must be re-qualified against the reference checkpoint.
- A model result should return confidence/abstention and evidence references where its task requires grounding.

## 14. CI and device verification

Separate host compilation proof from on-device hardware proof.

### Host CI can prove

- Gradle/Kotlin compilation;
- CMake/NDK arm64 compilation and linkage;
- unit tests and selected host/native tests;
- deterministic manifest/schema validation;
- JNI symbol/package consistency;
- artifact hashes and binary-size budgets.

### Host CI cannot prove alone

- Adreno vendor OpenCL accessibility on the Nubia firmware;
- Android system Vulkan versus Termux Turnip performance;
- QNN/Hexagon execution;
- sustained thermal behavior;
- power consumption;
- process-death/recovery behavior on the physical phone.

Plan a separate physical-device evidence harness that records device/OS/driver/runtime versions, backend actually selected, step latency, memory, thermal state/headroom, energy proxy, checkpoint recovery, and result hashes. Never accept a nominal “GPU” setting without evidence that the GPU backend executed.

## 15. Required phased plan and stop/go gates

Produce a sequenced implementation plan with explicit file ownership and validation for each phase. At minimum:

### Phase 0 — Baseline and contract freeze

- Verify current repository build/test status without changing architecture.
- Replace documentation claims with evidence boundaries in the new plan.
- Define versioned domain contracts, run state machine, SpecialistPack manifest, model/data-card fields, and evaluation suite.
- Freeze the meaning of `trained_from_scratch`: random initialization, no imported model parameters, no teacher logits/labels from `ultra_50g`, and no checkpoint-derived preprocessing fitted on evaluation data.
- Gate: user approves contracts, `MiniULTRA-SKG-v0` as the leading first-model candidate, and the MVP scope.

### Phase 1 — First-model dataset, baselines, and reference trainer

- Design reproducible ML environment using working Ubuntu NumPy/SciPy/PyTorch.
- Compile a canonical KG snapshot and leakage-resistant edge-restoration splits from the existing code KG.
- Implement the simple graph baselines and a tiny DistMult baseline before training the proposed neural candidate.
- Build the ordinary-PyTorch `MiniULTRA-SKG-v0` reference from random initialization; do not install or execute upstream remote model code as a shortcut.
- Measure quality, repeatability, wall time, peak memory, and thermal behavior; perform an explicit model-versus-graph-size/sample-sufficiency review.
- Gate: the from-scratch model beats the strongest eligible predeclared baseline by the frozen promotion margin across three seeds, survives save/reload, and emits a validated SpecialistPack—or the phase stops with a truthful failed/insufficient-sample receipt and a decision between `MicroRetriever-v0`, more governed data, or a revised non-neural feature.

### Phase 2 — Working-app MVP through the companion engine

- Replace mock-only behavior with the authenticated job/capability protocol while retaining a compatibility route if needed.
- Add the minimal Android workflow for dataset selection, start, progress/cooling, pause/resume, failure, qualification, and pack inspection.
- Integrate only the qualified Phase 1 trainer and immutable artifacts; do not copy private absolute paths into packs.
- Gate: the installed Android app launches a real deterministic training run from random initialization, resumes it after a deliberate interruption, receives the qualification report, and demonstrates the model's held-out edge-ranking utility. This is the MVP gate; a proxy-only curl demo or loss animation does not pass.

### Phase 3 — JNI/NDK boundary spike

- Add NDK/CMake and an arm64 JNI library with capability/version/self-test methods.
- This phase proves loading, calling, error handling, buffers, cancellation plumbing, and CI linkage—not NNTrainer performance.
- Gate: APK and CI build; physical device runs JNI self-test and reports a receipt.

### Phase 4 — Actual native training framework and operator spike

- Link/build the selected framework and execute a real minimal trainable network with real loss change.
- Measure native CPU behavior first.
- Probe the actual operations needed by both a dense `MicroRetriever-v0` and relational/scatter-based `MiniULTRA-SKG-v0`; explicitly record unsupported backward operators.
- Gate: no constant/mock result; real gradients/parameter update, checkpoint, resume, teardown, bounded binary-size evidence, and a supported/rejected verdict for each first-model architecture.

### Phase 5 — Standalone native specialist and transformer parity

- First port whichever qualified small specialist has supported native backward operators. Prefer `MiniULTRA-SKG-v0` if relational message passing is correct and maintainable; otherwise port the dense `MicroRetriever-v0` and keep MiniULTRA on the companion engine until a justified kernel path exists.
- Then port the approximately 1.36M SpecialistKG encoder as the first transformer-parity target and compare its exact loss/data semantics.
- Compare fixed-seed Python CPU and native CPU losses, gradients, rankings/embeddings, and held-out utility within specified tolerances.
- Gate: the APK trains at least one useful specialist without Termux, and parity, repeatability, checkpoint recovery, and the model-specific qualification thresholds pass. Record partial support honestly if only the companion engine can run MiniULTRA.

### Phase 6 — GPU training experiment

- Compare native CPU, Vulkan if supported for backward pass, and vendor OpenCL if supported for backward pass.
- Run identical seeds/data under thermal governance.
- Gate: GPU path must prove backend execution, correctness, and sustainable benefit; otherwise retain CPU training and document the rejected optimization.

### Phase 7 — Qualified inference targets

- Export/convert and qualify CPU, Vulkan, and QNN variants where supported.
- Gate: operator coverage, numerical/quality tolerance, backend receipt, and deterministic fallback pass.

### Phase 8 — Factory UX and multi-specialist routing

- Add device profile, dataset preparation, training progress/cooling/pause/resume, qualification report, pack registry, backend selection, rollback, and evidence-aware specialist routing.
- Combine the text retriever, structural link predictor, deterministic KG traversal, and abstention policy only after each component has an ablation result.
- Gate: end-to-end user workflow survives process death, exposes observed-versus-predicted provenance, and never presents an unqualified model or predicted edge as canonical fact.

Each phase must state exact proposed files/modules, dependencies, migrations, tests, device evidence, rollback path, and conditions that stop later work.

## 16. Product behavior and killer features to preserve

Align the architecture with these high-value product capabilities:

- import a repository/folder or bounded KG dataset and build a specialist entirely offline;
- device profiler recommends model template, batch size, backend, and thermal policy;
- deterministic overnight training with cooling, pause/resume, and checkpoint receipts;
- CPU versus Vulkan versus OpenCL hardware race using identical seed/data;
- qualification dashboard comparing learned models to BM25/graph/hybrid baselines;
- live KG repair lab: hide an observed edge, train from scratch, show whether `MiniULTRA-SKG-v0` restores it, then keep novel predictions in a review queue rather than silently modifying the KG;
- architecture cards that show random initialization, exact parameter count, dataset/graph hashes, excluded relations, baselines, seed variance, and which operations actually ran on CPU/GPU/NPU;
- signed/versioned SpecialistPacks with provenance, evaluation, compatibility, and rollback;
- specialist router/cascade that chooses narrow models and abstains safely;
- KG-RAG remains the factual/evidence layer while learned models improve routing, ranking, relation scoring, or bounded generation;
- optional Termux companion mode for rapid research without making Termux a production dependency;
- standalone native mode remains useful when Termux is absent.

Preserve PeerSignal's high-signal, dark-editorial product identity, but do not design polished UI before the execution and evidence contracts are approved.

## 17. Deliverables required from this planning task

Create a repository-owned plan, preferably:

- `docs/architecture/dual-engine-edge-specialist-factory.md`

If `docs/architecture/` does not exist, propose creating it in the plan output; do not implement other source changes. The new plan should include:

1. Executive verdict and bounded feasibility statement.
2. Evidence ledger with source anchors and freshness/proof limits.
3. Corrections to the prior Antigravity draft.
4. System/context diagram.
5. Ownership boundaries and dependency rules.
6. Domain contracts and run state machine.
7. Companion protocol and security contract.
8. Native/JNI/framework integration design.
9. SpecialistPack manifest/schema proposal.
10. Data lifecycle, provenance, privacy, and deletion policy.
11. Training, qualification, inference, and routing separation.
12. Thermal/lifecycle/resource policy.
13. Phased implementation checklist with stop/go gates.
14. Test/evaluation matrix and required physical-device receipts.
15. CI plan with host-versus-device proof boundaries.
16. Risk register, contradictions, unknowns, rejected alternatives, and rollback routes.
17. Explicit decisions requiring user approval.
18. Recommended next action after approval.
19. A dedicated first-model decision record comparing `MiniULTRA-SKG-v0`, `MicroRetriever-v0`, the existing SpecialistKG encoder, and a non-neural graph baseline on usefulness, dataset sufficiency, dependency risk, native operator risk, expected training cost, evaluation quality, and fallback route.
20. A `from_scratch` provenance contract that makes imported initialization, teacher labels/logits, frozen external embeddings, and checkpoint reuse machine-detectable rather than a narrative claim.

Also provide a concise chat summary to the user containing:

- what changed relative to the old draft;
- what is proven now;
- what remains unknown;
- the recommended MVP boundary;
- the exact plan path;
- confirmation that no implementation was performed.

## 18. Required decision defaults unless evidence overturns them

Use these as proposed defaults and flag them for user approval:

- Product term: **Edge Specialist Factory**, not a promise of general-purpose on-device LLM pretraining.
- First factory/MVP model candidate: from-scratch `MiniULTRA-SKG-v0`, subject to the frozen baseline and sample-sufficiency gate.
- First semantic companion: from-scratch `MicroRetriever-v0`; add only after the structural gate or use it as the fallback if the graph cannot support qualified link prediction.
- First transformer/native-parity model: existing approximately 1.36M SpecialistKG encoder.
- ULTRA relationship: architecture/task inspiration only. `ultra_50g` weights are optional external zero-shot comparison artifacts and are forbidden from the from-scratch training lineage.
- Companion engine: optional research/reference backend, not a required APK dependency.
- Native training fallback: CPU-first.
- GPU training: benchmark-gated experiment.
- Hexagon/QNN: inference only until official training support is proven.
- Model size MVP: approximately 0.1M-5M; approximately 5M-20M experimental; broad 100M+ from-scratch training out of MVP scope.
- OpenCL: optional capability-probed SM8650/vendor optimization.
- Vulkan: portable Android GPU target, but training support must be demonstrated separately from inference.
- Room: metadata/state/receipts, not large weight or tensor storage.
- Learned specialist: bounded recommender/ranker/router with abstention; KG/source evidence remains canonical.
- Prediction status: all learned missing-edge outputs remain non-canonical proposals until verified against a source or explicitly accepted with provenance.
- Implementation begins only after the user approves the aligned plan.

## 19. Quality bar

The final plan must be specific enough that an implementation agent could work phase by phase without rediscovering the architecture, but it must not pretend unresolved framework/operator/performance questions are settled.

Avoid:

- marketing language presented as proof;
- invented benchmarks or CI estimates;
- generic “use clean architecture” statements without dependencies and owners;
- enormous token arrays over JNI;
- mutable Hilt bindings;
- hidden fallback between engines;
- background execution assumptions;
- NPU-training claims;
- test plans that only assert a mock constant;
- declaring completion based only on compilation;
- treating training loss as end-user reliability;
- calling a random-initialized model “ULTRA” without the `Mini`/inspired distinction or implying reproduction of the pretrained foundation checkpoint;
- using `ultra_50g` weights, teacher scores, or remote-code execution inside the from-scratch path;
- allowing hidden positive triples into negative samples or splitting after reverse-edge augmentation;
- treating a restored deliberately hidden edge as proof that novel predictions are true;
- coupling portable core contracts to Qualcomm-only APIs.

Prefer:

- exact source anchors;
- explicit observed/documented/proposed labels;
- capability negotiation;
- deterministic artifacts and receipts;
- coarse-grained, versioned boundaries;
- independently testable phases;
- physical-device evidence for hardware claims;
- reversible implementation steps;
- clear user approval checkpoints.

Begin by inspecting the current repository and cited evidence. Then produce the aligned repository-owned architecture plan and stop for user review. Do not implement it in this task.
