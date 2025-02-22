package com.example.auditlogservice.Service;

import com.example.auditlogservice.Dto.AuditEvent;
import com.example.auditlogservice.Dto.AuditResponseDto;
import com.example.auditlogservice.Entity.AuditLog;
import com.example.auditlogservice.Mapper.AuditLogMapper;
import com.example.auditlogservice.Repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditRepository auditRepository;
    private final AuditLogMapper auditLogMapper;

    public AuditLog saveAuditLog(AuditEvent auditEvent) {
        AuditLog auditLog=auditLogMapper.EntityFromDto(auditEvent);
        return auditRepository.save(auditLog);
    }
    public List<AuditResponseDto> getAllAuditLogs() {
        List<AuditLog> auditLogs=auditRepository.findAll();
        return auditLogs.stream().map(auditLogMapper::DtoFromEntity).collect(Collectors.toList());
    }
}
