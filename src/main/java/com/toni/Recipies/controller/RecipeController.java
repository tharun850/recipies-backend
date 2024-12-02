package com.toni.Recipies.controller;

import com.toni.Recipies.entity.Recipe;
import com.toni.Recipies.service.RecipeService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@Validated
@CrossOrigin(origins = "*")
public class RecipeController {
    @Autowired
    private RecipeService recipeService;

    @PostMapping("/load")
    @ResponseStatus(HttpStatus.CREATED)
    public void loadRecipes() {
        recipeService.loadRecipes();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Recipe>> searchRecipes(
            @RequestParam @NotNull(message = "{query.notnull}") @Size(min = 3, message = "{query.size}") String query) {
        List<Recipe> recipes = recipeService.searchRecipes(query);
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Recipe> getRecipeById(
            @PathVariable @NotNull(message = "{id.notnull}") @Min(value = 1, message = "{id.min}") Long id) {
        Recipe recipe = recipeService.getRecipeById(id);
        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }
}