package org.springboot.smartcontactmanager.controller;

import org.springboot.smartcontactmanager.entites.User;
import org.springboot.smartcontactmanager.helper.Message;
import org.springboot.smartcontactmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @RequestMapping("/logIn")
    public String logIn(Model model) {

        return "logIn";
    }

    @RequestMapping("/")
    public String home(Model model) {

        model.addAttribute("title", "Home - Smart Contact Manager");

        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model) {

        model.addAttribute("title", "About - Smart Contact Manager");

        return "about";
    }

    @RequestMapping("/signUp")
    public String signUp(Model model) {

        model.addAttribute("title", "Register - Smart Contact Manager");
        model.addAttribute("user", new User());

        return "signUp";
    }

    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String register(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
                           @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
                           HttpSession session) {
        //"value" in "@RequestParam()" annotation contains the name of "field" that we have provided to an "checkbox" and
        //"default" in "@RequestParam()" is used so that if user doesn't accept (or don't check it), then it's value will
        //be set to "false" by default.
        //"HttpSession" is used to handle the error and shown to user.

        model.addAttribute("title", "Register - Smart Contact Manager");

        try {

            if(!agreement) {

                System.out.println("You haven't agreed the terms and conditions !!");

                throw new Exception("You haven't agreed the terms and conditions"); //This exception will be catch by "catch" block below.
            }

            if(bindingResult.hasErrors()) {

                System.out.println("ERROR: " + bindingResult.toString());
                model.addAttribute("user", user);

                return "signUp";
            }

            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default");

            //Encoding the password.
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            System.out.println("Agreement = " + agreement);
            System.out.println("User = " + user);

            User user1 = this.userService.saveUser(user);

            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));

            return "signUp";

        } catch(Exception e) {

            System.out.println(e.getMessage());
            e.printStackTrace();

            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong !! " + e.getMessage(), "alert-danger"));

            return "signUp";
        }
    }

}
