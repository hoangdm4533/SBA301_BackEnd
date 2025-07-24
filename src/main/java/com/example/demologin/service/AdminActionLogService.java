package com.example.demologin.service;

import com.example.demologin.entity.AdminActionLog;
import com.example.demologin.repository.AdminActionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public interface AdminActionLogService {
    void save(com.example.demologin.entity.AdminActionLog log);
} 