package com.example.mywebsite.controller;

import com.example.mywebsite.entity.Coupon;
import com.example.mywebsite.entity.User;
import com.example.mywebsite.model.CartProduct;
import com.example.mywebsite.entity.Cart;
import com.example.mywebsite.model.HomeAddress;
import com.example.mywebsite.service.*;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

@Controller
public class CartController {
    private final CartService cartService;
    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;
    private final CouponService couponService;

    public CartController(CartService cartService, ProductService productService, OrderService orderService, UserService userService, CouponService couponService) {
        this.cartService = cartService;
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;
        this.couponService = couponService;
    }

    @PostMapping("/account/add-to-cart")
    public String addToCart(@RequestParam("id") Long id, Principal principal) {
        cartService.addToCart(principal, id, 1);
        return "redirect:/store?added=true";
    }

    @GetMapping("/account/clear-cart")
    public String clearCart(Principal principal) {
        cartService.clearCart(principal);
        return "redirect:/store";
    }

    @PostMapping("/account/update-quantity")
    @ResponseBody
    public String updateQuantity(@RequestParam("id") Long id, @RequestParam("quantity") int quantity, Principal principal) {
        cartService.setProductQuantity(principal, id, quantity);
        cartService.updatePrice(principal, id);
        cartService.updateCheckout(principal);
        return "ok";
    }

    @PostMapping("/account/remove-product")
    @ResponseBody
    public String removeProduct(@RequestParam("id") Long id, Principal principal) {
        cartService.removeProduct(principal, id);
        cartService.updateCheckout(principal);
        return "ok";
    }

    @PostMapping("/account/cart/apply-coupon")
    public String applyCoupon(@RequestParam String code, Principal principal) {
        try {
            String result = couponService.assignCoupon(principal, couponService.getByCode(code));
            return "redirect:/account/cart?" + result;
        } catch (Exception e) {
            couponService.assignCoupon(principal, new Coupon());
            return "redirect:/account/cart?error=notfound";
        }
    }

    @GetMapping("/account/cart")
    public String showCart(Principal principal, Model model) {
        Cart cart = cartService.getCart(principal);
        List<CartProduct> items = cart.getItems();
        List<Map<String, Object>> itemsList = new ArrayList<>();

        cart.getItems().removeIf(item -> !productService.exists(item.getProductId()));

        for (CartProduct item : items) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getProductId());
            map.put("name", item.getName());
            map.put("price", item.getPrice());
            map.put("quantity", item.getQuantity());
            map.put("image", Base64.getEncoder().encodeToString(item.getImage()));
            map.put("combinedPrice", item.getCombinedPrice());
            itemsList.add(map);
        }

        cartService.updateCheckout(principal);

        String code = Optional.ofNullable(cart.getActiveCoupon()).orElse("");

        model.addAttribute("items", itemsList);
        model.addAttribute("code", code);
        model.addAttribute("checkoutProductPrice", cart.getCheckoutProductPrice());
        model.addAttribute("checkoutShippingPrice", cart.getCheckoutShippingPrice());
        model.addAttribute("checkoutPrice", cart.getCheckoutPrice());
        try {
            Coupon coupon = couponService.getByCode(code);
            model.addAttribute("discount", coupon.getDiscount());
            model.addAttribute("actualPrice", cart.getCheckoutPrice() * (1 - coupon.getDiscount() / 100));
        } catch (Exception _) {
        }

        return "cart";
    }

    @GetMapping("/account/cart/order")
    public String order(Principal principal) {
        User user = userService.getUser(principal);
        Cart cart = cartService.getCart(principal);

        if (cart.getItems().isEmpty()) {
            return "redirect:/account/cart?error=empty";
        }

        HomeAddress address = user.getAddress();
        if (address == null || Stream.of(
                address.getCountry(),
                address.getCity(),
                address.getAddress(),
                address.getPostalCode(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone()
        ).anyMatch(field -> field == null || field.isBlank())) {
            return "redirect:/account/cart?error=data";
        }

        try {
            Coupon coupon = couponService.getByCode(cart.getActiveCoupon());
            couponService.useCoupon(coupon);
            orderService.createOrder(principal, cart.getCheckoutPrice(), LocalDate.now(), user.getAddress(), coupon.getDiscount());
        } catch (Exception e) {
            orderService.createOrder(principal, cart.getCheckoutPrice(), LocalDate.now(), user.getAddress(), 0.0);
        }

        couponService.assignCoupon(principal, new Coupon());

        return "redirect:/account/cart?successful=order";
    }
}
