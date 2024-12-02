package com.toni.Recipies.client;

import com.toni.Recipies.entity.RecipeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RecipeClientTest {

    @Mock
    private RecipeClient recipeClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetRecipes() {
        RecipeResponse mockResponse = new RecipeResponse();

        when(recipeClient.getRecipes()).thenReturn(mockResponse);

        RecipeResponse response = recipeClient.getRecipes();

        assertNotNull(response);
    }
}