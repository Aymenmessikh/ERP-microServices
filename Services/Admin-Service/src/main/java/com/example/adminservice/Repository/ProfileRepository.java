package com.example.adminservice.Repository;

import com.example.adminservice.Entity.*;
import com.example.adminservice.Entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile , Long> {
    List<Profile> findByModulesAndUser(Module module, User user);

    Profile findByProfileAuthorities(ProfileAuthority profileAuthority);

    List<Profile> findAllByGroupe(Groupe groupe);
}
