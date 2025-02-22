package com.example.auditlogservice.Controller;

import com.example.auditlogservice.Dto.AuditResponseDto;
import com.example.auditlogservice.Entity.AuditLog;
import com.example.auditlogservice.Service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/audit-log")
@RequiredArgsConstructor
public class AuditLogController {
    private final AuditLogService auditLogService;
    @GetMapping
    public ResponseEntity<List<AuditResponseDto>> getAuditLog() {
        List<AuditResponseDto> auditResponseDtos=auditLogService.getAllAuditLogs();
        return new ResponseEntity<>(auditResponseDtos, HttpStatus.OK);
    }
}
