from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import time

app = FastAPI(title="SmartMarket Python Service")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173", "http://localhost:3000"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


class AnalyzeInput(BaseModel):
    data: dict


@app.post("/api/analyze")
def analyze(payload: AnalyzeInput):
    start = time.time()
    # Lógica de IA de ejemplo (eco y score ficticio)
    result = {
        "echo": payload.data,
        "score": 0.87,
        "labels": ["example", "demo"],
    }
    duration_ms = int((time.time() - start) * 1000)
    return {"status": "ok", "duration_ms": duration_ms, "result": result}