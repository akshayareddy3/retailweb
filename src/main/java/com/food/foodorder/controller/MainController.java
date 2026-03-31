package com.food.foodorder.controller;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.food.foodorder.model.OrderEntity;
import com.food.foodorder.model.Product;
import com.food.foodorder.model.User;
import com.food.foodorder.repository.OrderRepository;
import com.food.foodorder.repository.ProductRepository;
import com.food.foodorder.repository.UserRepository;

@Controller
public class MainController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    public MainController(UserRepository userRepository,
                          ProductRepository productRepository,
                          OrderRepository orderRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }
        userRepository.save(user);

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/admin";
        }
        return "redirect:/user";
    }

    @GetMapping("/user")
    public String userPage(Model model) {
        seedProducts();
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("orders", orderRepository.findAll());
        model.addAttribute("username", "Demo User");
        return "user";
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        seedProducts();
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("orders", orderRepository.findAll());
        model.addAttribute("totalProducts", productRepository.count());
        model.addAttribute("totalOrders", orderRepository.count());
        return "admin";
    }

    @GetMapping("/confirmation")
    public String confirmation() {
        return "confirmation";
    }

    @ResponseBody
    @GetMapping("/api/products")
    public List<Product> getProducts() {
        seedProducts();
        return productRepository.findAll();
    }

    @ResponseBody
    @PostMapping("/api/products")
    public Product addProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @ResponseBody
    @GetMapping("/api/orders")
    public List<OrderEntity> getOrders() {
        return orderRepository.findAll();
    }

    @ResponseBody
    @PostMapping("/api/orders")
    public OrderEntity addOrder(@RequestBody OrderEntity order) {
        if (order.getStatus() == null || order.getStatus().isBlank()) {
            order.setStatus("CONFIRMED");
        }
        return orderRepository.save(order);
    }

    @PostMapping("/admin/increaseStock/{id}")
    public String increaseStock(@PathVariable Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            product.setStock(product.getStock() + 1);
            productRepository.save(product);
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/decreaseStock/{id}")
    public String decreaseStock(@PathVariable Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null && product.getStock() > 0) {
            product.setStock(product.getStock() - 1);
            productRepository.save(product);
        }
        return "redirect:/admin";
    }
    @ResponseBody
@PostMapping("/api/products/{id}/increase")
public Product increaseStockApi(@PathVariable Long id) {
    Product product = productRepository.findById(id).orElse(null);
    if (product != null) {
        product.setStock(product.getStock() + 1);
        return productRepository.save(product);
    }
    return null;
}

@ResponseBody
@PostMapping("/api/products/{id}/decrease")
public Product decreaseStockApi(@PathVariable Long id) {
    Product product = productRepository.findById(id).orElse(null);
    if (product != null && product.getStock() > 0) {
        product.setStock(product.getStock() - 1);
        return productRepository.save(product);
    }
    return product;
}

    private void seedProducts() {
        if (productRepository.count() == 0) {
            productRepository.save(new Product("Margherita Pizza", "Pizza", "Urban Slice", 249, 20, "https://images.unsplash.com/photo-1513104890138-7c749659a591?q=80&w=1200&auto=format&fit=crop"));
            productRepository.save(new Product("Garlic Bread", "Bread", "Bake House", 129, 15, "https://images.unsplash.com/photo-1509440159596-0249088772ff?q=80&w=1200&auto=format&fit=crop"));
            productRepository.save(new Product("Mineral Water", "Water", "Aqua Pure", 30, 50, "https://images.unsplash.com/photo-1564419320461-6870880221ad?q=80&w=1200&auto=format&fit=crop"));
            productRepository.save(new Product("Veg Burger", "Burger", "Street Grill", 179, 18, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?q=80&w=1200&auto=format&fit=crop"));
            productRepository.save(new Product("Cold Coffee", "Beverages", "Cafe Rush", 99, 25, "https://images.unsplash.com/photo-1517701604599-bb29b565090c?q=80&w=1200&auto=format&fit=crop"));
            productRepository.save(new Product("French Fries", "Snacks", "Crisp Box", 119, 30, "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?q=80&w=1200&auto=format&fit=crop"));
        }
    }
}