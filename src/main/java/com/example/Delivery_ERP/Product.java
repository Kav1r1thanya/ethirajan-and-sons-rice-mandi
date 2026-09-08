
package com.example.Delivery_ERP; 

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity 
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
   

    public Product() {} // Required for JPA

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    
    private String brand;
    // e.g., "Unity", "Walima", "Fresh Cook"

 // Add Getter and Setter
 public String getBrand() { return brand; }
 public void setBrand(String brand) { this.brand = brand; }
 
//Inside Product.java
private double buyingPrice;

public double getBuyingPrice() { return buyingPrice; }
public void setBuyingPrice(double buyingPrice) { this.buyingPrice = buyingPrice; }
 
 private double bagPrice;    // Price for one full bag
 private int minStockBags;   // Alert threshold (e.g., 5 bags)

 // Add Getters and Setters for both
 
//Inside Product.java
private Long supplierId; // Add this

public Long getSupplierId() { return supplierId; }
public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
 
 private int bagWeight; // 25 or 26

public double getBagPrice() {
	return bagPrice;
}

 public void setBagPrice(double bagPrice) {
	this.bagPrice = bagPrice;
 }

 public int getMinStockBags() {
	return minStockBags;
 }

 public void setMinStockBags(int minStockBags) {
	this.minStockBags = minStockBags;
 }

//Add getter and setter
public int getBagWeight() { return bagWeight; }
public void setBagWeight(int bagWeight) { this.bagWeight = bagWeight; }

    // GETTERS AND SETTERS (Right Click -> Source -> Generate Getters and Setters)
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    private double quantity; // Change from int to double

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
}
