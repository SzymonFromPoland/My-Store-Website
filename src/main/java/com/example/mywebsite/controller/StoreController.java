package com.example.mywebsite.controller;

import com.example.mywebsite.entity.Product;
import com.example.mywebsite.repository.ProductRepository;
import com.example.mywebsite.service.CartService;
import com.example.mywebsite.service.OrderService;
import com.example.mywebsite.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Controller
public class StoreController {
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final OrderService orderService;

    public StoreController(ProductService productService, ProductRepository productRepository, CartService cartService, OrderService orderService) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping("/create-samples")
    public String generate() throws IOException {
        String[][] products = {
                {"Sony WH-1000XM5", "High-end noise-cancelling headphones with superior sound quality and comfort.", "399.99", "src/main/resources/static/images/products/sony_wh_1000xm5.png"},
                {"Logitech MX Keys", "Wireless illuminated keyboard with ergonomic design and seamless device switching.", "99.99", "src/main/resources/static/images/products/logitech_mx_keys.png"},
                {"Dell UltraSharp U2720Q", "27-inch 4K monitor with superb color accuracy and USB-C connectivity.", "649.99", "src/main/resources/static/images/products/dell_ultrasharp_u2720q.png"},
                {"iPhone 14 Pro", "Apple's latest smartphone featuring advanced camera system and A16 Bionic chip.", "1099.99", "src/main/resources/static/images/products/iphone_14_pro.png"},
                {"Canon EOS R5", "Mirrorless camera with 45MP resolution and 8K video capabilities.", "3899.99", "src/main/resources/static/images/products/canon_eos_r5.png"},
                {"Samsung Galaxy Watch 5", "Smartwatch with fitness tracking features and long battery life.", "329.99", "src/main/resources/static/images/products/samsung_galaxy_watch_5.png"},
                {"JBL Flip 6", "Portable Bluetooth speaker offering powerful sound and waterproof design.", "129.99", "src/main/resources/static/images/products/jbl_flip_6.png"},
                {"MacBook Pro 16", "High-performance laptop with M1 chip and Retina display.", "2499.99", "src/main/resources/static/images/products/macbook_pro_16.png"},
                {"iPad Air (5th Gen)", "Versatile tablet with A15 Bionic chip and 10.9-inch Liquid Retina display.", "599.99", "src/main/resources/static/images/products/ipad_air_5th_gen.png"},
                {"Razer DeathAdder V2", "High-precision gaming mouse with ergonomic design and customizable buttons.", "69.99", "src/main/resources/static/images/products/razer_deathadder_v2.png"},
                {"Anker PowerCore III Elite", "Portable charger with 26000mAh capacity and fast charging capabilities.", "129.99", "src/main/resources/static/images/products/anker_powercore_iii_elite.png"},
                {"Belkin USB-C to USB-C Cable", "Durable and fast-charging cable suitable for various devices.", "19.99", "src/main/resources/static/images/products/belkin_usb_c_to_usb_c_cable.png"},
                {"Logitech C920 HD Pro", "HD 1080p webcam with stereo audio and wide-screen capabilities.", "79.99", "src/main/resources/static/images/products/logitech_c920_hd_pro.png"},
                {"Blue Yeti USB Microphone", "Professional-grade USB microphone perfect for streaming and podcasting.", "129.99", "src/main/resources/static/images/products/blue_yeti_usb_microphone.png"},
                {"Samsung 970 EVO Plus 1TB", "High-speed NVMe SSD with reliable performance and large storage capacity.", "169.99", "src/main/resources/static/images/products/samsung_970_evo_plus_1tb.png"},
                {"Xbox Wireless Controller", "Ergonomic game controller with Bluetooth support and textured grip.", "59.99", "src/main/resources/static/images/products/xbox_wireless_controller.png"}
        };

        for (String[] p : products) {
            productService.createProduct(p[0], p[1], Double.parseDouble(p[2]), Files.readAllBytes(Path.of(p[3])), true, new ArrayList<>(List.of("1", "2", "3")));
        }

        return "redirect:/store";
    }

    @GetMapping("/clear")
    public String clear() throws IOException {
        productRepository.deleteAll();
        return "redirect:/store";
    }

    @GetMapping("/clear-orders")
    public String clearOrders() throws IOException {
        orderService.clearOrders();
        return "redirect:/store";
    }


    @GetMapping("/store")
    public String store(Model model,
                        @RequestParam(required = false) String input,
                        @RequestParam(required = false) String min_range,
                        @RequestParam(required = false) String max_range
    ) throws Exception {

        List<Product> products = productService.getProducts();
        List<Map<String, Object>> productList = new ArrayList<>();

        for (Product p : products) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("description", p.getDescription());
            map.put("price", p.getPrice());
            map.put("stock", p.getStock());
            map.put("image", Base64.getEncoder().encodeToString(p.getImage()));
            map.put("configurable", p.getConfigurable());
            map.put("variants", p.getVariants());

            boolean show = true;

            if (min_range != null && p.getPrice() < Double.parseDouble(min_range))
                show = false;

            if (max_range != null && p.getPrice() > Double.parseDouble(max_range))
                show = false;

            if (show) {
                if (input != null) {
                    String lowerInput = input.toLowerCase();
                    if (p.getName().toLowerCase().contains(lowerInput) || p.getDescription().toLowerCase().contains(lowerInput)) {
                        productList.add(map);

                    }
                } else {
                    productList.add(map);
                }
            }
        }
        model.addAttribute("products", productList);

        return "store";
    }

    @GetMapping("/store/product/{id}")
    public String productPage(@PathVariable Long id, Model model) {

        Product p = productService.getProductById(id);

        model.addAttribute("id", p.getId());
        model.addAttribute("name", p.getName());
        model.addAttribute("description", p.getDescription());
        model.addAttribute("price", p.getPrice());
        model.addAttribute("stock", p.getStock());
        model.addAttribute("image", Base64.getEncoder().encodeToString(p.getImage()));

        return "product";

    }

    @GetMapping("/store/product/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/store";
    }

    @PostMapping("/create")
    public String createProduct(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String price,
            @RequestParam("image") MultipartFile image,
            @RequestParam(required = false, defaultValue = "false") boolean configurable,
            @RequestParam(required = false) List<String> variants) throws IOException {

        String created = productService.createProduct(name, description, Double.parseDouble(price), image.getBytes(), configurable, variants);
        if (Objects.equals(created, "successful")) {
            return "redirect:/store?successful";
        } else {
            return "redirect:/store?" + created;
        }
    }
}
