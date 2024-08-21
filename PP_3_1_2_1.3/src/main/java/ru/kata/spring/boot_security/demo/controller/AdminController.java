package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepos;


    @Autowired
    public AdminController(UserService userService, RoleRepository roleRepos, RoleService roleService) {
        this.userService = userService;
        this.roleRepos = roleRepos;
    }


    @GetMapping
    public String listUsers(Model model) {
        Set<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }


    @GetMapping("/new")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleRepos.findAll());
        return "new";
    }

    @PostMapping("/new")
    public String addUser(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/admin";
    }


    @GetMapping("/edit")
    public String createUpdateForm(@RequestParam("id") Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleRepos.findAll());  // Добавляем роли
        return "update";  // Возвращаем имя шаблона
    }


    @PostMapping("/edit")
    public String updateUser(@RequestParam("id") Long id,
                             @RequestParam("username") String username,
                             @RequestParam("email") String email,
                             @RequestParam("roles") List<String> roles) {

        User user = userService.findById(id);

        user.setUsername(username);
        user.setEmail(email);

        user.getRoles().clear();
        for (String roleName : roles) {
            Role role = roleRepos.findByName(roleName);
            if (role != null) {
                user.getRoles().add(role);
            }
        }

        userService.updateUser(user.getId(), user);

        return "redirect:/admin";
    }


    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }


}
