package com.toni.Recipies;

import com.toni.Recipies.controller.RecipeControllerTest;
import com.toni.Recipies.repository.RecipeRepositoryTest;
import com.toni.Recipies.service.RecipeServiceTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

class RecipiesApplicationTests {

    @Test
    void contextLoads() {
    }

}