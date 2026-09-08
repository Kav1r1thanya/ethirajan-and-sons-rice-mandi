package com.example.Delivery_ERP;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phone;
    private String address;

    // Use these names to match your Controller logic
    private double totalBillAmount; 
    private double paidAmount;
    private double balanceDebt;

    // --- GETTERS AND SETTERS ---
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getTotalBillAmount() { return totalBillAmount; }
    public void setTotalBillAmount(double totalBillAmount) { this.totalBillAmount = totalBillAmount; }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }

    public double getBalanceDebt() { return balanceDebt; }
    public void setBalanceDebt(double balanceDebt) { this.balanceDebt = balanceDebt; }
}