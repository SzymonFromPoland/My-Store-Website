package com.example.mywebsite.controller;

import com.example.mywebsite.entity.*;
import com.example.mywebsite.model.HomeAddress;
import com.example.mywebsite.service.CartService;
import com.example.mywebsite.service.OrderService;
import com.example.mywebsite.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

@Controller
public class AccountController {

    private final UserService userService;
    private final CartService cartService;
    private final OrderService orderService;

    public AccountController(UserService userService, CartService cartService, OrderService orderService) {
        this.userService = userService;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping("/login")
    public String loadLogin() {
        return "login";
    }

    @GetMapping("/register")
    public String registerLogin() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, Model model) {
        try {
            User user = userService.registerUser(username, password, "ROLE_USER");
            Cart cart = cartService.assignCart(user.getId());
            user.setAddress(new HomeAddress());
            return "redirect:/register?successful";
        } catch (Exception e) {
            return "redirect:/register?error";
        }
    }

    @GetMapping("/account/data")
    public String accountData(Principal principal, Model model) {
        User user = userService.getUser(principal);


        String firstName = Optional.ofNullable(user.getFirstName()).orElse("");
        String lastName = Optional.ofNullable(user.getLastName()).orElse("");
        String email = Optional.ofNullable(user.getEmail()).orElse("");

        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("email", email);
        return "account-data";
    }

    @PostMapping("/account/data")
    public String updateData(Principal principal, @RequestParam String firstName, @RequestParam String lastName, @RequestParam String email) {
        try {
            User user = userService.getUser(principal);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);

            userService.saveUser(user);

            return "redirect:/account/data?successful";
        } catch (Exception e) {
            return "redirect:/account/data?error";
        }
    }

    @GetMapping("/account/address")
    public String accountAddress(Principal principal, Model model) {
        User user = userService.getUser(principal);

        if (user.getAddress() == null) user.setAddress(new HomeAddress());

        String country = Optional.ofNullable(user.getAddress().getCountry()).orElse("");
        String city = Optional.ofNullable(user.getAddress().getCity()).orElse("");
        String address = Optional.ofNullable(user.getAddress().getAddress()).orElse("");
        String postal = Optional.ofNullable(user.getAddress().getPostalCode()).orElse("");
        String phone = Optional.ofNullable(user.getPhone()).orElse("");

        model.addAttribute("country", country);
        model.addAttribute("city", city);
        model.addAttribute("address", address);
        model.addAttribute("postal", postal);
        model.addAttribute("phone", phone);
        return "account-address";
    }

    @PostMapping("/account/address")
    public String updateAddress(Principal principal, @RequestParam String country, @RequestParam String city, @RequestParam String address, @RequestParam String postal, @RequestParam String phone) {
        try {
            User user = userService.getUser(principal);
            if (user.getAddress() == null) user.setAddress(new HomeAddress());
            user.getAddress().setCountry(country);
            user.getAddress().setCity(city);
            user.getAddress().setAddress(address);
            user.getAddress().setPostalCode(postal);
            user.setPhone(phone);

            userService.saveUser(user);

            return "redirect:/account/address?successful";
        } catch (Exception e) {
            return "redirect:/account/address?error";
        }
    }

    @GetMapping("/account/history")
    public String accountHistory(Principal principal, Model model) {
        List<Order> orders = orderService.getOrders();
        List<Map<String, Object>> orderList = new ArrayList<>();

        for (Order order : orders) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("date", order.getDate());
            map.put("price", order.getActualPrice());
            map.put("status", order.getStatus());
            orderList.add(map);
        }

        model.addAttribute("orders", orderList);

        return "account-history";
    }
}
