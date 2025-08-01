#!/bin/bash

# Test script for My Login History API
echo "🔐 Testing My Login History API"
echo "================================"

# Base URL
BASE_URL="http://localhost:8080"

echo "📝 Step 1: Login to get JWT token..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "member",
    "password": "member123"
  }')

echo "Login Response: $LOGIN_RESPONSE"

# Extract token from response (assuming it's in "token" field)
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "❌ Failed to get token from login response"
    exit 1
fi

echo "✅ Token obtained: ${TOKEN:0:50}..."

echo ""
echo "📋 Step 2: Get my login history..."
HISTORY_RESPONSE=$(curl -s -X GET "$BASE_URL/api/user-activity-logs/my-login-history?page=0&size=5" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json")

echo "My Login History Response:"
echo $HISTORY_RESPONSE | jq '.' || echo $HISTORY_RESPONSE

echo ""
echo "🎯 Login History Fields Include:"
echo "- timestamp: Thời gian đăng nhập"
echo "- ipAddress: Địa chỉ IP (127.0.0.1 (localhost))"
echo "- browser: Trình duyệt (Google Chrome)"
echo "- browserVersion: Phiên bản browser"
echo "- operatingSystem: Hệ điều hành (Windows 10/11)"
echo "- device: Thiết bị (Windows Computer)"
echo "- deviceType: Loại thiết bị (Desktop)"
echo "- city, region, country: Vị trí địa lý"
echo "- deviceInfo: Thông tin tổng hợp thiết bị"
echo "- location: Vị trí tổng hợp"
