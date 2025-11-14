package com.example.demologin.service;

public interface SessionManagementService {

    void logoutCurrentDevice();
    void logoutFromAllDevices();
    void forceLogoutUser(Long userId);
}
