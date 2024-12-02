package com.toni.Recipies.repository;

import com.toni.Recipies.entity.Recipe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@ActiveProfiles("test")
public class RecipeRepositoryTest {

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    public void testSaveRecipe() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Test Recipe");
        Recipe savedRecipe = recipeRepository.save(recipe);

        assertNotNull(savedRecipe.getId());
        assertEquals("Test Recipe", savedRecipe.getName());
    }

    @Test
    public void testFindById() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Test Recipe");
        recipe = recipeRepository.save(recipe);

        Optional<Recipe> foundRecipe = recipeRepository.findById(recipe.getId());

        assertTrue(foundRecipe.isPresent());
        assertEquals("Test Recipe", foundRecipe.get().getName());
    }

    @Test
    public void testFindAll() {
        Recipe recipe1 = new Recipe();
        recipe1.setId(1L);
        recipe1.setName("Test Recipe 1");
        recipeRepository.save(recipe1);

        Recipe recipe2 = new Recipe();
        recipe2.setId(2L);
        recipe2.setName("Test Recipe 2");
        recipeRepository.save(recipe2);

        List<Recipe> recipes = recipeRepository.findAll();

        assertEquals(2, recipes.size());
    }

    @Test
    public void testDeleteById() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Test Recipe");
        recipe = recipeRepository.save(recipe);

        recipeRepository.deleteById(recipe.getId());

        Optional<Recipe> deletedRecipe = recipeRepository.findById(recipe.getId());

        assertFalse(deletedRecipe.isPresent());
    }

    @Test
    public void testUpdateRecipe() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Test Recipe");
        recipe = recipeRepository.save(recipe);

        recipe.setName("Updated Recipe");
        Recipe updatedRecipe = recipeRepository.save(recipe);

        assertEquals("Updated Recipe", updatedRecipe.getName());
    }
}