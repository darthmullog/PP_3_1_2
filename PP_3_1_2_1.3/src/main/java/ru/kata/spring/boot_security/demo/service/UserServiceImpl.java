package ru.kata.spring.boot_security.demo.service;

import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, EntityManager entityManager, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.entityManager = entityManager;

    }

    @Transactional
    @Override
    public void saveUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            User managedUser = existingUser.get();
            managedUser.setUsername(user.getUsername());
            managedUser.setPassword(passwordEncoder.encode(user.getPassword()));
            Set<Role> managedRoles = new HashSet<>();
            for (Role role : user.getRoles()) {
                Role managedRole = entityManager.merge(role);
                managedRoles.add(managedRole);
            }
            managedUser.setRoles(managedRoles);
            entityManager.merge(managedUser);
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            Set<Role> managedRoles = new HashSet<>();
            for (Role role : user.getRoles()) {
                Role managedRole = entityManager.merge(role);
                managedRoles.add(managedRole);
            }
            user.setRoles(managedRoles);
            userRepository.save(user);
        }
    }

    public void updateUser(Long id, User user) {

        User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        Hibernate.initialize(existingUser.getRoles());
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        existingUser.setRoles(user.getRoles());
        userRepository.save(existingUser);
    }


    @Transactional
    @Override
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            logger.warn("User with id {} does not exist", id);
        }
    }

    @Override
    public Set<User> getAllUsers() {
        return new HashSet<>(userRepository.findAll());
    }

    @Transactional
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
