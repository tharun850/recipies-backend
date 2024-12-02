package com.toni.Recipies;

import com.toni.Recipies.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class RecipiesApplication {

	@Autowired
	private RecipeService recipeService;

	public static void main(String[] args) {
		SpringApplication.run(RecipiesApplication.class, args);
	}

	@EventListener(ContextRefreshedEvent.class)
	public void onApplicationEvent() {
		recipeService.loadRecipes();
	}

}
