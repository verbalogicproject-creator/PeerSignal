"
• Yes—the APK changes the engineering boundary
  materially, but probably not in the
  simplistic “APK unlocks the NPU for training”
  sense. It gives you a controlled NDK/Vulkan
  runtime, lifecycle, storage, thermal and
  security APIs, while Hexagon-class NPUs
  remain primarily inference targets. I’ll
  inspect your SpecialistKG prototype and data
  first, then map its actual model scale and
  training path against what Android, Vulkan,
  Qualcomm and current edge runtimes can really
  support. This remains read-only.

• Ran prototype=/storage/emulated/0/Download/
  │ claude-projects/code-kg-rag-MIGRATION/
  │ code-kg-rag; printf '%s\n' '### target';
  │ … +20 lines
  └ /storage/emulated/0/Download/
    claude-projects/code-kg-rag-MIGRATION/
    … +232 lines (ctrl + t to view transcript)
    claude-projects/code-kg-rag-MIGRATION/
    code-kg-rag/data

───────────────────────────────────────────────

• Your prototype is already the strongest
  argument that this is feasible at the right
  scale: it trained a 1.36M-parameter, repo-
  specific two-layer encoder from scratch on
  the phone, using 3,528 graph/SDG pairs, and
  produced a 5.4 MB checkpoint. The real
  opportunity is to turn that one-off PyTorch/
  Termux success into an APK-native factory
  with deterministic datasets, several tiny
  model templates, hardware-aware training,
  export, qualification, rollback, and
  specialist routing.

• Ran prototype=/storage/emulated/0/Download/
  │ claude-projects/code-kg-rag-MIGRATION/
  │ code-kg-rag; for f in CLAUDE.md docs/
  │ … +21 lines
  └ """
    Saturday Tier 2 item 6 — ONNX export +
    … +317 lines (ctrl + t to view transcript)
      pairs generated: 450
      errors:          0

• Ran prototype=/storage/emulated/0/Download/
  │ claude-projects/code-kg-rag-MIGRATION/
  │ code-kg-rag; printf '%s\n' '###
  │ … +28 lines
  └ Archive:  /storage/emulated/0/Download/
    claude-projects/code-kg-rag-MIGRATION/
    … +95 lines (ctrl + t to view transcript)
    v_temporal_nodes|view
    v_weighted_edges|view

• Ran db=/storage/emulated/0/Download/
  │ claude-projects/code-kg-rag-MIGRATION/
  │ code-kg-rag/data/typer.kg.db; sqlite3
  │ … +33 lines
  └      kind         n
    ---------------  ----
    … +120 lines (ctrl + t to view transcript)
        "you",
        "f",

• Ran awk 'match($0,/\(([0-9.]+)s\)/,a)
  │ {sum+=a[1]; n++} END{printf
  │ "run2_epochs=%d total_s=%.1f total_h=%.2f
  │ … +33 lines
  └ -rw-r--r--. 1 root root 59544 Jan  1
    2009 /vendor/lib64/libOpenCL.so
    … +27 lines (ctrl + t to view transcript)
    vulkan-utility-libraries/stable,now 1.4.359
    aarch64 [installed]

• Ran clinfo 2>&1 | sed -n '1,360p'; printf
  │ '%s\n' '### vulkan tools files'; dpkg -L
  │ vulkan-tools 2>/dev/null | rg '/bin/|
  │ … +8 lines
  └ Number of platforms
    1
    … +319 lines (ctrl + t to view transcript)
    thermal_zone12
    thermal_zone13

• Context compacted

───────────────────────────────────────────────

• I’ve now separated three questions that often
  get conflated: what Android officially
  exposes, what this Qualcomm firmware happens
  to expose, and which accelerators can
  actually backpropagate. I’m checking the
  current platform/runtime contracts before
  giving you a feasibility verdict and a
  concrete factory architecture.

• Searching the web

• Searched the web for
  site:developer.android.com/ndk/guides neural
  networks NNAPI deprecated Android 15 ...

• Searching the web

• Searched the web for site:ai.google.dev/edge/
  litert "on-device training" Android ...

• Searching the web

• Searched the web for site:ai.google.dev/edge/
  litert "training" signatures optimizer
  Android ...

• Searching the web

• Searched the web for site:mnn-
  docs.readthedocs.io training module Android
  OpenCL MNN train ...

• Searching the web

• Searched the web for site:github.com/
  nntrainer/nntrainer Android training OpenCL
  transformer example ...

• Searching the web

• Searched the web for site:docs.qualcomm.com
  QNN SDK on-device training Android Hexagon
  inference ...


› search the web for grounding data about
  termux/apk differences, are there
  optimizations? does apk make it more
  efficient and doable?


• That is exactly the comparison I’m grounding
  now. The early answer is nuanced: an APK does
  not inherently run native math faster than
  the same AArch64 code in Termux, but it
  removes several integration barriers and
  exposes supported Android lifecycle, thermal,
  Vulkan, AHardwareBuffer, and vendor-runtime
  paths. The decisive gains come from changing
  the runtime and kernels—not from the .apk
  container itself.

• Searching the web

• Searched the web for
  site:developer.android.com NDK app private
  storage mmap AHardwareBuffer Vulkan compute
  Android ...

• Searching the web

• Searched the web for site:github.com/termux/
  termux-app execution environment Android app
  process native binaries proot performance ...

• Ran rg -i 'opencl|qnn|hexagon|vulkan' /
  │ vendor/etc/public.libraries.txt /system/
  │ etc/public.libraries-*.txt 2>/dev/null ||
  │ … +4 lines
  └ ==========
    VULKANINFO
    … +65 lines (ctrl + t to view transcript)
        driverUUID         =
    6c6c766d-7069-7065-5555-494400000000

• Ran getprop | rg -i 'vulkan|gpu|egl|qnn|
  │ hexagon|opencl' | sed -n '1,160p'
  │ ls -l /vendor/lib64/libOpenCL.so /system/
  │ … +2 lines
  └ [debug.egl.hw]: [0]
    [graphics.gpu.profiler.support]: [true]
    … +11 lines (ctrl + t to view transcript)
    -rw-r--r--. 1 root root   59544 Jan  1
    2009 /vendor/lib64/libOpenCL.so

───────────────────────────────────────────────

• The device-specific check is unusually
  encouraging: this firmware explicitly
  publishes libOpenCL.so to third-party apps,
  and it has the official Android Vulkan loader
  plus Qualcomm’s Adreno driver. So, on this
  Nubia/SM8650, an APK can legitimately request
  vendor OpenCL and can use official Vulkan—not
  merely rely on Termux tricks. That is a real
  advantage, but it still needs an APK
  benchmark because your current Vulkan probe
  is using Mesa Turnip, not the vendor APK
  path.

• Searching the web

• Searched the web for site:github.com/
  nntrainer/nntrainer/tree/main nntrainer
  opencl layer training ...

• Searching the web

• Searched the web

• Searching the web

• Searched the web

• Explored
  └ List decisions
    List logs
    Search libOpenCL.so in public.libraries.txt
    Search overheat|3.8B|Adreno|OpenCL|epoch 24
    in
           0010-sdg-retraining-validated.md
    Search class CodeKGEmbedder|d_model|nhead|
           num_layers|projection in
           architecture.py

───────────────────────────────────────────────

• ## Verdict

  Yes—an installable APK makes the “edge micro-
  model factory” materially more practical.

  But the APK itself is not the speedup. Termux
  is already an Android app, and its native
  binaries execute directly on Android’s
  kernel. Ubuntu under PRoot adds overhead
  because PRoot intercepts every syscall, but
  dense matrix multiplication spends relatively
  little time making syscalls. Removing PRoot
  will mostly improve startup, data loading,
  tokenization, checkpointing, and process
  reliability—not magically accelerate every
  transformer operation. Termux documents
  native execution
  (https://github.com/termux/termux-packages/wiki/Termux-execution-environment);
  PRoot documents its ptrace overhead
  (https://github.com/termux/proot-distro#limitations).

  The major opportunity is replacing the
  Python/PyTorch/PRoot stack with an APK-native
  training engine that has fused ARM/GPU
  kernels and Android-aware thermal scheduling.

  ## What your prototype already proves

  This is not speculative greenfield work:

  - You trained a real 1,362,432-parameter,
    two-layer transformer encoder from scratch
    on the phone. Its architecture is visible
    in /storage/emulated/0/Download/claude-
    projects/code-kg-rag-MIGRATION/code-kg-rag/
    code_kg_rag/model/architecture.py:52.

  - It used 3,528 structural and synthetic
    graph pairs, produced a roughly 5.4 MB
    checkpoint, and reached epoch 24.

  - The four-hour runtime was not a clean
    hardware limit. The log says the CPU
    trained at full load while a Phi-4-mini
    3.8B server remained resident on the Adreno
    GPU, eventually causing thermal failure.
    See /storage/emulated/0/Download/claude-
    projects/code-kg-rag-MIGRATION/code-kg-rag/
    docs/decisions/0010-sdg-retraining-
    validated.md:44.

  - The trained model improved its training
    separation substantially, but the planned
    real-query Phase 5 benchmark was not
    completed. Therefore, the prototype proves
    training feasibility—not yet “reliable
    specialist superiority.”

  That last distinction matters: 37% in-batch
  training accuracy is not the same as reliable
  retrieval, routing, or generation.

  ## What an APK unlocks on this particular
  phone

  Your Nubia/SM8650 is unusually favorable.

   Capability
    CPU
   Termux/PRoot today
    PyTorch CPU through Ubuntu/PRoot
   Native APK opportunity
    NDK C++ with NEON, FP16, BF16 and I8MM-
    aware kernels
  ─────────────────────────────────────────────
   Capability
    GPU/OpenCL
   Termux/PRoot today
    Vendor OpenCL already reachable
   Native APK opportunity
    Firmware explicitly publishes libOpenCL.so;
    APK can request it with uses-native-library
  ─────────────────────────────────────────────
   Capability
    GPU/Vulkan
   Termux/PRoot today
    Current Termux probe uses Mesa Turnip
   Native APK opportunity
    APK can use Android’s system Vulkan loader
    and Qualcomm vulkan.adreno.so
  ─────────────────────────────────────────────
   Capability
    Hexagon NPU
   Termux/PRoot today
    Difficult/indirect
   Native APK opportunity
    QNN/LiteRT/ExecuTorch delegates for
    compiled inference
  ─────────────────────────────────────────────
   Capability
    Thermal control
   Termux/PRoot today
    Mostly indirect
   Native APK opportunity
    Thermal headroom, Performance Hint
    sessions, charging constraints and
    deliberate cooldowns
  ─────────────────────────────────────────────
   Capability
    Long jobs
   Termux/PRoot today
    Shell processes can be killed
   Native APK opportunity
    Foreground training service, resumable
    WorkManager orchestration and persistent
    notification
  ─────────────────────────────────────────────
   Capability
    Memory exchange
   Termux/PRoot today
    Ordinary buffers/files
   Native APK opportunity
    AHardwareBuffer, shared memory and memory-
    mapped weights/checkpoints
  ─────────────────────────────────────────────
   Capability
    Security
   Termux/PRoot today
    Termux-owned environment
   Native APK opportunity
    App-private storage, Keystore signing and
    verified specialist bundles

  The strongest device-specific fact is
  /vendor/etc/public.libraries.txt:6, which
  explicitly contains libOpenCL.so. Android
  normally hides non-NDK vendor libraries
  unless manufacturers publish them; apps
  targeting modern Android must request them
  explicitly. Android explains that contract
  here
  (https://developer.android.com/guide/topics/manifest/uses-native-library-element).

  Vulkan is the more portable official route:
  it is a public NDK API, while OpenCL
  availability remains device/vendor-specific.
  Android’s NDK exposes Vulkan directly
  (https://developer.android.com/ndk/guides/stable_apis).

  WebGL is not attractive here. It would add
  WebView/browser overhead and give you less
  control. Native Vulkan compute—or this
  device’s published OpenCL—is the serious
  path.

  ## What the APK does not unlock

  The Hexagon NPU should be considered an
  inference target, not your main training
  accelerator.

  NNAPI was an inference API, and it is
  deprecated as of Android 15. Google now
  recommends alternative runtimes such as
  LiteRT’s GPU and vendor NPU delegates.
  Android NNAPI status
  (https://developer.android.com/ndk/guides/neuralnetworks).

  Qualcomm QNN can lower and run models on the
  Adreno GPU and Hexagon processors, and SM8650
  is a supported Qualcomm AI platform. Current
  public integration paths—including
  ExecuTorch—are centered on deployment and
  inference, not general-purpose
  backpropagation. ExecuTorch’s Qualcomm
  backend
  (https://docs.pytorch.org/executorch/stable/backends-qualcomm.html).

  The practical pipeline is therefore:

  KG/source data
      ↓
  native CPU or GPU training
      ↓
  qualification and calibration
      ↓
  quantize / compile
      ↓
  CPU, Adreno GPU, or Hexagon NPU inference

  “Train on GPU; serve on NPU” is the realistic
  design.

  ## A credible training stack

  My first candidate would be Samsung’s open-
  source NNTrainer—not LiteRT or ExecuTorch—for
  the training engine.

  NNTrainer is specifically designed for
  Android on-device training and exposes
  embeddings, positional encoding, layer
  normalization, multi-head attention, AdamW,
  GELU, checkpointing, and complete
  from-scratch training. Those closely match
  your existing model. NNTrainer repository and
  capabilities
  (https://github.com/nntrainer/nntrainer).

  I would evaluate the runtimes this way:

  1. NNTrainer: first training-engine spike.
  2. Custom C++/Vulkan kernels: only for
     missing or performance-critical operations
     after profiling.

  3. Vendor OpenCL backend: high-performance
     SM8650 plugin, never the only backend.

  4. ExecuTorch or LiteRT/QNN: qualified
     inference and NPU packaging, not factory
     training. ExecuTorch currently ships
     Android CPU, Vulkan GPU, and Qualcomm NPU
     backends. Android integration
     (https://docs.pytorch.org/executorch/stable/using-executorch-android.html).

  5. MNN: worthwhile secondary investigation;
     it claims on-device training and has
     mature Android OpenCL/Vulkan support, but
     much of its current transformer work is
     inference-focused.

  The highest-risk porting point is not the
  encoder itself. It is reproducing your
  weighted contrastive loss, masks,
  normalization, and exact optimizer behavior.
  That needs gradient and loss-parity tests
  against the existing PyTorch implementation.

  ## Realistic model sizes

  - 0.1–5M parameters: clearly feasible for
    embedders, routers, relation predictors,
    classifiers and abstention heads.

  - 5–20M: plausible but experimental for
    rerankers or very narrow decoders,
    especially with short sequences and compact
    vocabularies.

  - 20–100M: possible as a research exercise,
    but long training, heat, memory bandwidth
    and dataset quality become dominant.

  - 100M+: not a sensible from-scratch phone
    factory target. Fine-tuning/adapters are a
    different proposition.

  The reliable product should therefore be
  called an Edge Specialist Factory, not
  promise general-purpose miniature LLMs. A 1–
  10M model that performs one contract
  extremely well and abstains outside that
  contract is more valuable than a weak 100M
  generator.

  ## The killer app architecture

  The APK should manufacture signed “specialist
  packs,” each containing:

  - immutable source/KG snapshot and source
    ledger;

  - deterministic tokenizer and training
    shards;

  - architecture and backend requirements;
  - weights plus CPU/GPU/NPU variants;
  - training receipt and thermal history;
  - held-out evaluation results;
  - confidence calibration and abstention
    threshold;

  - provenance, version, rollback point and
    compatibility manifest.

  Its killer features would be:

  - “Build a specialist from this folder/
    repository” entirely offline.

  - Device profiler that selects model size,
    batch size and backend.

  - Thermal-aware overnight training with
    pause/resume.

  - Strict stage isolation: unload the
    synthetic-data LLM before training.

  - Hardware race mode: CPU vs Vulkan vs OpenCL
    on an identical seed.

  - Automatic comparison against BM25/graph-
    only baselines.

  - Specialist router that composes several
    narrow models while KG-RAG remains the
    factual authority.

  - Verifiable model receipts: exactly what
    data produced this checkpoint and whether
    it passed qualification.

  ## The first de-risking experiment

  Before building a 5–10M generator, port the
  exact existing 1.36M embedder into a minimal
  APK and run:

  1. Existing PRoot/PyTorch CPU baseline.
  2. APK-native NNTrainer CPU.
  3. APK-native GPU training using OpenCL or
     Vulkan.

  Use identical tokenized tensors,
  initialization seed and 200 training steps.
  Measure:

  - milliseconds per step;
  - loss/gradient parity;
  - peak memory;
  - thermal headroom and throttling;
  - energy per step;
  - checkpoint/resume correctness;
  - held-out Recall@5/10, MRR and abstention
    behavior.

  Also ensure Phi-4-mini is completely unloaded
  before training.

  If the native GPU path cannot outperform
  native CPU sustainably, retain CPU training
  and use GPU/NPU only for inference. If it
  does, you have the foundation for a genuinely
  novel edge specialist factory.

  So: yes, the APK meaningfully changes
  feasibility—but through native kernels,
  vendor-driver access, orchestration, thermal
  control and deployable inference. It does not
  make Hexagon a general training device, and
  it does not by itself make code faster. The
  existing prototype is already small enough
  that a well-designed APK port has a credible
  chance of turning a four-hour fragile
  experiment into a repeatable product
  workflow.
"
