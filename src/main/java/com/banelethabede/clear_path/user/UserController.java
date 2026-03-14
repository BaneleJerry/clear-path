package com.banelethabede.clear_path.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
 
        private final UserService userService;
    


        @GetMapping("/")
        public ResponseEntity<List<User>> getUsers() {
            return ResponseEntity.ok(userService.getAllUsers());
        }
        
        

}
