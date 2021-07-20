package org.springboot.smartcontactmanager.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springboot.smartcontactmanager.dao.ContactRepository;
import org.springboot.smartcontactmanager.dao.MyOrderRepository;
import org.springboot.smartcontactmanager.dao.UserRepository;
import org.springboot.smartcontactmanager.entites.Contact;
import org.springboot.smartcontactmanager.entites.MyOrder;
import org.springboot.smartcontactmanager.entites.User;
import org.springboot.smartcontactmanager.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private MyOrderRepository myOrderRepository;

    //This method will run before every handler (or method/user url) and add an user.
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        //"Principal" will get the unique id (here, Email) from login details.

        String userName = principal.getName();

        User user = this.userRepository.getUserByUserName(userName); //userName -> User Email.
        System.out.println("User: " + user);

        model.addAttribute("user", user);
    }

    @RequestMapping("/index")
    public String dashboard(Model model) {

        model.addAttribute("title", "User Dashboard");

        return "normal/user_dashboard"; //"normal" is the directory that contains "user_dashboard" page.
    }

    @GetMapping("/add-Contact")
    public String openAddContactForm(Model model) {

        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());

        return "normal/add_contact_form";
    }

    @PostMapping("/process-contact")
    public String processAddContactForm(@ModelAttribute("contact") Contact contact, @RequestParam("profileImage") MultipartFile file,
                                        Principal principal, Model model, HttpSession session) {

        try {

            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            if(file.isEmpty()) {

                System.out.println("File is empty !!");
                contact.setImageUrl("contact.jpg");

            } else {

                contact.setImageUrl(file.getOriginalFilename());

                File file1 = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(file1.getAbsolutePath() + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("File uploaded !!");
            }

            contact.setUser(user);
            user.getContacts().add(contact);

            this.userRepository.save(user);
            System.out.println("Contact: " + contact);

            session.setAttribute("message", new Message("Your contact is added !! Add more...", "success"));

        } catch (Exception e) {

            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();

            session.setAttribute("message", new Message("Something went wrong !! Try again...", "danger"));
        }

        return "normal/add_contact_form";
    }

    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") int page, Principal principal, Model model) {

        String name = principal.getName();

        User user = this.userRepository.getUserByUserName(name);

        //PageRequest -> Pageable.
        Pageable pageable = PageRequest.of(page, 4); //4 -> number of contacts want to show in one page.

        Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);

        model.addAttribute("title", "Show User Contacts");

        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page); //currentPage -> current page.
        model.addAttribute("totalPages", contacts.getTotalPages()); //Total number of pages according to division of contacts per page.

        return "normal/show_contacts";
    }

    @RequestMapping("/{id}/contact-detail")
    public String showContactDetail(@PathVariable("id") int id, Model model, Principal principal) {

        System.out.println("Contact id: " + id);

        Optional<Contact> contact = this.contactRepository.findById(id);

        Contact contact1 = contact.get();

        String name = principal.getName();

        User user = this.userRepository.getUserByUserName(name);

        if(user.getId() == contact1.getUser().getId()) {

            model.addAttribute("title", contact1.getName());
            model.addAttribute("contact", contact1);
        }

        return "normal/contact_detail";
    }

    @GetMapping("/delete/{id}")
    public String deleteContact(@PathVariable("id") int id, Model model, Principal principal, HttpSession session) {

        System.out.println("Contact id: " + id);

        Contact contact = this.contactRepository.findById(id).get();

        String name = principal.getName();

        User user = this.userRepository.getUserByUserName(name);

        if(user.getId() == contact.getUser().getId()) {

            contact.setUser(null); //To unlink (or unmap) the contact with user.
            this.contactRepository.delete(contact);

            session.setAttribute("message", new Message("Contact deleted successfully...", "success"));
        }

        return "redirect:/user/show-contacts/0";
    }

    @PostMapping("/update-contact/{id}")
    public String updateContact(@PathVariable("id") int id, Model model, Principal principal, HttpSession session) {

        Contact contact = this.contactRepository.findById(id).get();

        model.addAttribute("title", "Update Contact");
        model.addAttribute("contact", contact);

        return "normal/update_contact";
    }

    @PostMapping("/process-update")
    public String processUpdateContact(@ModelAttribute("contact") Contact contact, @RequestParam("profileImage") MultipartFile file, Model model,
                                       Principal principal, HttpSession session) {

        try {

            System.out.println(contact);
            Contact oldContactDetails = this.contactRepository.findById(contact.getId()).get();

            if(!file.isEmpty()) {

                //Delete old file.
                File deleteFile = new ClassPathResource("static/img").getFile();

                File f = new File(deleteFile, oldContactDetails.getImageUrl());
                f.delete();

                //Save new file.
                contact.setImageUrl(file.getOriginalFilename());

                File file1 = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(file1.getAbsolutePath() + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            } else {

                contact.setImageUrl(oldContactDetails.getImageUrl());
            }

            User user = this.userRepository.getUserByUserName(principal.getName());

            contact.setUser(user); //Again mapping the contact to user.

            this.contactRepository.save(contact);

            session.setAttribute("message", new Message("Your contact is updated...", "success"));

        } catch (Exception e) {

            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/user/" + contact.getId() + "/contact-detail";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {

        User user = this.userRepository.getUserByUserName(principal.getName());

        model.addAttribute("title", "Profile Page");
        model.addAttribute("user", user);

        return "normal/profile";
    }

    @RequestMapping("/settings")
    public String openSettings() {

        return "normal/settings";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword,
                                 Principal principal, HttpSession session) {

        System.out.println("OLD PASSWORD: " + oldPassword);
        System.out.println("NEW PASSWORD: " + newPassword);

        User currentUser = this.userRepository.getUserByUserName(principal.getName());
        System.out.println(currentUser);

        if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {

            //Change the password.
            currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(currentUser);

            session.setAttribute("message", new Message("Your password is successfully changed...", "success"));

        } else {

            //Error.
            session.setAttribute("message", new Message("Please enter correct old password !!", "danger"));

            return "redirect:/user/settings";
        }

        return "redirect:/user/index";
    }

    //Creating order for payment.
    @PostMapping("/create_order")
    @ResponseBody
    public String createOrder(@RequestBody Map<String, Object> data, Principal principal) throws RazorpayException {

        System.out.println("Info: " + data);

        int amount = Integer.parseInt(data.get("amount").toString());

        var razorpayClient = new RazorpayClient("rzp_test_m36y7H3aZmfb94", "P3x87yL2QMQM48KdumSYfSz5");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", amount * 100); //"* 100" -> To convert "amount" into "Paisa" instead of "Rupees".
        jsonObject.put("currency", "INR"); //INR -> Indian Rupees.
        jsonObject.put("receipt", "txn_235425");

        //Creating new order.
        Order order = razorpayClient.Orders.create(jsonObject);
        System.out.println("Order: " + order);

        //Save the order in database.
        MyOrder myOrder = new MyOrder();

        myOrder.setAmount(order.get("amount") + ""); //"" -> To convert the "Integer" to String". Because amount in "MyOrder" class is "String", but amount in "Order" will be "Integer".
        myOrder.setOrderId(order.get("id"));
        myOrder.setPaymentId(null);
        myOrder.setStatus("created"); //Status will be created now, not the "Paid".
        myOrder.setUser(this.userRepository.getUserByUserName(principal.getName()));
        myOrder.setReceipt(order.get("receipt"));

        this.myOrderRepository.save(myOrder);

        return order.toString();
    }

    @PostMapping("/update_order")
    public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data) {

        System.out.println("Data: " + data);

        MyOrder myOrder = this.myOrderRepository.findByOrderId(data.get("order_id").toString());

        myOrder.setPaymentId(data.get("payment_id").toString());
        myOrder.setStatus(data.get("status").toString());

        this.myOrderRepository.save(myOrder);

        return ResponseEntity.ok(Map.of("msg", "Updated"));
    }

}
