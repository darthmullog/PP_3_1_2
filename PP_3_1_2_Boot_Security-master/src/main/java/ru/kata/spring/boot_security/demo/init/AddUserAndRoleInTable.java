package ru.kata.spring.boot_security.demo.init;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Arrays;
import java.util.List;


@Component
public class AddUserAndRoleInTable {

    private final RoleRepository roleRepos;
    private final UserService userService;

    @Autowired
    public AddUserAndRoleInTable(RoleRepository roleRepos, UserService userService) {
        this.roleRepos = roleRepos;
        this.userService = userService;
    }

    @PostConstruct
    private void init() {
        try {
            System.out.println("Инициализация ролей...");
            Role roleAdmin = roleRepos.save(new Role(1L, "ROLE_ADMIN"));
            Role roleUser = roleRepos.save(new Role(2L, "ROLE_USER"));
            System.out.println("Роли сохранены.");

            System.out.println("Инициализация пользователей...");
            roleAdmin = roleRepos.findById(roleAdmin.getId()).orElseThrow();
            roleUser = roleRepos.findById(roleUser.getId()).orElseThrow();
            List<Role> roles = Arrays.asList(roleAdmin, roleUser);

            userService.saveUser(new User("Igor", "igor@gmail.com", "12345678", roles));
            userService.saveUser(new User("Oleg", "oleg@mail.ru", "1234", roles));
            System.out.println("Пользователи сохранены.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Ошибка при инициализации: " + e.getMessage());
        }
    }




}
