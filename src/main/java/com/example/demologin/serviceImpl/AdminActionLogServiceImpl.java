package com.example.demologin.serviceImpl;

import com.example.demologin.dto.response.AdminActionLogResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.entity.AdminActionLog;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.mapper.AdminActionLogMapper;
import com.example.demologin.repository.AdminActionLogRepository;
import com.example.demologin.service.AdminActionLogService;
import com.example.demologin.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminActionLogServiceImpl implements AdminActionLogService {
    @Autowired private AdminActionLogRepository adminActionLogRepository;
    @Autowired private AdminActionLogMapper adminActionLogMapper;
    @Override
    public void save(AdminActionLog log) {
        adminActionLogRepository.save(log);
    }

    @Override
    public ResponseObject getLogs(Pageable pageable) {
        Page<AdminActionLog> logPage = adminActionLogRepository.findAll(pageable);

        if (logPage.isEmpty()) {
            throw new NotFoundException("No admin action logs found");
        }

        Page<AdminActionLogResponse> responsePage = logPage.map(adminActionLogMapper::toResponseDto);

        return new ResponseObject(
                HttpStatus.OK.value(),
                "Success",
                PageUtils.toPageResponse(responsePage)
        );
    }

    @Override
    public ResponseObject getAllLogs(int page, int size) {
        List<AdminActionLogResponse> logs = adminActionLogRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "actionTime")))
                .stream()
                .map(adminActionLogMapper::toResponseDto)
                .toList();
        
        String message = logs.isEmpty() ? "Empty list!" : "Get all admin action logs successfully";
        
        return new ResponseObject(200, message, logs);
    }
} 