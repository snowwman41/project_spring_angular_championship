package com.project.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserDetailsServiceImplTest {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testLoadUserByUsernameWithCorrectUsernameAndPassword() {
        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername("user");
        // then
        assertEquals("user", userDetails.getUsername());
        String password = userDetails.getPassword();
        assertTrue(passwordEncoder.matches("secret", password));
    }

    @Test
    void testLoadUserByUsernameWithInvalidPassword() {
        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername("user");
        // then
        String password = userDetails.getPassword();
        assertFalse(passwordEncoder.matches("secret2", password));
    }

    @Test
    void testLoadUserByUsernameWithInvalidUsername() {
        // when
        Executable call = () -> userDetailsService.loadUserByUsername("user2");
        // then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, call);
        assertEquals("Utilisateur non trouvé", exception.getMessage());
    }
}