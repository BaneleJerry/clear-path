package com.banelethabede.clear_path.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
 
        private final UserService userService;
    
        // Define your endpoints here, for example:
        // @PostMapping("/register")
        // public ResponseEntity<User> registerUser(@RequestBody User user) {
        //     User createdUser = userService.createUser(user);
        //     return ResponseEntity.ok(createdUser);
        // }

        @PostMapping("/register")
        public User registerUser(@RequestBody User user) {
            return userService.createUser(user);
        }

        @GetMapping("/")
        public ResponseEntity<List<User>> getUsers() {
            return ResponseEntity.ok(userService.getAllUsers());
        }
        
        

}
