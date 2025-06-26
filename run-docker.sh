#!/bin/bash

# Docker Compose Management Script
# Usage: ./run-docker.sh <token_file> [--build]
# Purpose: Start/stop containers with optional rebuild

# Validate at least one argument (token file) was provided
if [ $# -eq 0 ]; then
    echo "Error: Token file not specified"
    echo "Usage: $0 <token_file_path> [--build]"
    exit 1
fi

# Verify the token file exists
TOKEN_FILE="$1"
if [ ! -f "$TOKEN_FILE" ]; then
    echo "Error: Token file '$TOKEN_FILE' not found"
    exit 1
fi

# Check for --build flag (second argument)
BUILD_FLAG=""
if [ $# -ge 2 ] && [ "$2" == "--build" ]; then
    BUILD_FLAG="--build"
    echo "Build flag detected - containers will be rebuilt"
fi

# Get absolute path to token file for reliability
TOKEN_FILE=$(realpath "$TOKEN_FILE")
echo "Starting Docker with token file: $TOKEN_FILE"

# Change to docker directory (where compose file lives)
echo "Stopping any running containers..."
cd docker || { echo "Error: docker directory not found"; exit 1; }

# Clean up existing containers
docker-compose down

# Start containers with environment variable
echo "Starting containers..."
GPT_TOKEN=$(cat "$TOKEN_FILE") docker-compose up $BUILD_FLAG