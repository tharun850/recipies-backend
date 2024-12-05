package com.toni.Recipies.service;

import com.toni.Recipies.client.RecipeClient;
import com.toni.Recipies.entity.Recipe;
import com.toni.Recipies.entity.RecipeResponse;
import com.toni.Recipies.repository.RecipeRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.persistence.EntityManager;
import org.hibernate.search.engine.search.aggregation.dsl.SearchAggregationFactory;
import org.hibernate.search.engine.search.query.dsl.SearchQueryOptionsStep;
import org.hibernate.search.engine.search.query.dsl.SearchQuerySelectStep;
import org.hibernate.search.engine.search.sort.dsl.SearchSortFactory;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.search.loading.dsl.SearchLoadingOptionsStep;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private SearchSession searchSession;

    @Mock
    private SearchQuerySelectStep<SearchQueryOptionsStepLocalAbstractImpl<?, Recipe, SearchLoadingOptionsStep, ?, ?>, ?, Recipe, SearchLoadingOptionsStep, ?, ?> searchQuerySelectStep;

    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private RecipeClient recipeClient;

    private CircuitBreaker circuitBreaker;


    @Mock
    private SearchQueryOptionsStepLocalAbstractImpl<?, Recipe, SearchLoadingOptionsStep, ?, ?> searchQueryOptionsStepLocalAbstract;

    abstract static class SearchQueryOptionsStepLocalAbstractImpl<A extends SearchQueryOptionsStep<?, B, C, D, E>, B, C, D extends SearchSortFactory,E extends SearchAggregationFactory> implements SearchQueryOptionsStep<A, B, C, D, E> { }



    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .slidingWindowSize(2)
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        circuitBreaker = registry.circuitBreaker("recipeClient");

    }


    @Test
    public void testLoadRecipes() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Test Recipe");

        RecipeResponse recipeResponse = new RecipeResponse();
        recipeResponse.setRecipes(Collections.singletonList(recipe));

        when(recipeClient.getRecipes()).thenReturn(recipeResponse);

        recipeService.loadRecipes();

        verify(recipeRepository, times(1)).saveAllAndFlush(Collections.singletonList(recipe));
    }


    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void testSearchRecipes() {

        MockedStatic<Search> utilities = mockStatic(Search.class);
        when(Search.session(entityManager)).thenReturn(searchSession);

        when(searchSession.search(Recipe.class)).thenReturn((SearchQuerySelectStep)searchQuerySelectStep);
        when(searchQuerySelectStep.where(any(Function.class))).thenReturn(searchQueryOptionsStepLocalAbstract);

        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Test Recipe");

        when(searchQueryOptionsStepLocalAbstract.fetchHits(20)).thenReturn(Collections.singletonList(recipe));

        List<Recipe> recipes = recipeService.searchRecipes("test");

        assertEquals(1, recipes.size());
        assertEquals("Test Recipe", recipes.get(0).getName());
        utilities.close();
    }

    @Test
    public void testGetRecipeById() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Test Recipe");

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        Recipe foundRecipe = recipeService.getRecipeById(1L);

        assertEquals("Test Recipe", foundRecipe.getName());
    }

    @Test
    public void testGetRecipesWithCircuitBreaker() {
        RecipeResponse mockResponse = new RecipeResponse();

        when(recipeClient.getRecipes()).thenReturn(mockResponse);

        RecipeResponse response = circuitBreaker.executeSupplier(() -> recipeClient.getRecipes());

        assertNotNull(response);
    }

    @Test
    public void testGetRecipesWithResilience() {
        RecipeResponse mockResponse = new RecipeResponse();

        when(recipeClient.getRecipes()).thenReturn(mockResponse);

        RecipeResponse response = circuitBreaker.executeSupplier(() -> recipeClient.getRecipes());

        assertNotNull(response);

        when(recipeClient.getRecipes()).thenThrow(new RuntimeException("Service not available"));

        assertThrows(RuntimeException.class, () -> circuitBreaker.executeSupplier(() -> recipeClient.getRecipes()));

        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
    }

    @Test
    public void testGetAllRecipes() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Test Recipe");

        when(recipeRepository.findAll()).thenReturn(Collections.singletonList(recipe));

        List<Recipe> recipes = recipeService.getAllRecipes();

        assertEquals(1, recipes.size());
        assertEquals("Test Recipe", recipes.get(0).getName());
    }

    @AfterEach
    public void tearDown()  {
        circuitBreaker.reset();
        try {
            MockitoAnnotations.openMocks(this).close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}