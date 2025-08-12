package com.example.demologin.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class LocationUtil {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class LocationInfo {
        private String city;
        private String region;
        private String country;
        private String countryCode;
    }

    public static LocationInfo getLocationFromIP(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return new LocationInfo("Unknown", "Unknown", "Unknown", "Unknown");
        }

        // Xử lý localhost và private IP
        if (isLocalOrPrivateIP(ipAddress)) {
            return new LocationInfo("Local", "Local Network", "Local", "LOCAL");
        }

        // TODO: Tích hợp với GeoIP service (ví dụ: MaxMind, IPinfo, etc.)
        // Hiện tại return unknown cho public IP
        // Trong thực tế, bạn có thể tích hợp với:
        // 1. MaxMind GeoIP2
        // 2. IPinfo API
        // 3. IP2Location
        // 4. GeoJS API (free)
        
        return new LocationInfo("Unknown", "Unknown", "Unknown", "Unknown");
    }

    private static boolean isLocalOrPrivateIP(String ipAddress) {
        if (ipAddress == null) return true;
        
        // Remove port and localhost info if exists  
        String ip = ipAddress.split("\\s+")[0].split(":")[0].trim();
        
        // Check for localhost
        if (ip.equals("127.0.0.1") || ip.equals("::1") || ip.equalsIgnoreCase("localhost")) {
            return true;
        }

        // Check for private IP ranges
        String[] parts = ip.split("\\.");
        if (parts.length != 4) return false;

        try {
            int firstOctet = Integer.parseInt(parts[0]);
            int secondOctet = Integer.parseInt(parts[1]);

            // 10.0.0.0 - 10.255.255.255
            if (firstOctet == 10) return true;

            // 172.16.0.0 - 172.31.255.255
            if (firstOctet == 172 && secondOctet >= 16 && secondOctet <= 31) return true;

            // 192.168.0.0 - 192.168.255.255
            if (firstOctet == 192 && secondOctet == 168) return true;

        } catch (NumberFormatException e) {
            return false;
        }

        return false;
    }

    public static String formatLocationInfo(LocationInfo locationInfo) {
        if (locationInfo.getCountryCode().equals("LOCAL")) {
            return "Local Network";
        }
        
        StringBuilder location = new StringBuilder();
        if (!locationInfo.getCity().equals("Unknown")) {
            location.append(locationInfo.getCity());
        }
        if (!locationInfo.getRegion().equals("Unknown") && !locationInfo.getRegion().equals(locationInfo.getCity())) {
            if (location.length() > 0) location.append(", ");
            location.append(locationInfo.getRegion());
        }
        if (!locationInfo.getCountry().equals("Unknown")) {
            if (location.length() > 0) location.append(", ");
            location.append(locationInfo.getCountry());
        }
        
        return location.length() > 0 ? location.toString() : "Unknown Location";
    }

    // Method để tích hợp với GeoIP service thực tế
    // Ví dụ với MaxMind GeoIP2
    /*
    public static LocationInfo getLocationFromIPWithGeoIP2(String ipAddress) {
        try {
            // Cần add dependency: com.maxmind.geoip2:geoip2
            File database = new File("path/to/GeoLite2-City.mmdb");
            DatabaseReader reader = new DatabaseReader.Builder(database).build();
            
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            CityResponse response = reader.city(inetAddress);
            
            String city = response.getCity().getName();
            String region = response.getMostSpecificSubdivision().getName();
            String country = response.getCountry().getName();
            String countryCode = response.getCountry().getIsoCode();
            
            return new LocationInfo(city, region, country, countryCode);
            
        } catch (Exception e) {
            return new LocationInfo("Unknown", "Unknown", "Unknown", "Unknown");
        }
    }
    */
}
