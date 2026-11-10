package com.foodapp.restaurant;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RestaurantController {

    private final RestaurantRepository restaurants;
    private final MenuItemRepository menuItems;

    RestaurantController(RestaurantRepository restaurants, MenuItemRepository menuItems) {
        this.restaurants = restaurants;
        this.menuItems = menuItems;
    }

    // ---- Commands ----

    @PostMapping("/restaurants")
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurant createRestaurant(@Valid @RequestBody Restaurant restaurant) {
        restaurant.setId(null);
        return restaurants.save(restaurant);
    }

    @PostMapping("/restaurants/{restaurantId}/menu")
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItem addMenuItem(@PathVariable Long restaurantId, @Valid @RequestBody MenuItem item) {
        requireRestaurant(restaurantId);
        item.setId(null);
        item.setRestaurantId(restaurantId);
        return menuItems.save(item);
    }

    @PutMapping("/menu-items/{id}/availability")
    public MenuItem setAvailability(@PathVariable Long id, @RequestParam boolean available) {
        MenuItem item = menuItems.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));
        item.setAvailable(available);
        return menuItems.save(item);
    }

    // ---- Queries ----

    @GetMapping("/restaurants")
    public List<Restaurant> listRestaurants() {
        return restaurants.findAll();
    }

    @GetMapping("/restaurants/{id}")
    public Restaurant getRestaurant(@PathVariable Long id) {
        return requireRestaurant(id);
    }

    @GetMapping("/restaurants/{restaurantId}/menu")
    public List<MenuItem> getMenu(@PathVariable Long restaurantId) {
        requireRestaurant(restaurantId);
        return menuItems.findByRestaurantId(restaurantId);
    }

    /** Used by order-service to validate and price items. */
    @GetMapping("/menu-items/{id}")
    public ResponseEntity<MenuItem> getMenuItem(@PathVariable Long id) {
        return menuItems.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private Restaurant requireRestaurant(Long id) {
        return restaurants.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));
    }
}
