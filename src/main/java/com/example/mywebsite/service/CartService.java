package com.example.mywebsite.service;

import com.example.mywebsite.entity.Product;
import com.example.mywebsite.model.CartProduct;
import com.example.mywebsite.entity.Cart;
import com.example.mywebsite.entity.User;
import com.example.mywebsite.repository.CartRepository;
import com.example.mywebsite.repository.ProductRepository;
import com.example.mywebsite.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    public CartService(CartRepository cartRepository, UserRepository userRepository, ProductRepository productRepository, UserService userService) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.userService = userService;
    }

    public Cart assignCart(Long userId) throws Exception {
        Cart cart = new Cart(userId);

        return cartRepository.save(cart);
    }

    public void updatePrice(Principal principal, Long id) {
        User user = userService.getUser(principal);
        Cart cart = cartRepository.findById(user.getId()).orElseThrow();

        for (CartProduct item : cart.getItems()) {
            if (Objects.equals(item.getProductId(), id)) {
                BigDecimal price = BigDecimal.valueOf(item.getPrice() * item.getQuantity());
                double roundedPrice = price.setScale(2, RoundingMode.HALF_UP).doubleValue();
                item.setCombinedPrice(roundedPrice);
                break;
            }
        }
        cartRepository.save(cart);
    }

    public void updateCheckout(Principal principal) {
        User user = userService.getUser(principal);
        Cart cart = cartRepository.findById(user.getId()).orElseThrow();

        cart.setCheckoutShippingPrice(8.99);  // static for now - later make adjustable depending on amount of objects. Free when above 200$;

        double totalProductPrice = BigDecimal.valueOf(cart.getItems().stream()
                .mapToDouble(item -> item.getCombinedPrice() != null ? item.getCombinedPrice() : 0.0)
                .sum()).setScale(2, RoundingMode.HALF_UP).doubleValue();

        cart.setCheckoutProductPrice(totalProductPrice);

        double shipping = cart.getCheckoutShippingPrice() != null ? cart.getCheckoutShippingPrice() : 0.0;
        double roundedTotal = BigDecimal.valueOf(totalProductPrice + shipping).setScale(2, RoundingMode.HALF_UP).doubleValue();

        cart.setCheckoutPrice(roundedTotal);
        cartRepository.save(cart);
    }

    public void addToCart(Principal principal, Long id, int quantity) {
        User user = userService.getUser(principal);
        Cart cart = cartRepository.findById(user.getId()).orElseThrow();
        Product p = productRepository.findById(id).orElseThrow();

        CartProduct product = new CartProduct(p.getImage(), p.getPrice(), p.getName(), quantity, id);

        boolean exists = false;
        for (CartProduct item : cart.getItems()) {
            if (Objects.equals(item.getProductId(), id)) {
                item.setQuantity(item.getQuantity() + quantity);
                exists = true;
                break;
            }
        }

        if (!exists) cart.addItem(product);
        cartRepository.save(cart);
    }

    public void setProductQuantity(Principal principal, Long id, int quantity) {
        User user = userService.getUser(principal);
        Cart cart = cartRepository.findById(user.getId()).orElseThrow();

        for (CartProduct item : cart.getItems()) {
            if (Objects.equals(item.getProductId(), id)) {
                item.setQuantity(quantity);
                break;
            }
        }

        cartRepository.save(cart);
    }

    public Cart getCart(Principal principal) {
        return cartRepository.findCartByUserId(userRepository.findByUsername(principal.getName()).orElseThrow().getId());
    }

    public void clearCart(Principal principal) {
        Cart cart = getCart(principal);
        cart.clearItems();
        cartRepository.save(cart);
    }

    public void removeProduct(Principal principal, Long id) {
        User user = userService.getUser(principal);
        Cart cart = cartRepository.findById(user.getId()).orElseThrow();

        for (CartProduct item : cart.getItems()) {
            if (Objects.equals(item.getProductId(), id)) {
                cart.removeItem(item);
                break;
            }
        }

        cartRepository.save(cart);
    }

    public void removeAll(Long id) {
        for (Cart cart : cartRepository.findAll()) {
            boolean changed = cart.getItems().removeIf(item -> Objects.equals(item.getProductId(), id));
            if (changed) {
                cartRepository.save(cart);
            }
        }
    }

}
