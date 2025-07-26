package com.example.demologin.service;

import com.example.demologin.dto.response.ResponseObject;
import org.springframework.data.domain.Pageable;

public interface AdminActionLogService {
    void save(com.example.demologin.entity.AdminActionLog log);
    ResponseObject getLogs(Pageable pageable);


} 