#!/bin/bash

echo "🛑 Stopping 01Blog Application..."

# Kill all Java processes (backend)
pkill -f "spring-boot:run"

# Kill all node processes (frontend)
pkill -f "ng serve"
pkill -f "npm start"

echo "✅ Application stopped!"