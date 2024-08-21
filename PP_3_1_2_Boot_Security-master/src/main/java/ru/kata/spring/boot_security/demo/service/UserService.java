package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findById(Long id);

    void saveUser(User user);

    void updateUser(Long id, User user);

    void deleteUser(Long id);

    List<User> getAllUsers();

}
