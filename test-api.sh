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

# Test 3: Signin as Admin (or create one)
echo "3. Testing Class Creation..."
# First, try to login as admin
admin_login=$(curl -s -X POST "$BASE_URL/api/auth/signin" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@example.com",
    "password": "AdminPassword123"
  }')

admin_token=$(echo $admin_login | grep -o '\"accessToken\":\"[^\"]*' | cut -d'\"' -f4)

if [ -z "$admin_token" ]; then
  echo "Admin login failed or no token found. Creating admin..."
  curl -s -X POST "$BASE_URL/api/auth/signup" \
    -H "Content-Type: application/json" \
    -d '{
      "username": "admin01",
      "email": "admin@example.com",
      "password": "AdminPassword123",
      "firstName": "Admin",
      "lastName": "User",
      "roles": ["admin"]
    }'
  admin_login=$(curl -s -X POST "$BASE_URL/api/auth/signin" \
    -H "Content-Type: application/json" \
    -d '{
      "username": "admin01",
      "password": "AdminPassword123"
    }')
  admin_token=$(echo $admin_login | grep -o '\"accessToken\":\"[^\"]*' | cut -d'\"' -f4)
fi

echo "Token: $admin_token"

# Test 4: Create Class
echo "4. Create Class..."
class_res=$(curl -s -X POST "$BASE_URL/api/classes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $admin_token" \
  -d '{
    "name": "Test Class CLI",
    "teacher": "CLI Teacher",
    "studentCount": 10,
    "schedule": "Fri 10AM",
    "branch": "Virtual"
  }')
echo "Response: $class_res"
echo ""

# Test 5: Fetch Classes
echo "5. Fetch Classes..."
fetch_res=$(curl -s -X GET "$BASE_URL/api/classes" \
  -H "Authorization: Bearer $admin_token")
echo "Response: $fetch_res"
echo ""
echo "Testing complete!"
