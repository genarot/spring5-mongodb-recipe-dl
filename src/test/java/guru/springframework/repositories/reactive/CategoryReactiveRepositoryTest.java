package guru.springframework.repositories.reactive;

import guru.springframework.domain.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class CategoryReactiveRepositoryTest {

    public static final String FAST_FOOD = "Fast Food";
    @Autowired
    CategoryReactiveRepository categoryReactiveRepository;

    @BeforeEach
    public void setUp() {
        categoryReactiveRepository.deleteAll().block();
    }

    @Test
    public void testSave(){
        Category category = new Category();
        category.setDescription(FAST_FOOD);

        categoryReactiveRepository.save(category).block();

        Long count = categoryReactiveRepository.count().block();
        assertEquals(1L, count.longValue());
    }

    @Test
    public void testFindByDescription() {
        Long initial  = categoryReactiveRepository.count().block();

        assertEquals(0L,initial.longValue());

        testSave();

        Category category = categoryReactiveRepository.findByDescription(FAST_FOOD).block();

        assertNotNull(category);
    }
}