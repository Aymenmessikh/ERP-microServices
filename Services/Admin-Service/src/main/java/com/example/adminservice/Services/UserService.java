package com.example.adminservice.Services;

import com.example.adminservice.Dto.Notification.MailMessage;
import com.example.adminservice.Dto.Notification.SmsMessage;
import com.example.adminservice.Dto.User.UserRequest;
import com.example.adminservice.Dto.User.UserResponse;
import com.example.adminservice.Entity.Profile;
import com.example.adminservice.Entity.ProfileAuthority;
import com.example.adminservice.Entity.User;
import com.example.adminservice.Events.Kafka.KafkaEvents;
import com.example.adminservice.Events.Modele.UserEvent;
import com.example.adminservice.Exeptions.MyResourceNotFoundException;
import com.example.adminservice.Mapper.User.UserMapper;
import com.example.adminservice.Repository.ProfileRepository;
import com.example.adminservice.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final KeycloakService keycloakService;
    private final UserMapper userMapper;
    private final KafkaEvents kafkaEvents;
//    public UserResponse createUser(UserRequest userRequest, Long profileTypeId) {
//        String uuid = keycloakService.createUser(userRequest);
//        if (uuid == null) {
//            throw new RuntimeException("uuid is null");
//        }
//
//
//        Profile defaultProfile;
//        if (profileTypeId == -1) {
//            defaultProfile = Profile.builder()
//                    .libelle("default Profile")
//                    .actif(true)
//                    .build();
//        } else {
//            Profile profile = profileRepository.findById(profileTypeId)
//                    .orElseThrow(() -> new MyResourceNotFoundException("Profile not found with id:" + profileTypeId));
//
//            defaultProfile = Profile.builder()
//                    .libelle(profile.getLibelle())
//                    .groupe(profile.getGroupe())
//                    .roles(new ArrayList<>(profile.getRoles()))
//                    .modules(new ArrayList<>(profile.getModules()))
//                    .actif(true)
//                    .build();
//
//            // Save the profile first to get its ID
//            Profile savedProfile = profileRepository.save(defaultProfile);
//
//            List<ProfileAuthority> newProfileAuthorities = new ArrayList<>();
//            for (ProfileAuthority authority : profile.getProfileAuthorities()) {
//                ProfileAuthority newAuthority = ProfileAuthority.builder()
//                        .authority(authority.getAuthority())
//                        .granted(authority.getGranted())
//                        .profile(savedProfile) // Now reference the saved profile
//                        .build();
//                newProfileAuthorities.add(newAuthority);
//            }
//
//            // Set the saved profileAuthorities to the savedProfile
//            savedProfile.setProfileAuthorities(newProfileAuthorities);
//
//            // Update the saved profile with the authorities
//            profileRepository.save(savedProfile);
//        }
//
//        User user = userMapper.EntityFromDto(userRequest);
//        user.setUuid(uuid);
//        defaultProfile.setUser(user);
//        user.setActifProfile(defaultProfile);
//        user.addProfile(defaultProfile);
//
//        // Save the user and the defaultProfile
//        User createdUser = userRepository.save(user);
//
//        // Trigger the user event for notification service
////        UserEvent userEvent = UserEvent.UserEventFromEntity(createdUser);
////        kafkaEvents.sendUserToNotificationService(userEvent);
//
//        return userMapper.DtoFromEntity(createdUser);
//    }

    public UserResponse createUser(UserRequest userRequest, Long profileTypeId) {
        // Créer l'utilisateur dans Keycloak et vérifier la réponse
        String uuid = keycloakService.createUser(userRequest);
        if (uuid == null) {
            throw new IllegalStateException("UUID is null. User creation in Keycloak failed.");
        }

        // Mapper l'utilisateur et l'enregistrer
        User user = userMapper.EntityFromDto(userRequest);
        user.setUuid(uuid);
        userRepository.save(user);

        // Déterminer le profil par défaut
        Profile defaultProfile = (profileTypeId == -1) ? createDefaultProfile(user) : cloneExistingProfile(user, profileTypeId);

        // Assigner le profil par défaut à l'utilisateur et sauvegarder
        user.setActifProfile(defaultProfile);
        user.addProfile(defaultProfile);
        userRepository.save(user);

        // triggerUserEvent(createdUser);

        return userMapper.DtoFromEntity(user);
    }

    // Crée un profil par défaut
    private Profile createDefaultProfile(User user) {
        Profile profile = Profile.builder()
                .libelle("Default Profile")
                .actif(true)
                .user(user)
                .build();
        return profileRepository.save(profile);
    }

    // Clone un profil existant
    private Profile cloneExistingProfile(User user, Long profileTypeId) {
        Profile profile = profileRepository.findById(profileTypeId)
                .orElseThrow(() -> new MyResourceNotFoundException("Profile not found with id: " + profileTypeId));

        Profile clonedProfile = Profile.builder()
                .libelle(profile.getLibelle())
                .groupe(profile.getGroupe())
                .roles(new ArrayList<>(profile.getRoles()))
                .modules(new ArrayList<>(profile.getModules()))
                .actif(true)
                .user(user)
                .build();

        Profile savedProfile = profileRepository.save(clonedProfile);

        // Cloner les autorités associées
        List<ProfileAuthority> newAuthorities = profile.getProfileAuthorities().stream()
                .map(auth -> ProfileAuthority.builder()
                        .authority(auth.getAuthority())
                        .granted(auth.getGranted())
                        .profile(savedProfile)
                        .build())
                .collect(Collectors.toList());

        savedProfile.setProfileAuthorities(newAuthorities);
        return profileRepository.save(savedProfile);
    }

// déclencher un événement Kafka
// private void triggerUserEvent(User user) {
//     UserEvent userEvent = UserEvent.UserEventFromEntity(user);
//     kafkaEvents.sendUserToNotificationService(userEvent);
// }



    public List<UserResponse> getAllUsers(){
        List<UserResponse> userResponses=userRepository.findAll().stream()
                .map(userMapper::DtoFromAllEntity).collect(Collectors.toList());
        return userResponses;
    }
    public UserResponse getUserById(Long id){
        User user=userRepository.findById(id)
                .orElseThrow(()->new MyResourceNotFoundException("User not found with id:"+id));
        return userMapper.DtoFromEntity(user);
    }
    public UserResponse getUserByEmail(String email){
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new MyResourceNotFoundException("User not found with Email:"+email));
        return userMapper.DtoFromEntity(user);
    }
    public User getUserByUserName(String userName){
        User user=userRepository.findByUserName(userName)
                .orElseThrow(()->new MyResourceNotFoundException("User not found with userName:"+userName));
        return user;
    }
    public UserResponse updateUser(UserRequest userRequest,Long id){
        User user=userRepository.findById(id)
                .orElseThrow(()->new MyResourceNotFoundException("User not found with id:"+id));
        user.setUserName(user.getUserName());
        user.setEmail(user.getEmail());
        user.setFirstName(user.getFirstName());
        user.setLastName(user.getLastName());
        user.setPhoneNumber(user.getPhoneNumber());
        userRepository.save(user);
        keycloakService.updateUser(user.getUuid(),userRequest);
        return userMapper.DtoFromEntity(user);
    }
    public UserResponse enableDisabeleUser(Long id,Boolean enable){
        User user=userRepository.findById(id)
                .orElseThrow(()->new MyResourceNotFoundException("User not found with id:"+id));
        keycloakService.enableDisableUser(user.getUuid(),enable);
        user.setActif(enable);
        userRepository.save(user);
        return userMapper.DtoFromEntity(user);
    }
    public void deleteUser(Long id){
        User user=userRepository.findById(id)
                .orElseThrow(()->new MyResourceNotFoundException("User not found with id:"+id));
        keycloakService.deleteUser(user.getUuid());
        userRepository.delete(user);
    }
}
