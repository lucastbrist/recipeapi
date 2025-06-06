package com.example.recipeAPI.controllers;

import com.example.recipeAPI.exceptions.NoSuchRecipeException;
import com.example.recipeAPI.exceptions.NoSuchReviewException;
import com.example.recipeAPI.models.Recipe;
import com.example.recipeAPI.models.Review;
import com.example.recipeAPI.services.RecipeService;
import com.example.recipeAPI.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    ReviewService reviewService;

    @Autowired
    RecipeService recipeService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable("id") Long id) {
        try {
            Review retrievedReview = reviewService.getReviewById(id);
            return ResponseEntity.ok(retrievedReview);
        } catch (IllegalStateException | NoSuchReviewException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<?> getReviewByRecipeId(
            @PathVariable("recipeId") Long recipeId) {
        try {
            List<Review> reviews =
                    reviewService.getReviewByRecipeId(recipeId);
            return ResponseEntity.ok(reviews);
        } catch (NoSuchRecipeException | NoSuchReviewException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getReviewByUsername(
            @PathVariable("username") String username) {
        try {
            List<Review> reviews =
                    reviewService.getReviewByUsername(username);
            return ResponseEntity.ok(reviews);
        } catch (NoSuchReviewException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{recipeId}")
    public ResponseEntity<?> postNewReview(
            @RequestBody Review review,
            @PathVariable("recipeId") Long recipeId) {
        try {
            Recipe insertedRecipe =
                    reviewService.postNewReview(review, recipeId);
            if (Objects.equals(insertedRecipe.getSubmittedBy(), review.getUsername())) {
                String response = "Reviewing your own recipes is fine for a retrospective, but keep that to yourself!";
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.created(
                    insertedRecipe.getLocationURI()).body(insertedRecipe);
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReviewById(
            @PathVariable("id") Long id) {
        try {
            Review review = reviewService.deleteReviewById(id);
            return ResponseEntity.ok(review);
        } catch (NoSuchReviewException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping
    public ResponseEntity<?> updateReviewById(
            @RequestBody Review reviewToUpdate) {
        try {
            Review review =
                    reviewService.updateReviewById(reviewToUpdate);
            return ResponseEntity.ok(review);
        } catch (NoSuchReviewException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search/average/{rating}")
    public ResponseEntity<?> getRecipesByMinimumAverageRating(
            @PathVariable("rating") Long rating) {

        ArrayList<Recipe> matchingRecipes = new ArrayList<>();

        List<Recipe> allRecipes = null;
        try {
            allRecipes = recipeService.getAllRecipes();
        } catch (NoSuchRecipeException e) {
            throw new RuntimeException(e);
        }
        for (Recipe recipe : allRecipes) {
            reviewService.calculateAverageRating(recipe);
            if (recipe.getAverageRating() >= rating) {
                matchingRecipes.add(recipe);
            }
        }
        return ResponseEntity.ok(matchingRecipes);
    }

}
