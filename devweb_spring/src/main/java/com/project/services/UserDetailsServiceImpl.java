package com.project.services;

import com.project.entities.Team;
import com.project.entities.User;
import com.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    @Value("${admin.username}")
    String username;

    @Value("${admin.password}")
    String password;

    public UserDetailsServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        return new UserDetailsImpl(user.username, user.password);
    }
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        addDefaultUser();
    }

    private void addDefaultUser() {
        if (userRepository.count() > 0) return;
            User entity = new User(username,passwordEncoder.encode(password));
            userRepository.save(entity);

        /* TODO : ajouter un utilisateur */
    }

}