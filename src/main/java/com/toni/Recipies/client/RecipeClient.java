package com.toni.Recipies.client;

import com.toni.Recipies.entity.RecipeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "recipeClient", url = "https://dummyjson.com")
public interface RecipeClient {
    @GetMapping("/recipes")
    public RecipeResponse getRecipes();
}