package com.example.adminservice.Repository;

import com.example.adminservice.Entity.Profile;
import com.example.adminservice.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User ,Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByProfiles(Profile profile);

    Optional<User> findByUserName(String userName);
}
