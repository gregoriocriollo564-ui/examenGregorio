/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.model;

/**
 *
 * @author Gregorio
 */
public class User {
    private String name;
    private String email;
    private String phone;
    private String city;
    private Address address;
    private Company company;
            
    public User(String name, String email, String phone, Address address, Company company) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.company = company;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getCity() { return city; }
    public Address getAddress() { return address; }
    public Company getCompany() { return company; }
}
