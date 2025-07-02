package com.example.recipeAPI.repositories;

import com.example.recipeAPI.models.CustomUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo
        extends JpaRepository<CustomUserDetails, Long> {

    CustomUserDetails findByUsername(String username);
}
