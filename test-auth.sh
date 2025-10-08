#!/bin/bash

# Test Authentication Service
echo "=== Testing Documind Authentication Service ==="
echo ""

BASE_URL="http://localhost:8082"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "1. Testing User Registration..."
REGISTER_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }')

HTTP_CODE=$(echo "$REGISTER_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$REGISTER_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 200 ]; then
    echo -e "${GREEN}✓ Registration successful${NC}"
    echo "Response: $RESPONSE_BODY"
    TOKEN=$(echo "$RESPONSE_BODY" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    echo "Token: $TOKEN"
else
    echo -e "${RED}✗ Registration failed (HTTP $HTTP_CODE)${NC}"
    echo "Response: $RESPONSE_BODY"
fi

echo ""
echo "2. Testing User Login..."
LOGIN_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }')

HTTP_CODE=$(echo "$LOGIN_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$LOGIN_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 200 ]; then
    echo -e "${GREEN}✓ Login successful${NC}"
    echo "Response: $RESPONSE_BODY"
    TOKEN=$(echo "$RESPONSE_BODY" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    echo "Token: $TOKEN"
else
    echo -e "${RED}✗ Login failed (HTTP $HTTP_CODE)${NC}"
    echo "Response: $RESPONSE_BODY"
fi

echo ""
echo "3. Testing Token Validation..."
if [ -n "$TOKEN" ]; then
    VALIDATE_RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/auth/validate" \
      -H "Authorization: Bearer $TOKEN")
    
    HTTP_CODE=$(echo "$VALIDATE_RESPONSE" | tail -n1)
    RESPONSE_BODY=$(echo "$VALIDATE_RESPONSE" | sed '$d')
    
    if [ "$HTTP_CODE" -eq 200 ]; then
        echo -e "${GREEN}✓ Token validation successful${NC}"
        echo "Response: $RESPONSE_BODY"
    else
        echo -e "${RED}✗ Token validation failed (HTTP $HTTP_CODE)${NC}"
        echo "Response: $RESPONSE_BODY"
    fi
else
    echo -e "${RED}✗ No token available for validation${NC}"
fi

echo ""
echo "4. Testing Invalid Login..."
INVALID_LOGIN_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "wrongpassword"
  }')

HTTP_CODE=$(echo "$INVALID_LOGIN_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$INVALID_LOGIN_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 400 ]; then
    echo -e "${GREEN}✓ Invalid login correctly rejected${NC}"
    echo "Response: $RESPONSE_BODY"
else
    echo -e "${RED}✗ Invalid login should have been rejected (HTTP $HTTP_CODE)${NC}"
    echo "Response: $RESPONSE_BODY"
fi

echo ""
echo "=== Test Complete ==="
