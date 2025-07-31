package com.example.demologin.service;

import com.example.demologin.dto.request.userActionLog.*;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.entity.UserActionLog;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface UserActionLogService {
    
    // Internal methods used by other services
    UserActionLog save(UserActionLog userActionLog);
    void deleteOldLogs(LocalDateTime beforeDate);
    
    // Controller methods - return ResponseEntity<ResponseObject>
    ResponseEntity<ResponseObject> getAllActionLogs(int page, int size);
    ResponseEntity<ResponseObject> getActionLogById(GetActionLogByIdRequest request);
    ResponseEntity<ResponseObject> getActionLogsByUserId(GetActionLogsByUserIdRequest request);
    ResponseEntity<ResponseObject> getActionLogsByActionType(GetActionLogsByTypeRequest request);
    ResponseEntity<ResponseObject> getActionLogsByTargetType(GetActionLogsByTargetTypeRequest request);
    ResponseEntity<ResponseObject> getActionLogsByDateRange(GetActionLogsByDateRangeRequest request);
    ResponseEntity<ResponseObject> deleteActionLog(DeleteActionLogRequest request);
}
