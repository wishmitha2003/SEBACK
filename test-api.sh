#!/bin/bash

BASE_URL="http://localhost:8082"

echo "========================================="
echo "Testing EzyEnglish Authentication API"
echo "========================================="
echo ""

# Test 1: Signup
echo "1. Testing Signup endpoint..."
echo "POST $BASE_URL/api/auth/signup"
signup_response=$(curl -s -X POST "$BASE_URL/api/auth/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser01",
    "email": "testuser@example.com",
    "password": "Test@123",
    "firstName": "Test",
    "lastName": "User",
    "phone": "+1234567890",
    "roles": ["student"]
  }')

echo "Response: $signup_response"
echo ""

# Extract token from response (using basic parsing)
token=$(echo $signup_response | grep -o '"token":"[^"]*' | cut -d'"' -f4)

echo "========================================="
echo ""

# Test 2: Signin
echo "2. Testing Signin endpoint..."
echo "POST $BASE_URL/api/auth/signin"
signin_response=$(curl -s -X POST "$BASE_URL/api/auth/signin" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser@example.com",
    "password": "Test@123"
  }')

echo "Response: $signin_response"
echo ""
echo "========================================="
echo ""
echo "Testing complete!"
