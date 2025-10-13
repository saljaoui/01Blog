#!/bin/bash

echo "🚀 Starting 01Blog Application..."
echo ""

# Start PostgreSQL
echo "📦 Starting PostgreSQL..."
sudo service postgresql start
echo "✅ PostgreSQL started"
echo ""

# Start Backend in background
echo "☕ Starting Backend (Spring Boot)..."
cd backend
mvn spring-boot:run > backend.log 2>&1 &
BACKEND_PID=$!
echo "✅ Backend starting... (PID: $BACKEND_PID)"
echo ""

# Wait a bit for backend to initialize
echo "⏳ Waiting 10 seconds for backend to initialize..."
sleep 10

# Start Frontend in background
echo "🎨 Starting Frontend (Angular)..."
cd ../frontend
npm start > frontend.log 2>&1 &
FRONTEND_PID=$!
echo "✅ Frontend starting... (PID: $FRONTEND_PID)"
echo ""

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🎉 01Blog Application Started!"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📍 Backend:  http://localhost:8080"
echo "📍 Frontend: http://localhost:4200"
echo ""
echo "Backend PID:  $BACKEND_PID"
echo "Frontend PID: $FRONTEND_PID"
echo ""
echo "📄 Logs:"
echo "   Backend:  ~/01Blog/backend/backend.log"
echo "   Frontend: ~/01Blog/frontend/frontend.log"
echo ""
echo "To stop the application, run: ./stop.sh"
echo "Or press Ctrl+C and run: kill $BACKEND_PID $FRONTEND_PID"
echo ""

# Keep script running
wait