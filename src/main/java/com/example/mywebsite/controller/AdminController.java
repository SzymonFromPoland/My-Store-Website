package com.example.mywebsite.controller;

import com.example.mywebsite.entity.Order;
import com.example.mywebsite.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class AdminController {

    private final OrderService orderService;

    public AdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/manager/orders")
    public String orders(Model model) {
        List<Order> products = orderService.getOrders();
        List<Map<String, Object>> productList = new ArrayList<>();

        for (Order o : products) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", o.getId());
            map.put("userName", o.getUserName());
            map.put("userId", o.getUserId());
            map.put("userLastName", o.getUserLastName());

            map.put("date", o.getDate());
            map.put("price", o.getPrice());
            map.put("discount", (o.getDiscount() / 100) * o.getPrice());
            map.put("actualPrice", o.getActualPrice());
            map.put("status", o.getStatus());
            productList.add(map);
        }

        model.addAttribute("orders", productList);

        return "orders";
    }

    @PostMapping("/manager/orders/{id}/status")
    public String orderStatus(@RequestParam String status, @PathVariable Integer id, Model model) {
        orderService.changeStatus(id.longValue(), status);

        return "redirect:/manager/orders";
    }

    @PostMapping("/manager/orders/{id}/delete")
    public String deleteOrder(@PathVariable Integer id, Model model) {
        orderService.removeOrder(id.longValue());

        return "redirect:/manager/orders";
    }

    @GetMapping("/manager/orders/{id}/address")
    public String address(@PathVariable Integer id, Model model) {
        Order order = orderService.getOrderById(id.longValue());
        model.addAttribute("id", id);
        model.addAttribute("userName", order.getUserName());
        model.addAttribute("userLastName", order.getUserLastName());
        model.addAttribute("address", order.getAddress().getAddress());
        model.addAttribute("city", order.getAddress().getCity());
        model.addAttribute("postalCode", order.getAddress().getPostalCode());
        model.addAttribute("country", order.getAddress().getCountry());


        return "order-address";
    }
}
