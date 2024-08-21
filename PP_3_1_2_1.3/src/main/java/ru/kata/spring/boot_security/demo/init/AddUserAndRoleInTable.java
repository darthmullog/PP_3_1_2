package ru.kata.spring.boot_security.demo.init;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.HashSet;
import java.util.Set;

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

            Role roleAdmin = roleRepos.save(new Role(1L, "ROLE_ADMIN"));
            Role roleUser = roleRepos.save(new Role(2L, "ROLE_USER"));

            roleAdmin = roleRepos.findById(roleAdmin.getId()).orElseThrow();
            roleUser = roleRepos.findById(roleUser.getId()).orElseThrow();
            Set<Role> roles = new HashSet<>();
            roles.add(roleAdmin);
            roles.add(roleUser);

            userService.saveUser(new User("Igor", "igor@gmail.com", "12345678", roles));
            userService.saveUser(new User("Oleg", "oleg@mail.ru", "1234", roles));

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Ошибка при инициализации: " + e.getMessage());
        }
    }
}
