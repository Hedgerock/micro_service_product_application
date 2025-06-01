package com.hedgerock.manager.repository;

import com.hedgerock.manager.entities.ApplicationUser;

import java.util.Optional;

public interface ApplicationUserRepository {
    Optional<ApplicationUser> findByUsername(String username);
}
