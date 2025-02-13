package com.example.adminservice.Controller;

import com.example.adminservice.Dto.Profile.ProfileAuthorityResponse;
import com.example.adminservice.Dto.Profile.ProfileForModuleResponse;
import com.example.adminservice.Dto.Profile.ProfileRequest;
import com.example.adminservice.Dto.Profile.ProfileResponse;
import com.example.adminservice.Dto.User.UserResponse;
import com.example.adminservice.Services.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin/profile")
public class ProfileController {

    private final ProfileService profileService;

    @PreAuthorize("hasAuthority('CREATE_PROFILE')")
    @PostMapping("{id}")
    public ResponseEntity<ProfileResponse> addProfileToUser(@Valid @RequestBody ProfileRequest profileRequest, @PathVariable Long id) {
        ProfileResponse profileResponse = profileService.addProfileToUser(profileRequest, id);
        return new ResponseEntity<>(profileResponse, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('DELETE_PROFILE')")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> removeProfileFromUser(@PathVariable Long id) {
        profileService.removeProfileFromUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('UPDATE_PROFILE')")
    @PutMapping("{id}")
    public ResponseEntity<ProfileResponse> updateProfile(@Valid @RequestBody ProfileRequest profileRequest, @PathVariable Long id) {
        ProfileResponse profileResponse = profileService.updateProfile(profileRequest, id);
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ENABLE_PROFILE')")
    @PutMapping("activate/{id}")
    public ResponseEntity<ProfileResponse> activateProfile(@PathVariable Long id) {
        ProfileResponse profileResponse = profileService.activeProfile(id);
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('DISABLE_PROFILE')")
    @PutMapping("deactivate/{id}")
    public ResponseEntity<ProfileResponse> deactivateProfile(@PathVariable Long id) {
        ProfileResponse profileResponse = profileService.desactiveProfile(id);
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('CHANGE_ACTIVE_PROFILE')")
    @PutMapping("changeActiveProfile/{userId}/{profileId}")
    public ResponseEntity<UserResponse> changeActiveProfile(@PathVariable Long userId, @PathVariable Long profileId) {
        UserResponse userResponse = profileService.changeActiveProfile(userId, profileId);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADD_ROLE_TO_PROFILE')")
    @PutMapping("addRole/{profileId}/{roleId}")
    public ResponseEntity<ProfileResponse> addRoleToProfile(@PathVariable Long profileId, @PathVariable Long roleId) {
        ProfileResponse profileResponse = profileService.addRoleToProfile(profileId, roleId);
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('REMOVE_ROLE_FROM_PROFILE')")
    @PutMapping("removeRole/{profileId}/{roleId}")
    public ResponseEntity<ProfileResponse> removeRoleFromProfile(@PathVariable Long profileId, @PathVariable Long roleId) {
        ProfileResponse profileResponse = profileService.removeRoleFromProfile(profileId, roleId);
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADD_PROFILE_TO_GROUP')")
    @PutMapping("addGroup/{profileId}/{groupId}")
    public ResponseEntity<ProfileResponse> addProfileToGroup(@PathVariable Long profileId, @PathVariable Long groupId) {
        ProfileResponse profileResponse = profileService.addProfileToGroupe(profileId, groupId);
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('REMOVE_PROFILE_FROM_GROUP')")
    @PutMapping("removeGroup/{profileId}/{groupId}")
    public ResponseEntity<ProfileResponse> removeProfileFromGroup(@PathVariable Long profileId, @PathVariable Long groupId) {
        ProfileResponse profileResponse = profileService.removeProfileFromGroupe(profileId, groupId);
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADD_MODULE_TO_PROFILE')")
    @PutMapping("addModule/{profileId}/{moduleId}")
    public ResponseEntity<ProfileResponse> addModuleToProfile(@PathVariable Long profileId, @PathVariable Long moduleId) {
        ProfileResponse profileResponse = profileService.addModuleToProfile(profileId, moduleId);
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('REMOVE_MODULE_FROM_PROFILE')")
    @PutMapping("removeModule/{profileId}/{moduleId}")
    public ResponseEntity<ProfileResponse> removeModuleFromProfile(@PathVariable Long profileId, @PathVariable Long moduleId) {
        ProfileResponse profileResponse = profileService.removeModuleFromProfile(profileId, moduleId);
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('READ_PROFILES_BY_MODULE')")
    @GetMapping("getProfiles/{userId}/{moduleId}")
    public ResponseEntity<List<ProfileForModuleResponse>> getProfileByModuleAndUser(@PathVariable Long userId, @PathVariable Long moduleId) {
        List<ProfileForModuleResponse> profiles = profileService.getProfileByModuleAndUser(userId, moduleId);
        return new ResponseEntity<>(profiles, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADD_AUTHORITY_TO_PROFILE')")
    @PutMapping("addAuthority/{profileId}/{authorityId}")
    public ResponseEntity<ProfileResponse> addAuthorityToProfile(@PathVariable Long profileId, @PathVariable Long authorityId) {
        ProfileResponse profileResponse = profileService.addAuthorityToProfile(profileId, authorityId);
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('REMOVE_AUTHORITY_FROM_PROFILE')")
    @DeleteMapping("removeAuthority/{id}")
    public ResponseEntity<Void> removeAuthorityFromProfile(@PathVariable Long id) {
        profileService.removeAuthorityFromProfile(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('REVOKE_AUTHORITY')")
    @PutMapping("revokeAuthority/{id}")
    public ResponseEntity<ProfileAuthorityResponse> revokeAuthorityFromProfile(@PathVariable Long id) {
        ProfileAuthorityResponse profile = profileService.revokeAuthorityFromProfile(id);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('GRANT_AUTHORITY')")
    @PutMapping("grantAuthority/{id}")
    public ResponseEntity<ProfileAuthorityResponse> grantAuthorityToProfile(@PathVariable Long id) {
        ProfileAuthorityResponse profile = profileService.granteAuthorityFromProfile(id);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('READ_USER_UUID_FOR_GROUP')")
    @GetMapping("userUuid")
    public ResponseEntity<Set<String>> getUserUuidForGroup(@RequestParam Long id) {
        Set<String> uuids = profileService.getUserUuidforGroupe(id);
        return new ResponseEntity<>(uuids, HttpStatus.OK);
    }
}
