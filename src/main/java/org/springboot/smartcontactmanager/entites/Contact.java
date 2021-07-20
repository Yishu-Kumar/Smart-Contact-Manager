package org.springboot.smartcontactmanager.entites;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "Contact")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id")
    private int id;

    @Column(name = "Name")
    private String name;

    @Column(name = "Second_Name")
    private String secondName; //secondName -> nickName.

    @Column(name = "Work")
    private String work;

    @Column(name = "Email")
    private String email;

    @Column(name = "Phone_Number")
    private String phoneNumber;

    @Column(name = "Image_URL")
    private String imageUrl;

    @Column(name = "Description", length = 200)
    private String description;

    @ManyToOne
    @JsonIgnore //So that "User" data will not "Serializable", otherwise it will create the circular loop.
    private User user;

    public Contact() {

    }

    public Contact(int id, String name, String secondName, String work, String email, String phoneNumber, String imageUrl,
                   String description, User user) {
        this.id = id;
        this.name = name;
        this.secondName = secondName;
        this.work = work;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.description = description;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Contact[id = " + id + ", name = " + name + ", secondName = " + secondName + ", work = " + work + ", email = " +
               email + ", phoneNumber = " + phoneNumber + ", imageUrl = " + imageUrl + ", description = " + description + "]";
    }
}
