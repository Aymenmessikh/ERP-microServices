package com.example.adminservice.Controller;

import com.example.adminservice.Dto.Role.RoleRequest;
import com.example.adminservice.Dto.Role.RoleResponse;
import com.example.adminservice.Services.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/admin/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PreAuthorize("hasAuthority('CREATE_ROLE')")
    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest roleRequest){
        RoleResponse roleResponse = roleService.createRole(roleRequest);
        return new ResponseEntity<>(roleResponse, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('READ_ALL_ROLES')")
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles(){
        List<RoleResponse> roleResponses = roleService.getAllRoles();
        return new ResponseEntity<>(roleResponses, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('READ_ROLE')")
    @GetMapping("{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable Long id){
        RoleResponse roleResponse = roleService.getRoleById(id);
        return new ResponseEntity<>(roleResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('READ_ROLES_BY_MODULE')")
    @GetMapping("byModule/{id}")
    public ResponseEntity<List<RoleResponse>> getAllRoleByModule(@PathVariable Long id){
        List<RoleResponse> roleResponses = roleService.getAllRoleByModule(id);
        return new ResponseEntity<>(roleResponses, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('UPDATE_ROLE')")
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> updateRole(@RequestBody RoleRequest roleRequest, @PathVariable Long id){
        RoleResponse roleResponse = roleService.updateRole(roleRequest, id);
        return new ResponseEntity<>(roleResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ENABLE_ROLE')")
    @PutMapping("/enable/{id}")
    public ResponseEntity<RoleResponse> enableRole(@PathVariable Long id){
        RoleResponse roleResponse = roleService.enableRole(id);
        return new ResponseEntity<>(roleResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('DISABLE_ROLE')")
    @PutMapping("/disable/{id}")
    public ResponseEntity<RoleResponse> disableRole(@PathVariable Long id){
        RoleResponse roleResponse = roleService.disableRole(id);
        return new ResponseEntity<>(roleResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('DELETE_ROLE')")
    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable Long id){
        roleService.deleteRole(id);
    }

    @PreAuthorize("hasAuthority('GRANT_AUTHORITY_TO_ROLE')")
    @PostMapping("{RoleId}/{AuthorityId}")
    public ResponseEntity<RoleResponse> grantAuthorityToRole(@PathVariable Long RoleId, @PathVariable Long AuthorityId){
        RoleResponse roleResponse = roleService.grantAuthorityToRole(RoleId, AuthorityId);
        return new ResponseEntity<>(roleResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('REVOKE_AUTHORITY_FROM_ROLE')")
    @DeleteMapping("{RoleId}/{AuthorityId}")
    public ResponseEntity<RoleResponse> revokeAuthorityToRole(@PathVariable Long RoleId, @PathVariable Long AuthorityId){
        RoleResponse roleResponse = roleService.revokeAuthorityFromRole(RoleId, AuthorityId);
        return new ResponseEntity<>(roleResponse, HttpStatus.OK);
    }
}
