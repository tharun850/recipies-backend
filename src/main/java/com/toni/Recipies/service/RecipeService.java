package com.toni.Recipies.service;

import com.toni.Recipies.entity.Recipe;
import com.toni.Recipies.entity.RecipeResponse;
import com.toni.Recipies.exception.CustomException;
import com.toni.Recipies.exception.RecipeNotFoundException;
import com.toni.Recipies.repository.RecipeRepository;
import com.toni.Recipies.client.RecipeClient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.List;
import java.util.Objects;

@Service
public class RecipeService {
    private static final Logger logger = LogManager.getLogger(RecipeService.class);

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RecipeClient recipeClient;

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    @Retryable(
            retryFor = { CustomException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    @Cacheable("responseRecipes")
    @CircuitBreaker(name = "recipeService", fallbackMethod = "loadRecipesFallback")
    public void loadRecipes() {
        logger.info("Loading recipes from client");
        RecipeResponse recipeResponse = getRecipeFromClient();
        System.out.println(recipeResponse.getRecipes().get(0).getMealType());
        if (recipeResponse == null || recipeResponse.getRecipes().isEmpty()) {
            throw new CustomException("No recipes found");
        }
        List<Recipe> newRecipes = recipeResponse.getRecipes();
        List<Recipe> existingRecipes = recipeRepository.findAll();

        if (!Objects.equals(newRecipes, existingRecipes)) {
            logger.info("Data difference detected, clearing database");
            recipeRepository.deleteAllInBatch();
            recipeRepository.saveAllAndFlush(newRecipes);
            logger.info("Database cleared and new recipes loaded successfully");
        } else {
            logger.info("No data difference detected, skipping database update");
        }
    }

    @Cacheable("recipes")
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public RecipeResponse getRecipeFromClient() {
        return recipeClient.getRecipes();
    }

    public List<Recipe> searchRecipes(String query) {
        SearchSession searchSession = Search.session(entityManager);
        List<Recipe> recipes = searchSession.search(Recipe.class)
                .where(f -> f.match()
                        .fields("name", "cuisine")
                        .matching(query))
                .fetchHits(20);
        if (recipes.isEmpty()) {
            throw new CustomException("No recipes found for the query: " + query);
        }
        return recipes;
    }

    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id).orElseThrow(() -> new RecipeNotFoundException("Recipe not found with id: " + id));

    }

    public void loadRecipesFallback(Throwable t) {
        logger.error("Fallback method called due to: " + t.getMessage());
    }
}