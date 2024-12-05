package com.toni.Recipies;

import com.toni.Recipies.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableFeignClients
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@EnableCaching
public class RecipesApplication {

	private final ApplicationContext applicationContext;

	private RecipeService recipeService;

	@Value("${app.loadRecipes}")
	private boolean loadRecipes;

	public RecipesApplication(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public static void main(String[] args) {
		SpringApplication.run(RecipesApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationEvent() {
		System.out.println("Active profile: " + loadRecipes);
		if (loadRecipes) {
			RecipeService recipeService = applicationContext.getBean(RecipeService.class);
			recipeService.loadRecipes();
		}
	}

}
