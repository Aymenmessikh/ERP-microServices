package com.example.adminservice.Services;

import com.example.adminservice.Dto.Authority.AuthorityRequest;
import com.example.adminservice.Dto.Authority.AuthorityResponse;
import com.example.adminservice.Entity.Authority;
import com.example.adminservice.Entity.AuthorityType;
import com.example.adminservice.Entity.Module;
import com.example.adminservice.Entity.Role;
import com.example.adminservice.Exeptions.MyResourceNotFoundException;
import com.example.adminservice.Exeptions.RessourceAlreadyEnabledException;
import com.example.adminservice.Mapper.Authority.AuthorityMapper;
import com.example.adminservice.Repository.AuthorityRepository;
import com.example.adminservice.Repository.AuthorityTypeRepository;
import com.example.adminservice.Repository.ModuleRepository;
import com.example.adminservice.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorityService {
    private final AuthorityRepository authorityRepository;
    private final AuthorityTypeRepository authorityTypeRepository;
    private final ModuleRepository moduleRepository;
    private final RoleRepository roleRepository;
    private final AuthorityMapper authorityMapper;
    public AuthorityResponse createAuthority(AuthorityRequest authorityRequest){
        Authority authority =authorityRepository.save(authorityMapper.EntityFromDto(authorityRequest));
        return authorityMapper.DtoFromEntity(authority);
    }
    public List<AuthorityResponse> getAllAuthoritys(){
        return authorityRepository.findAll().stream().map(authorityMapper::DtoFromEntity).collect(Collectors.toList());
    }
    public AuthorityResponse getAuthorityById(Long id) {
        Authority authority=authorityRepository.findById(id)
                .orElseThrow(()->new MyResourceNotFoundException("Authority not found with id: "+id));
        return authorityMapper.DtoFromEntity(authority);
    }
    public List<AuthorityResponse> getAllAuthorityByModule(Long id){
        Module module=moduleRepository.findById(id)
                .orElseThrow(()->new MyResourceNotFoundException("Module not found with id : "+id));
        List<Authority> authorities=authorityRepository.getAuthoritiesByModule(module);
        if (authorities !=null){
            return authorities.stream().map(authorityMapper::DtoFromEntity).collect(Collectors.toList());
        }else
            throw new MyResourceNotFoundException("No authority found for module with id:" + id);
    }
    public List<AuthorityResponse> getAllAuthorityByRole(Long id){
        Role role=roleRepository.findById(id)
                .orElseThrow(()->new MyResourceNotFoundException("Role not found with id : "+id));
        List<Authority> authorities=authorityRepository.getAuthoritiesByRoles(role);
        if (authorities !=null){
            return authorities.stream().map(authorityMapper::DtoFromEntity).collect(Collectors.toList());
        }else
            throw new MyResourceNotFoundException("No authority found for Role with id:" + id);
    }
    public AuthorityResponse updateAuthority(AuthorityRequest authorityRequest,Long id){
        Authority authority=authorityRepository.findById(id)
                .orElseThrow(()->new MyResourceNotFoundException("Authority not found with id: "+id));
        AuthorityType authorityType=authorityTypeRepository.
                findById(authorityRequest.getAuthorityTypeId())
                .orElseThrow(()->new MyResourceNotFoundException("Authority Type not found with id: "+authorityRequest.getAuthorityTypeId()));
        Module module=moduleRepository.findById(authorityRequest.getModuleId())
                .orElseThrow(()-> new MyResourceNotFoundException("Module not found with id: "+authorityRequest.getModuleId()));
        authority.setLibelle(authorityRequest.getLibelle());
        authority.setAuthorityType(authorityType);
        authority.setModule(module);
        Authority authority1=authorityRepository.save(authority);
        return authorityMapper.DtoFromEntity(authority1);
    }
    public void deleteAuthority(Long id){
        Authority authority=authorityRepository.findById(id)
                .orElseThrow(()->new MyResourceNotFoundException("Authority not found with id: "+id));
        authorityRepository.delete(authority);
    }
    public AuthorityResponse enableAuthority(Long id){
        Authority authority=authorityRepository.findById(id)
                .orElseThrow(()->new MyResourceNotFoundException("Authority not found with id: "+id));
        if (authority.getActif()==false){
            authority.setActif(true);
            authorityRepository.save(authority);
            return authorityMapper.DtoFromEntity(authority);
        }else
            throw new RessourceAlreadyEnabledException("Authority with ID " + id + " is already enabled");
    }
    public AuthorityResponse disableAuthority(Long id){
        Authority authority=authorityRepository.findById(id)
                .orElseThrow(()->new MyResourceNotFoundException("Authority not found with id: "+id));
        if (authority.getActif()==true){
            authority.setActif(false);
            authorityRepository.save(authority);
            return authorityMapper.DtoFromEntity(authority);
        }else
            throw new RessourceAlreadyEnabledException("Authority with ID " + id + " is already disabled");
    }

}
