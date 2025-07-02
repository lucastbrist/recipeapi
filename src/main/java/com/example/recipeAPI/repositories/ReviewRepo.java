package com.example.recipeAPI.repositories;

import com.example.recipeAPI.models.CustomUserDetails;
import com.example.recipeAPI.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepo extends JpaRepository<Review, Long> {

    List<Review> findByUser(CustomUserDetails user);

}
