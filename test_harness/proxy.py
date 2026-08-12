import time
import threading
import uuid
from typing import Dict, Any
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

app = FastAPI(title="PeerSignal Companion Engine")

# In-memory storage for training runs
runs_db: Dict[str, Dict[str, Any]] = {}

class TrainingRequest(BaseModel):
    dataset_id: str
    template_id: str
    seed: int
    budget_epochs: int

class PauseRequest(BaseModel):
    pass

def simulated_training_loop(run_id: str, budget_epochs: int):
    """Simulates the MiniULTRA-SKG-v0 training loop."""
    runs_db[run_id]["status"] = "running"
    
    for epoch in range(1, budget_epochs + 1):
        if runs_db[run_id]["status"] == "paused":
            break
        if runs_db[run_id]["status"] == "cancelled":
            return
            
        time.sleep(1.5)  # Simulate heavy math
        loss = max(0.1, 1.5 - (epoch * 0.05)) # Mock decreasing loss
        
        runs_db[run_id]["progress"] = {
            "epoch": epoch,
            "loss": round(loss, 4),
            "thermal_status": "LIGHT"
        }
    
    if runs_db[run_id]["status"] == "running":
        runs_db[run_id]["status"] = "completed"

@app.get("/v1/health")
def health_check():
    return {"status": "ok"}

@app.get("/v1/capabilities")
def capabilities():
    return {
        "engine_id": "CompanionPythonEngine_v1",
        "cpu_support": True,
        "gpu_support": False,
        "npu_support": False,
        "supported_templates": ["MiniULTRA-SKG-v0", "MicroRetriever-v0"]
    }

@app.post("/v1/runs")
def start_run(req: TrainingRequest):
    run_id = str(uuid.uuid4())
    runs_db[run_id] = {
        "run_id": run_id,
        "status": "preparing",
        "request": req.dict(),
        "progress": {"epoch": 0, "loss": 0.0, "thermal_status": "NORMAL"}
    }
    
    # Start background training thread
    thread = threading.Thread(target=simulated_training_loop, args=(run_id, req.budget_epochs))
    thread.daemon = True
    thread.start()
    
    return {"run_id": run_id}

@app.get("/v1/runs/{run_id}")
def get_run(run_id: str):
    if run_id not in runs_db:
        raise HTTPException(status_code=404, detail="Run not found")
    return runs_db[run_id]

@app.post("/v1/runs/{run_id}/pause")
def pause_run(run_id: str):
    if run_id not in runs_db:
        raise HTTPException(status_code=404, detail="Run not found")
    runs_db[run_id]["status"] = "paused"
    return {"status": "paused"}

@app.post("/v1/runs/{run_id}/resume")
def resume_run(run_id: str):
    if run_id not in runs_db:
        raise HTTPException(status_code=404, detail="Run not found")
    
    # Simple resume simulation
    runs_db[run_id]["status"] = "running"
    req = runs_db[run_id]["request"]
    current_epoch = runs_db[run_id]["progress"]["epoch"]
    remaining_epochs = req["budget_epochs"] - current_epoch
    
    if remaining_epochs > 0:
        thread = threading.Thread(target=simulated_training_loop, args=(run_id, req["budget_epochs"]))
        thread.daemon = True
        thread.start()
        
    return {"status": "running"}

if __name__ == "__main__":
    import uvicorn
    # Bind only to loopback for security
    uvicorn.run(app, host="127.0.0.1", port=8000)
