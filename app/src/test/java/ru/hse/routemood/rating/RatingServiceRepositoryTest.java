package ru.hse.routemood.rating;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import ru.hse.routemood.rating.models.RatingItem;
import ru.hse.routemood.rating.repository.RatingServiceRepository;

@DataJpaTest
class RatingServiceRepositoryTest {

    private final String user1 = "user1";
    @Autowired
    private RatingServiceRepository repository;
    @Autowired
    private TestEntityManager entityManager;
    private UUID existingId;

    @BeforeEach
    void setUp() {
        RatingItem item1 = RatingItem.builder()
            .name("Route 1")
            .authorUsername(user1)
            .build();

        RatingItem item2 = RatingItem.builder()
            .name("Route 2")
            .authorUsername(user1)
            .build();

        RatingItem item3 = RatingItem.builder()
            .name("Route 3")
            .authorUsername(user1)
            .build();

        item1 = entityManager.persistAndFlush(item1);
        entityManager.persistAndFlush(item2);
        entityManager.persistAndFlush(item3);

        existingId = item1.getId();
    }

    @Test
    void testFindById_ExistingId_ReturnsItem() {
        Optional<RatingItem> found = repository.findById(existingId);

        assertTrue(found.isPresent());
        assertEquals(existingId, found.get().getId());
        assertEquals("Route 1", found.get().getName());
        assertEquals(user1, found.get().getAuthorUsername());
    }

    @Test
    void testFindById_NonExistingId_ReturnsEmpty() {
        Optional<RatingItem> found = repository.findById(UUID.randomUUID());

        assertTrue(found.isEmpty());
    }

    @Test
    void testFindAllByAuthorUsername_ExistingAuthor_ReturnsItems() {
        List<RatingItem> items = repository.findAllByAuthorUsername(user1);

        assertEquals(3, items.size());
        assertTrue(items.stream().allMatch(item -> user1.equals(item.getAuthorUsername())));
    }

    @Test
    void testFindAllByAuthorUsername_NonExistingAuthor_ReturnsEmpty() {
        List<RatingItem> items = repository.findAllByAuthorUsername("unknown");

        assertTrue(items.isEmpty());
    }

    @Test
    void testSave_NewItem_PersistsCorrectly() {
        String user2 = "user2";
        RatingItem newItem = RatingItem.builder()
            .name("New Route")
            .authorUsername(user2)
            .build();

        RatingItem saved = repository.save(newItem);
        RatingItem found = entityManager.find(RatingItem.class, saved.getId());

        assertNotNull(found);
        assertEquals("New Route", found.getName());
        assertEquals(user2, found.getAuthorUsername());
    }

    @Test
    void testDelete_ExistingItem_RemovesFromDb() {
        repository.deleteById(existingId);

        RatingItem found = entityManager.find(RatingItem.class, existingId);
        assertNull(found);
    }

    @Test
    void testFindAll_ReturnsAllItems() {
        List<RatingItem> items = repository.findAll();

        assertEquals(3, items.size());
    }

    @Test
    void testFindById_NullId_ThrowsException() {
        assertThrows(InvalidDataAccessApiUsageException.class,
            () -> repository.findById(null));
    }

    @Test
    void testUpdateItem_ChangesPersisted() {
        RatingItem item = repository.findById(existingId).orElseThrow();
        item.setName("Updated Name");

        repository.save(item);
        RatingItem updated = entityManager.find(RatingItem.class, existingId);

        assertEquals("Updated Name", updated.getName());
    }
}