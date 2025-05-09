package com.example.recipeAPI.services;

import com.example.recipeAPI.exceptions.NoSuchRecipeException;
import com.example.recipeAPI.exceptions.NoSuchReviewException;
import com.example.recipeAPI.models.Recipe;
import com.example.recipeAPI.models.Review;
import com.example.recipeAPI.repositories.RecipeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    @Autowired
    RecipeRepo recipeRepo;

    @Autowired
    ReviewService reviewService;

    @Transactional
    public Recipe createNewRecipe(Recipe recipe)
            throws IllegalStateException {
        recipe.validate();
        recipe = recipeRepo.save(recipe);
        recipe.generateLocationURI();
        return recipe;
    }

    public Recipe getRecipeById(Long id) throws NoSuchRecipeException {
        Optional<Recipe> recipeOptional = recipeRepo.findById(id);

        if (recipeOptional.isEmpty()) {
            throw new NoSuchRecipeException(
                    "No recipe with ID " + id + " could be found.");
        }

        Recipe recipe = recipeOptional.get();
        recipe.generateLocationURI();
        return recipe;
    }

    public List<Recipe> getRecipesByName(String name)
            throws NoSuchRecipeException {
        List<Recipe> matchingRecipes =
                recipeRepo.findByNameContainingIgnoreCase(name);

        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException(
                    "No recipes could be found with that name.");
        }

        return matchingRecipes;
    }

    public List<Recipe> getRecipesByUsername(String username)
            throws NoSuchRecipeException {
        List<Recipe> matchingRecipes =
                recipeRepo.findBySubmittedByContainingIgnoreCase(username);

        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException(
                    "No recipes by that username could be found.");
        }

        return matchingRecipes;
    }


    public List<Recipe> getAllRecipes() throws NoSuchRecipeException {
        List<Recipe> recipes = recipeRepo.findAll();

        if (recipes.isEmpty()) {
            throw new NoSuchRecipeException(
                    "There are no recipes yet :( feel free to add one.");
        }
        return recipes;
    }

    @Transactional
    public Recipe deleteRecipeById(Long id) throws NoSuchRecipeException {
        try {
            Recipe recipe = getRecipeById(id);
            recipeRepo.deleteById(id);
            return recipe;
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException(
                    e.getMessage() + " Could not delete.");
        }
    }

    @Transactional
    public Recipe updateRecipe(Recipe recipe, boolean forceIdCheck)
            throws NoSuchRecipeException {
        try {
            if (forceIdCheck) {
                getRecipeById(recipe.getId());
            }
            recipe.validate();
            Recipe savedRecipe = recipeRepo.save(recipe);
            savedRecipe.generateLocationURI();
            return savedRecipe;
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException(
                    "The recipe you passed in did not have an ID found " +
                            "in the database. Double check that it is correct. " +
                            "Or maybe you meant to POST a recipe not PATCH one.");
        }
    }

    public long calculateAverageRating(Recipe recipe) {

        long meanAverage = 0;

        try {
            int ratingsTemp = 0;
            List<Review> reviewsForRecipe = reviewService.getReviewByRecipeId(recipe.getId());
            for (int i = 0; i < reviewsForRecipe.size(); i++) {
                ratingsTemp = ratingsTemp + reviewsForRecipe.get(i).getRating();
            }
            meanAverage = (ratingsTemp / reviewsForRecipe.size());
        } catch (NoSuchRecipeException | NoSuchReviewException e) {
            throw new RuntimeException(e);
        }

        return meanAverage;

    }

}
