package com.banelethabede.clear_path.user;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository UserRepository;

    public User createUser(User user){
        return UserRepository.save(user);
    }

    public User getUserByEmail(String email){
        return UserRepository.findByEmail(email).orElse(null);
    }

    public User getUserById(UUID id){
        return UserRepository.findById(id).orElse(null);
    }

    public List<User> getAllUsers(){
        return UserRepository.findAll();
    }

}
