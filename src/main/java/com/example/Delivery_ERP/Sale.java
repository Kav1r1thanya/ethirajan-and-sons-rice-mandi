package com.example.Delivery_ERP;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isDebt;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    
    // 🟢 Changed to Double to handle loose kg (e.g., 1.5kg)
    private Double quantity; 
    
    // 🟢 Changed to Double to handle exact purchase costs (e.g., ₹2250.75)
    private Double buyingPriceAtSale; 
    
    private Double totalAmount;
  
    private Double amountPaid; // 🟢 Add this field
 // Inside Sale.java
    
    private Double discount; // 🟢 Add this field

    // --- Add Getter and Setter ---
    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }
   
    private LocalDateTime date;

    @Column(length = 500)
    private String itemsSummary;
    
    

    // --- Constructors ---
    public Sale() {}

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean getIsDebt() { return isDebt; }
    public void setIsDebt(boolean debt) { isDebt = debt; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    
    
    public Double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(Double amountPaid) { this.amountPaid = amountPaid; }
    
    

    public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public Double getBuyingPriceAtSale() { return buyingPriceAtSale; }
    public void setBuyingPriceAtSale(Double buyingPriceAtSale) { this.buyingPriceAtSale = buyingPriceAtSale; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getItemsSummary() { return itemsSummary; }
    public void setItemsSummary(String itemsSummary) { this.itemsSummary = itemsSummary; }
    
 // 🟢 Add these exactly like this
    private boolean isHomeDelivery;
    private boolean deliveryStatus; // false = Pending, true = Delivered

    // 🟢 Corrected Getters and Setters
    public boolean isIsHomeDelivery() { return isHomeDelivery; }
    public void setIsHomeDelivery(boolean isHomeDelivery) { this.isHomeDelivery = isHomeDelivery; }

    public boolean isDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(boolean deliveryStatus) { this.deliveryStatus = deliveryStatus; }
}