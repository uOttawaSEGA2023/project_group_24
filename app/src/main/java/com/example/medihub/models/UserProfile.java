package com.example.medihub.models;

import com.example.medihub.enums.UserRole;
import android.util.Patterns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.medihub.interfaces.Model;

public class UserProfile implements Model, Serializable {
    // INSTANCE VARIABLES
    private String key;
    private UserRole role;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String email;

    // STATIC VARIABLES
    protected static final String POSTAL_CODE_REGEX = "^[A-Z][0-9][A-Z] [0-9][A-Z][0-9]$";
    public static final int MIN_PASSWORD_LENGTH = 8;


    // CONSTRUCTORS
    public UserProfile() {}

    public UserProfile(UserRole role) { this.role = role; }

    public UserProfile(UserRole role, String firstName, String lastName, String address, String phoneNumber, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.email = email;
    }

    // GETTERS
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getAddress() {
        return address;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getEmail() { return email; }
    public UserRole getRole() {
        return role;
    }

    // SETTERS
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // METHODS
    @Override
    public String toString() {
        return getRole().toString() + " User Profile: " +
                "\nName: " + getFirstName() + " " + getLastName() +
                "\nEmail: " + getEmail() +
                "\nAddress: " + getAddress() +
                "\nPhone Number: " + getPhoneNumber();
    }


    public HashMap<String, String> validate() {
        HashMap<String, String> errors = new HashMap<>();

        if (firstName == null || firstName.isEmpty()) {
            errors.put("firstName", "first name can't be blank");
        }
        if (lastName == null || lastName.isEmpty()) {
            errors.put("lastName", "last name can't be blank");
        }
        if (phoneNumber == null || !Patterns.PHONE.matcher(phoneNumber).matches()) {
            errors.put("phoneNumber", "invalid phone number");
        }
        if (address == null || !address.matches(POSTAL_CODE_REGEX)) {
            errors.put("address", "invalid postal code address (format: A1A 1A1)");
        }

        return errors;
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
}
