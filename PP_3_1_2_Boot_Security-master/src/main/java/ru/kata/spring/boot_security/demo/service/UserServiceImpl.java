package ru.kata.spring.boot_security.demo.service;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, EntityManager entityManager) {
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
            managedUser.setRoles(new ArrayList<>(managedRoles));

            entityManager.merge(managedUser);
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            Set<Role> managedRoles = new HashSet<>();
            for (Role role : user.getRoles()) {
                Role managedRole = entityManager.merge(role);
                managedRoles.add(managedRole);
            }
            user.setRoles(new ArrayList<>(managedRoles));

            userRepository.save(user);
        }
    }

    @Transactional
    @Override
    public void updateUser(Long id, User userDetails) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));


            Set<Role> managedRoles = new HashSet<>();
            for (Role role : userDetails.getRoles()) {
                Role managedRole = entityManager.merge(role);
                managedRoles.add(managedRole);
            }
            user.setRoles(new ArrayList<>(managedRoles));

            entityManager.merge(user);
        } else {
            logger.warn("User with id {} does not exist", id);
        }
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
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        logger.info("Finding user by id: {}", id);
        return userRepository.findById(id);
    }
}

