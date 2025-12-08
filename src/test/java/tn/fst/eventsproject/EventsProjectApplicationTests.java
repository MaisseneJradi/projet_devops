package tn.fst.eventsproject.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import tn.fst.eventsproject.model.User;
import tn.fst.eventsproject.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService; // classe à tester

    @BeforeEach
    void setUp() {
        // Initialiser les mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_shouldSaveUser() {
        // Préparer un utilisateur sans ID (nouveau)
        User user = new User(null, "Alice", "alice@example.com");

        // Définir le comportement du mock pour save
        when(userRepository.save(any(User.class)))
                .thenReturn(new User(1L, "Alice", "alice@example.com"));

        // Appeler la méthode à tester
        User created = userService.createUser(user);

        // Vérifier le résultat
        assertNotNull(created);
        assertEquals(1L, created.getId());
        assertEquals("Alice", created.getName());
        assertEquals("alice@example.com", created.getEmail());

        // Vérifier que save a été appelé exactement une fois
        verify(userRepository, times(1)).save(any(User.class));
    }
}
