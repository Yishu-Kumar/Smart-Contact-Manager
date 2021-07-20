package org.springboot.smartcontactmanager.service;

import org.springboot.smartcontactmanager.dao.UserRepository;
import org.springboot.smartcontactmanager.entites.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserService {
    //If we create an "UserRepository" interface in main package (here, "org.springboot.smartcontactmanager") without creating in any
    //other package, then we don't need to create this "Service" class.

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {

        User user1 = this.userRepository.save(user);

        return user1;
    }

    public User updateUser(User user) {

        User user1 = this.userRepository.findById(user.getId()).get();

        user1.setName(user.getName());
        user1.setEmail(user.getEmail());
        user1.setPassword(user.getPassword());
        user1.setRole(user.getRole());
        user1.setEnabled(user.isEnabled());
        user1.setImageUrl(user.getImageUrl());
        user1.setAbout(user.getAbout());

        User user2 = this.userRepository.save(user1);

        return user2;
    }

    public void deleteUser(int id) {

        this.userRepository.deleteById(id);
    }

    public User getUser(int id) {

        Optional<User> user = this.userRepository.findById(id);

        User user1 = user.get();

        return user1;
    }

    public List<User> getAllUsers() {

        Iterable<User> users = this.userRepository.findAll();

        List<User> userList = (List<User>) users;

        return userList;
    }
}
