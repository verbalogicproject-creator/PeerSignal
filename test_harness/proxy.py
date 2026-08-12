from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

class CodePayload(BaseModel):
    code: str

@app.post("/api/v1/sandbox/analyze")
def analyze_code(payload: CodePayload):
    # Mocking the kg-factory logic for the test
    print(f"Received code: {payload.code}")
    return {
        "status": "success", 
        "graph_nodes": [{"id": "NodeA", "type": "Function"}],
        "message": "Successfully hit the Python proxy from Kotlin!"
    }
