# User Activity Log Migration Guide

## Tổng quan thay đổi

Đã cập nhật hệ thống User Activity Log để cung cấp thông tin chi tiết hơn về thiết bị và vị trí của người dùng:

### Thay đổi chính:

1. **Loại bỏ các fields không cần thiết:**
   - `editorId` - Không liên quan đến login/logout tracking
   - `editorUsername` - Không liên quan đến login/logout tracking

2. **Cập nhật thông tin user:**
   - Thay `username` bằng `fullName` để hiển thị tên đầy đủ
   - Đảm bảo `userId` và `fullName` được lưu đúng khi có user đăng nhập

3. **Thêm thông tin thiết bị:**
   - `browser` - Tên trình duyệt (Chrome, Firefox, Safari, etc.)
   - `browserVersion` - Phiên bản trình duyệt
   - `operatingSystem` - Hệ điều hành (Windows 10/11, macOS, Android, etc.)
   - `device` - Loại thiết bị (iPhone, Android Phone, Windows Computer, etc.)
   - `deviceType` - Phân loại (Mobile, Tablet, Desktop)

4. **Thêm thông tin vị trí:**
   - `city` - Thành phố
   - `region` - Tỉnh/Bang
   - `country` - Quốc gia
   - `countryCode` - Mã quốc gia (VN, US, etc.)

5. **Response JSON mới bao gồm:**
   - `deviceInfo` - Formatted string hiển thị đẹp về thiết bị
   - `location` - Formatted string hiển thị đẹp về vị trí

## Utility Classes mới

### UserAgentUtil
Phân tích User Agent string để trích xuất thông tin:
- Trình duyệt và phiên bản
- Hệ điều hành
- Loại thiết bị

### LocationUtil
Xử lý thông tin vị trí từ IP address:
- Hiện tại support localhost và private IP
- Có thể tích hợp với GeoIP service (MaxMind, IPinfo, etc.)

## Migration Steps

### 1. Chạy Database Migration
```sql
-- File: src/main/resources/db/migration/V2__update_user_activity_logs.sql
-- Sẽ tự động chạy khi khởi động ứng dụng
```

### 2. Response JSON mới

**Trước:**
```json
{
  "id": 2,
  "activityType": "LOGIN_ATTEMPT",
  "userId": null,
  "username": null,
  "editorId": null,
  "editorUsername": null,
  "timestamp": "2025-08-01T14:38:23",
  "status": "SUCCESS",
  "details": "User login attempt",
  "ipAddress": "127.0.0.1 (localhost)",
  "userAgent": null
}
```

**Sau:**
```json
{
  "id": 2,
  "activityType": "LOGIN_ATTEMPT",
  "userId": 1,
  "fullName": "Nguyen Van A",
  "timestamp": "2025-08-01T14:38:23",
  "status": "SUCCESS",
  "details": "User login attempt",
  "ipAddress": "127.0.0.1 (localhost)",
  "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
  "browser": "Google Chrome",
  "browserVersion": "91.0.4472.124",
  "operatingSystem": "Windows 10/11",
  "device": "Windows Computer", 
  "deviceType": "Desktop",
  "city": "Local",
  "region": "Local Network",
  "country": "Local",
  "countryCode": "LOCAL",
  "deviceInfo": "Google Chrome 91.0.4472.124 on Windows 10/11 (Desktop)",
  "location": "Local Network"
}
```

## Testing

Chạy unit tests để verify UserAgentUtil:
```bash
mvn test -Dtest=UserAgentUtilTest
```

## Tích hợp GeoIP Service (Tùy chọn)

Để có thông tin location chính xác hơn, có thể tích hợp với:

1. **MaxMind GeoIP2** (Recommended)
2. **IPinfo API**
3. **IP2Location** 
4. **GeoJS API** (Free tier)

Xem comments trong `LocationUtil.java` để biết cách tích hợp.

## Backward Compatibility

- API endpoints không thay đổi
- Các fields cũ vẫn có trong response (trừ editor fields)
- Migration tự động và safe
- Không break existing clients
