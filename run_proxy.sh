#!/bin/bash
# Script to run the PeerSignal local Python Proxy

echo "Starting PeerSignal Python Proxy..."
echo "Setting up Python virtual environment..."
python3 -m venv venv
source venv/bin/activate

echo "Installing dependencies in venv..."
pip install fastapi uvicorn pydantic torch numpy scipy

echo "Starting uvicorn server on port 8000 (loopback only)..."
uvicorn test_harness.proxy:app --host 127.0.0.1 --port 8000
