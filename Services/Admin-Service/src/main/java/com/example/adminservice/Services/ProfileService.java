package com.example.adminservice.Services;

import com.example.adminservice.Dto.Profile.ProfileAuthorityResponse;
import com.example.adminservice.Dto.Profile.ProfileForModuleResponse;
import com.example.adminservice.Dto.Profile.ProfileRequest;
import com.example.adminservice.Dto.Profile.ProfileResponse;
import com.example.adminservice.Dto.User.UserResponse;
import com.example.adminservice.Entity.*;
import com.example.adminservice.Entity.Module;
import com.example.adminservice.Exeptions.MyResourceNotFoundException;
import com.example.adminservice.Exeptions.ProfileAlreadyActiveException;
import com.example.adminservice.Exeptions.RessourceAlreadyDisabledException;
import com.example.adminservice.Exeptions.RessourceAlreadyEnabledException;
import com.example.adminservice.Mapper.Profile.ProfileMapper;
import com.example.adminservice.Mapper.User.UserMapper;
import com.example.adminservice.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final GroupeRepository groupeRepository;
    private final RoleRepository roleRepository;
    private final ModuleRepository moduleRepository;
    private final AuthorityRepository authorityRepository;
    private final ProfileAuthorityRepository profileAuthorityRepository;
    private final ProfileMapper profileMapper;
    private final UserMapper userMapper;

    public ProfileResponse addProfileToUser(ProfileRequest profileRequest, Long profileTypeId) {
        Profile profile;
        User user = userRepository.findById(profileRequest.getUserId())
                .orElseThrow(() -> new MyResourceNotFoundException("User not found with id:" + profileRequest.getUserId()));
        Groupe groupe = null;
        if (profileRequest.getGroupId() != null) {
            groupe = groupeRepository.findById(profileRequest.getGroupId())
                    .orElseThrow(() -> new MyResourceNotFoundException("Groupe not found with id" + profileRequest.getGroupId()));
        }
        if (profileTypeId != -1) {
            Profile profileType = profileRepository.findById(profileTypeId)
                    .orElseThrow(() -> new MyResourceNotFoundException("Profile not found with id:" + profileTypeId));
            profile = Profile.builder()
                    .libelle(profileType.getLibelle())
                    .groupe(profileType.getGroupe())
                    .roles(new ArrayList<>(profileType.getRoles()))
                    .modules(new ArrayList<>(profileType.getModules()))
                    .user(user)
                    .actif(true)
                    .build();
            Profile savedProfile = profileRepository.save(profile);

            List<ProfileAuthority> newProfileAuthorities = new ArrayList<>();
            for (ProfileAuthority authority : profile.getProfileAuthorities()) {
                ProfileAuthority newAuthority = ProfileAuthority.builder()
                        .authority(authority.getAuthority())
                        .granted(authority.getGranted())
                        .profile(savedProfile) // Now reference the saved profile
                        .build();
                newProfileAuthorities.add(newAuthority);
            }

            // Set the saved profileAuthorities to the savedProfile
            savedProfile.setProfileAuthorities(newProfileAuthorities);

            // Update the saved profile with the authorities
            profileRepository.save(savedProfile);
        } else {
            profile = Profile.builder()
                    .libelle(profileRequest.getLibelle())
                    .groupe(groupe)
                    .user(user)
                    .actif(true)
                    .build();
        }
        profileRepository.save(profile);
        user.addProfile(profile);
        userRepository.save(user);
        return profileMapper.DtoFromEntity(profile);
    }

    public void removeProfileFromUser(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new MyResourceNotFoundException("Profile not found with id:" + profileId));
        User user = userRepository.findByProfiles(profile)
                .orElseThrow(() -> new MyResourceNotFoundException("User not found with Profile:" + profile));
        user.removeProfile(profile);
        profileRepository.delete(profile);
    }

    public ProfileResponse updateProfile(ProfileRequest profileRequest, Long profileId) {
        Groupe groupe = null;
        if (profileRequest.getGroupId() != null) {
            groupe = groupeRepository.findById(profileRequest.getGroupId())
                    .orElseThrow(() -> new MyResourceNotFoundException("Groupe not found with id:" + profileRequest.getGroupId()));
        }
        User user = userRepository.findById(profileRequest.getUserId())
                .orElseThrow(() -> new MyResourceNotFoundException("User not found with id:" + profileRequest.getUserId()));
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new MyResourceNotFoundException("Profile not found with id:" + profileId));
        profile.setLibelle(profileRequest.getLibelle());
        profile.setGroupe(groupe);
        profile.setUser(user);
        profileRepository.save(profile);
        return profileMapper.DtoFromEntity(profile);
    }

    public ProfileResponse activeProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new MyResourceNotFoundException("Profile not found with id:" + profileId));
        if (profile.getActif() == false) {
            profile.setActif(true);
            profileRepository.save(profile);
            return profileMapper.DtoFromEntity(profile);
        } else
            throw new RessourceAlreadyEnabledException("Profile Already Active");
    }

    public ProfileResponse desactiveProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new MyResourceNotFoundException("Profile not found with id:" + profileId));
        if (profile.getActif() == true) {
            profile.setActif(false);
            profileRepository.save(profile);
            return profileMapper.DtoFromEntity(profile);
        } else
            throw new RessourceAlreadyDisabledException("Profile Already desactive");
    }

    public UserResponse changeActiveProfile(Long userId, Long profileId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MyResourceNotFoundException("User not found with id:" + userId));
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new MyResourceNotFoundException("Profile not found with id:" + profileId));
        if (user.getActifProfile() != profile) {
            user.setActifProfile(profile);
            userRepository.save(user);
            return userMapper.DtoFromEntity(user);
        } else
            throw new ProfileAlreadyActiveException("Profile Already Active");
    }

    public ProfileResponse addRoleToProfile(Long profileId, Long roleId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new MyResourceNotFoundException("Profile not found with id:" + profileId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new MyResourceNotFoundException("Role not found with id" + roleId));
        profile.addRole(role);
        profileRepository.save(profile);
        return profileMapper.DtoFromEntity(profile);
    }

    public ProfileResponse removeRoleFromProfile(Long profileId, Long roleId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new MyResourceNotFoundException("Profile not found with id:" + profileId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new MyResourceNotFoundException("Role not found with id" + roleId));
        profile.removeRole(role);
        profileRepository.save(profile);
        return profileMapper.DtoFromEntity(profile);
    }

    public ProfileResponse addModuleToProfile(Long profileId, Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new MyResourceNotFoundException("Module not found with id: " + moduleId));
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new MyResourceNotFoundException("Profile not found with id:" + profileId));
        profile.addModule(module);
        profileRepository.save(profile);
        return profileMapper.DtoFromEntity(profile);
    }

    public ProfileResponse removeModuleFromProfile(Long profileId, Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new MyResourceNotFoundException("Module not found with id: " + moduleId));
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new MyResourceNotFoundException("Profile not found with id:" + profileId));
        profile.removeModule(module);
        profileRepository.save(profile);
        return profileMapper.DtoFromEntity(profile);
    }
    public ProfileResponse addProfileToGroupe(Long profileId, Long groupeId) {
        Groupe groupe=groupeRepository.findById(groupeId)
                .orElseThrow(()->new MyResourceNotFoundException("Groupe not found with id: "+groupeId));
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new MyResourceNotFoundException("Profile not found with id:" + profileId));
        profile.setGroupe(groupe);
        profileRepository.save(profile);
        return profileMapper.DtoFromEntity(profile);
    }

    public ProfileResponse removeProfileFromGroupe(Long profileId, Long groupeId) {
        Groupe groupe=null;
        if (groupeId!=-1){
            groupe=groupeRepository.findById(groupeId)
                    .orElseThrow(()->new MyResourceNotFoundException("Groupe not found with id: "+groupeId));
        }
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new MyResourceNotFoundException("Profile not found with id:" + profileId));
        profile.setGroupe(groupe);
        profileRepository.save(profile);
        return profileMapper.DtoFromEntity(profile);
    }
    public List<ProfileForModuleResponse> getProfileByModuleAndUser(Long userId,Long moduleId){
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new MyResourceNotFoundException("Module not found with id: " + moduleId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MyResourceNotFoundException("User not found with id:" + userId));
        List<Profile> profiles=profileRepository.findByModulesAndUser(module,user);
        return profiles.stream().map(profileMapper::ProfileForModuleDtoFromEntity).collect(Collectors.toList());
    }
    public ProfileResponse addAuthorityToProfile(Long profileId,Long authorityId){
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new MyResourceNotFoundException("Profile not found with id:" + profileId));
        Authority authority=authorityRepository.findById(authorityId)
                .orElseThrow(()->new MyResourceNotFoundException("Authority not found with id: "+authorityId));
        ProfileAuthority profileAuthority1=ProfileAuthority.builder()
                .authority(authority)
                .profile(profile)
                .granted(true)
                .build();
        profile.addAuthority(profileAuthority1);
        profileAuthorityRepository.save(profileAuthority1);
        profileRepository.save(profile);
        return profileMapper.DtoFromEntity(profile);
    }
    public void removeAuthorityFromProfile(Long profileAuthorityId){
        ProfileAuthority profileAuthority=profileAuthorityRepository.findById(profileAuthorityId)
                .orElseThrow(()->new MyResourceNotFoundException("Profile Authority not found with id: "+profileAuthorityId));
        Profile profile=profileRepository.findByProfileAuthorities(profileAuthority);
        profile.removeAuthority(profileAuthority);
        profileAuthorityRepository.delete(profileAuthority);
    }
    public ProfileAuthorityResponse revokeAuthorityFromProfile(Long profileAuthorityId){
        ProfileAuthority profileAuthority=profileAuthorityRepository.findById(profileAuthorityId)
                .orElseThrow(()->new MyResourceNotFoundException("Profile Authority not found with id: "+profileAuthorityId));
        profileAuthority.setGranted(false);
        profileAuthorityRepository.save(profileAuthority);
        return profileMapper.profileAuthorityToResponse(profileAuthority);
    }

    public ProfileAuthorityResponse granteAuthorityFromProfile(Long profileAuthorityId) {
        ProfileAuthority profileAuthority=profileAuthorityRepository.findById(profileAuthorityId)
                .orElseThrow(()->new MyResourceNotFoundException("Profile Authority not found with id: "+profileAuthorityId));
        profileAuthority.setGranted(true);
        profileAuthorityRepository.save(profileAuthority);
        return profileMapper.profileAuthorityToResponse(profileAuthority);
    }
    public Set<String> getUserUuidforGroupe(Long id){
        Groupe groupe=groupeRepository.findById(id)
                .orElseThrow(()->new MyResourceNotFoundException("Groupe not found with id: "+id));
        List<Profile> profiles=profileRepository.findAllByGroupe(groupe);
        Set<User> users = profiles.stream()
                .map(Profile::getUser)
                .collect(Collectors.toSet());
        Set<String> uuids=users.stream()
                .map(User::getUuid)
                .collect(Collectors.toSet());
        return uuids;
    }
}