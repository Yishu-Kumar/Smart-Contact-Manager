package org.springboot.smartcontactmanager.config;

import org.springboot.smartcontactmanager.dao.UserRepository;
import org.springboot.smartcontactmanager.entites.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        //Fetching "User" from database.

        User user = this.userRepository.getUserByUserName(s); //s -> User Email.

        if(user == null) {

            throw new UsernameNotFoundException("Couldn't found user !!");
        }

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        return customUserDetails;
    }
}
