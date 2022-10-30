package com.example.springboottest.controller;

import com.example.springboottest.model.User;
import com.example.springboottest.service.RoleService;
import com.example.springboottest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

@Controller
@RequestMapping
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired()
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/")
    public String users(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

    @GetMapping("/{id}")
    public String getUser (@PathVariable("id") long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "user";
    }

    @GetMapping("/new")
    public String addUser(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("listRoles", roleService.getAllRoles());
        return "create";
    }

    @PostMapping("/new")
    public String add(@ModelAttribute("user")
                          @Valid User user, BindingResult bindingResult,
                      @RequestParam("listRoles") ArrayList<Long> roles) {
        if (bindingResult.hasErrors()) {
            return "create";
        }
        if (userService.getUserByLogin(user.getName()) != null) {
            bindingResult.addError(new FieldError("username", "username",
                    String.format("User with name \"%s\" is already exist!", user.getName())));
            return "create";
        }
        user.setRoles(roleService.getRoleById(roles));
        userService.saveUser(user);
        return "redirect:/";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return "redirect:/";
    }

    @GetMapping("edit/{id}")
    public String updateUser(@PathVariable("id") long id, Model model) {
        model.addAttribute(userService.getUserById(id));
        model.addAttribute("listRoles", roleService.getAllRoles());
        return "edit";
    }

    @PatchMapping("/edit")
    public String update(@ModelAttribute("user")
                             @Valid User user, BindingResult bindingResult,
                         @RequestParam("listRoles") ArrayList<Long>roles) {
        if (bindingResult.hasErrors()) {
            return "edit";
        }
        user.setRoles(roleService.getRoleById(roles));
        userService.updateUser(user);
        return "redirect:/";

    }
}
