package com.example.adminservice.Services;

import com.example.adminservice.Config.Exceptions.MyResourceNotFoundException;
import com.example.adminservice.Config.Exceptions.RessourceAlreadyDisabledException;
import com.example.adminservice.Config.Exceptions.RessourceAlreadyEnabledException;
import com.example.adminservice.Dto.Module.ModuleRequest;
import com.example.adminservice.Dto.Module.ModuleResponse;
import com.example.adminservice.Entity.Module;
import com.example.adminservice.Entity.Profile;
import com.example.adminservice.Entity.User;
import com.example.adminservice.Mapper.Module.ModuleMapper;
import com.example.adminservice.Repository.ModuleRepository;
import com.example.adminservice.Repository.ProfileRepository;
import com.example.adminservice.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final ModuleMapper moduleMapper;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ModuleResponse createModule(ModuleRequest moduleRequest) {
        Module module = moduleRepository.save(moduleMapper.EntityFromDto(moduleRequest));
        return moduleMapper.DtoFromEntity(module);
    }

    public List<ModuleResponse> getAllModules() {
        return moduleRepository.findAll().stream().map(moduleMapper::DtoFromEntity).collect(Collectors.toList());
    }

    public ModuleResponse getModuleById(Long id) {
        return moduleMapper.DtoFromEntity(moduleRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Module not found with id: " + id)));
    }

    public ModuleResponse getModuleByCode(String code) {
        return moduleMapper.DtoFromEntity(moduleRepository.findByModuleCode(code)
                .orElseThrow(() -> new MyResourceNotFoundException("Module not found with code: " + code)));
    }



    public ModuleResponse updateModule(ModuleRequest moduleRequest, Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Module not found with id: " + id));
        module.setModuleName(moduleRequest.getModuleName());
        module.setModuleCode(moduleRequest.getModuleCode());
        module.setIcon(moduleRequest.getIcon());
        module.setUri(moduleRequest.getUri());
        module.setColor(moduleRequest.getColor());
        Module module1 = moduleRepository.save(module);
        return moduleMapper.DtoFromEntity(module1);
    }

    public void deleteModule(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Module not found with id: " + id));
        moduleRepository.delete(module);
    }

    public ModuleResponse enableModule(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Module not found with id: " + id));
        if (!module.getActif()) {
            module.setActif(true);
            moduleRepository.save(module);
            return moduleMapper.DtoFromEntity(module);
        } else
            throw new RessourceAlreadyEnabledException("Module with ID " + id + " is already enabled");

    }

    public ModuleResponse disableModule(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Module not found with id: " + id));
        if (module.getActif()) {
            module.setActif(false);
            moduleRepository.save(module);
            return moduleMapper.DtoFromEntity(module);
        } else
            throw new RessourceAlreadyDisabledException("Module with ID " + id + " is already disabled");
    }

    public List<ModuleResponse> getModulesExcludingModuleProfile(Long idProfile) {
        List<Module> modules = moduleRepository.findAll();
        List<Module> modulesProfile = profileRepository.findById(idProfile)
                .map(Profile::getModules)
                .orElse(Collections.emptyList());
        modules.removeAll(modulesProfile);
        return modules.stream().map(moduleMapper::DtoFromEntity).collect(Collectors.toList());
    }
    public List<ModuleResponse> getModulesByProfile(String username) {
        User user=userRepository.findByUserName(username).orElseThrow();
        Profile profile=user.getActifProfile();
        List<Module> modulesProfile = profile.getModules();
        return modulesProfile.stream().map(moduleMapper::DtoFromEntity).collect(Collectors.toList());
    }
    public Long count(){
        return moduleRepository.count();
    }
}
