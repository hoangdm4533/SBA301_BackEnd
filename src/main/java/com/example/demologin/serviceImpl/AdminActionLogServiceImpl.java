package com.example.demologin.serviceImpl;

import com.example.demologin.entity.AdminActionLog;
import com.example.demologin.repository.AdminActionLogRepository;
import com.example.demologin.service.AdminActionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminActionLogServiceImpl implements AdminActionLogService {
    @Autowired private AdminActionLogRepository logRepo;
    @Override
    public void save(AdminActionLog log) {
        logRepo.save(log);
    }
} 