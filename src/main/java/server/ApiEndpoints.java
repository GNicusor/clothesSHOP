package server;

import domain.Clothes;
import domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import repository.ClothesRepository;
import repository.UserRepository;
import service.CartService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class ApiEndpoints {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClothesRepository clothesRepository;


    @GetMapping("/clothes")
    public List<Clothes> getAllClothes() {
        return clothesRepository.findAll();
    }

    @PostMapping(path = "/addclothes", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Clothes> addClothes(@RequestBody Clothes clothes) {
        Clothes saved = clothesRepository.save(clothes);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    @PostMapping("/users/{userId}/cart/{clothesId}/add")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addOneToCart(
            @PathVariable("userId")   Long userId,
            @PathVariable("clothesId") Long clothesId
    ) {
        try {
            cartService.addOneItem(userId, clothesId);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping("/users/{userId}/cart/{clothesId}/removeOne")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeOneFromCart(
            @PathVariable Long userId,
            @PathVariable Long clothesId
    ) {
        try {
            cartService.decrementItem(userId, clothesId);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @GetMapping("/clothes/{clothesId}")
    public Clothes getClothesById(
            @PathVariable("clothesId") Long clothesId
    ) {
        return clothesRepository.findById(clothesId)
                .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Clothes not found with id: " + clothesId
                                )
                        );
    }

    @DeleteMapping("/users/{userId}/cart/{clothesId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAllFromCart(
            @PathVariable Long userId,
            @PathVariable Long clothesId
    ) {
        try {
            cartService.removeAllOfItem(userId, clothesId);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }


//    @GetMapping("/users/{userId}/cart")
//    public List<Clothes> getUserCart(@PathVariable Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//        return user.getCart();
//    }

}
