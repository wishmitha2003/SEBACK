#!/bin/bash

BASE_URL="http://localhost:8082"

echo "========================================="
echo "Testing CORS Configuration"
echo "========================================="
echo ""

# Test 1: CORS Preflight for Signup
echo "1. Testing CORS Preflight for /api/auth/signup..."
curl -i -X OPTIONS "$BASE_URL/api/auth/signup" \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type"
echo ""
echo "========================================="
echo ""

# Test 2: CORS Preflight for Signin
echo "2. Testing CORS Preflight for /api/auth/signin..."
curl -i -X OPTIONS "$BASE_URL/api/auth/signin" \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type"
echo ""
echo "========================================="
echo ""

# Test 3: Actual Signup with CORS headers
echo "3. Testing Signup with CORS headers..."
curl -i -X POST "$BASE_URL/api/auth/signup" \
  -H "Origin: http://localhost:3000" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser123",
    "email": "testuser123@example.com",
    "password": "Test@123",
    "firstName": "Test",
    "lastName": "User",
    "phone": "+1234567890",
    "roles": ["student"]
  }'
echo ""
echo "========================================="
echo ""

# Test 4: Actual Signin with CORS headers
echo "4. Testing Signin with CORS headers..."
curl -i -X POST "$BASE_URL/api/auth/signin" \
  -H "Origin: http://localhost:3000" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser123@example.com",
    "password": "Test@123"
  }'
echo ""
echo "========================================="
echo ""

# Test 5: Signin with wrong credentials
echo "5. Testing Signin with wrong credentials..."
curl -i -X POST "$BASE_URL/api/auth/signin" \
  -H "Origin: http://localhost:3000" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "wrong@example.com",
    "password": "wrongpassword"
  }'
echo ""
echo "========================================="
echo ""
echo "Testing complete!"
