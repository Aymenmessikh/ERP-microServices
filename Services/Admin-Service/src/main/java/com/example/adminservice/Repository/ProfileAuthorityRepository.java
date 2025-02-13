package com.example.adminservice.Repository;

import com.example.adminservice.Entity.ProfileAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileAuthorityRepository extends JpaRepository<ProfileAuthority,Long> {
}
