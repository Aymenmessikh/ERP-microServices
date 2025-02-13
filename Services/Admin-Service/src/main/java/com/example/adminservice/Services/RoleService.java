package com.example.adminservice.Services;

import com.example.adminservice.Dto.Role.RoleRequest;
import com.example.adminservice.Dto.Role.RoleResponse;
import com.example.adminservice.Entity.Authority;
import com.example.adminservice.Entity.Module;
import com.example.adminservice.Entity.Role;
import com.example.adminservice.Exeptions.*;
import com.example.adminservice.Mapper.Role.RoleMapper;
import com.example.adminservice.Repository.AuthorityRepository;
import com.example.adminservice.Repository.ModuleRepository;
import com.example.adminservice.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final ModuleRepository moduleRepository;
    private final AuthorityRepository authorityRepository;
    private final RoleMapper roleMapper;
    public RoleResponse createRole(RoleRequest roleRequest){
        Role role =roleRepository.save(roleMapper.EntityFromDto(roleRequest));
        return roleMapper.DtoFromEntity(role);
    }
    public List<RoleResponse> getAllRoles(){
        return roleRepository.findAll().stream().map(roleMapper::DtoFromEntity).collect(Collectors.toList());
    }
    public RoleResponse getRoleById(Long id) {
        Role role=roleRepository.findById(id)
                .orElseThrow(()->new MyResourceNotFoundException("Role not found with id: "+id));
        return roleMapper.DtoFromEntity(role);
    }
    public List<RoleResponse> getAllRoleByModule(Long id){
        Module module=moduleRepository.findById(id)
                .orElseThrow(()->new MyResourceNotFoundException("Module not found with id : "+id));
        List<Role> roles=roleRepository.findRolesByModule(module);
        if (roles !=null){
            return roles.stream().map(roleMapper::DtoFromEntity).collect(Collectors.toList());
        }else
            throw new MyResourceNotFoundException("No roles found for module with id:" + id);
    }
    public RoleResponse updateRole(RoleRequest roleRequest,Long id){
        Role role=roleRepository.findById(id)
                .orElseThrow(()->new MyResourceNotFoundException("Role not found with id: "+id));
        Module module=moduleRepository.findById(roleRequest.getModuleId())
                .orElseThrow(()-> new MyResourceNotFoundException("Module not found with id: "+roleRequest.getModuleId()));
        role.setLibelle(roleRequest.getLibelle());
        role.setModule(module);
        Role role1=roleRepository.save(role);
        return roleMapper.DtoFromEntity(role1);
    }
    public void deleteRole(Long id){
        Role role=roleRepository.findById(id)
                .orElseThrow(()->new MyResourceNotFoundException("Role not found with id: "+id));
        roleRepository.delete(role);
    }
    public RoleResponse enableRole(Long id){
        Role role=roleRepository.findById(id)
                .orElseThrow(()->new MyResourceNotFoundException("Role not found with id: "+id));
        if (role.getActif()==false){
            role.setActif(true);
            roleRepository.save(role);
            return roleMapper.DtoFromEntity(role);
        }else
            throw new RessourceAlreadyEnabledException("Role with ID " + id + " is already enabled");
    }
    public RoleResponse disableRole(Long id){
        Role role=roleRepository.findById(id)
                .orElseThrow(()->new MyResourceNotFoundException("Role not found with id: "+id));
        if (role.getActif()==true){
            role.setActif(false);
            roleRepository.save(role);
            return roleMapper.DtoFromEntity(role);
        }else
            throw new RessourceAlreadyEnabledException("Role with ID " + id + " is already disabled");
    }
    public RoleResponse grantAuthorityToRole(Long roleId,Long authorityId){
        Role role =roleRepository.findById(roleId)
                .orElseThrow(()->new MyResourceNotFoundException("Role not found with id: "+roleId));
        Authority authority=authorityRepository.findById(authorityId)
                .orElseThrow(()->new MyResourceNotFoundException("Authority not found with id: "+authorityId));
        if (role.getModule().getId() == authority.getModule().getId()){
            if (!role.getAuthoritys().contains(authority)){
                role.grantAuthority(authority);
                roleRepository.save(role);
                return roleMapper.DtoFromEntity(role);
            }else  throw new AuthorityAlreadyExistsException("Authority already exists for this role.");
        } else
            throw new ModuleMismatchException("Role and Authority do not belong to the same module.");
    }
    public RoleResponse revokeAuthorityFromRole(Long roleId,Long authorityId){
        Role role =roleRepository.findById(roleId)
                .orElseThrow(()->new MyResourceNotFoundException("Role not found with id: "+roleId));
        Authority authority=authorityRepository.findById(authorityId)
                .orElseThrow(()->new MyResourceNotFoundException("Authority not found with id: "+authorityId));
        if (role.getAuthoritys().contains(authority)){
            role.revokeAuthority(authority);
            roleRepository.save(role);
            return roleMapper.DtoFromEntity(role);
        }else  throw new AuthorityNoteExistsException("Authority not found in the role with id: " + roleId);
    }
}
