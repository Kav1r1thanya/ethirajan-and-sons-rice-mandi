package com.example.Delivery_ERP;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id; // <--- MUST BE THIS ONE

@Entity
public class Customer {
    @Id // <--- Ensure this is right here
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double debt; // Tracks how much the customer owes
    
    private String name;
    private String phone;
    private String email;
    private String address;
    private int totalOrders;
    private LocalDateTime lastUpdated;

 // Remove the setter for custId, and use this getter:
 public String getCustId() {
     if (this.id == null) return "NOM-PENDING";
     return "NOM-" + (1000 + this.id);
 }
	public Long getId() {
		return id;
	}
	
	public double getDebt() {
		return debt;
	}

	public void setDebt(double debt) {
		this.debt = debt;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getTotalOrders() {
		return totalOrders;
	}
	public void setTotalOrders(int totalOrders) {
		this.totalOrders = totalOrders;
	}
	

	// Getter
	public LocalDateTime getLastUpdated() {
	    return lastUpdated;
	}

	// Setter
	public void setLastUpdated(LocalDateTime lastUpdated) {
	    this.lastUpdated = lastUpdated;
	}
    
    

    // IMPORTANT: Make sure you have Getters and Setters below!
}