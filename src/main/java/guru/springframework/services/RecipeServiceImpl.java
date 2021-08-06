package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by jt on 6/13/17.
 */
@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeReactiveRepository recipeReactiveRepository;
    private final RecipeCommandToRecipe recipeCommandToRecipe;
    private final RecipeToRecipeCommand recipeToRecipeCommand;

    public RecipeServiceImpl(RecipeReactiveRepository recipeReactiveRepository, RecipeCommandToRecipe recipeCommandToRecipe, RecipeToRecipeCommand recipeToRecipeCommand) {
        this.recipeReactiveRepository = recipeReactiveRepository;
        this.recipeCommandToRecipe = recipeCommandToRecipe;
        this.recipeToRecipeCommand = recipeToRecipeCommand;
    }

    @Override
    public Flux<Recipe> getRecipes() {
        log.debug("I'm in the service");

        return recipeReactiveRepository.findAll();
    }

    @Override
    public Mono<Recipe> findById(String id) {

        Recipe recipeOptional = recipeReactiveRepository.findById(id).block();

        if (recipeOptional == null) {
            throw new NotFoundException("Recipe Not Found. For ID value: " + id );
        }

        return Mono.just(recipeOptional);
    }

    @Override
    public Mono<RecipeCommand> findCommandById(String id) {
        return findById(id)
                .map(recipeToRecipeCommand::convert);
    }

    @Override
    public Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command) {
        Recipe detachedRecipe = recipeCommandToRecipe.convert(command);

        return recipeReactiveRepository.save(detachedRecipe)
                .map(recipeToRecipeCommand::convert);
//        log.debug("Saved RecipeId:" + savedRecipe.getId());
//        return Mono.just(recipeToRecipeCommand.convert(savedRecipe));
    }

    @Override
    public Mono<Void> deleteById(String idToDelete) {
        recipeReactiveRepository.deleteById(idToDelete);
        return Mono.empty();
    }
}
