package ru.kata.spring.boot_security.demo.init;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;


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
        roleRepos.save(new Role(1L, "ROLE_ADMIN"));
        roleRepos.save(new Role(2L, "ROLE_USER"));
        userService.saveUser(new User("Igor", "igor@gmail.com", "12345678", roleRepos.findAll()));
        userService.saveUser(new User("Oleg", "Oleg@mail.ru", "1234", roleRepos.findAll()));
    }
}
