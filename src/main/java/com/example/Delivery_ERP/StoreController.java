package com.example.Delivery_ERP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping; 
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.time.*;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.Set;
import java.util.ArrayList;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
public class StoreController {

    @Autowired
    private ProductRepository productRepository; 
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private SaleRepository saleRepository;
    
    @Autowired
    private SupplierRepository supplierRepository;
    
    @Autowired
    private PurchaseRecordRepository purchaseRecordRepository;
    
    @Autowired
    private ExpenseRepository expenseRepository;

    

    @GetMapping("/hello")
    public String welcome() {
        return "<h1>Welcome to the Grocery ERP!</h1><p>The Server is LIVE and running in Kolathur.</p>";
    }
    
    @PostMapping("/add-expense")
    public Expense addExpense(@RequestBody Expense expense) {
        if (expense.getDate() == null) expense.setDate(LocalDate.now());
        return expenseRepository.save(expense);
    }

    @GetMapping("/view-expenses")
    public List<Expense> getExpenses(@RequestParam String start, @RequestParam String end) {
        return expenseRepository.findByDateBetween(LocalDate.parse(start), LocalDate.parse(end));
    }

    @DeleteMapping("/delete-expense/{id}")
    public String deleteExpense(@PathVariable Long id) {
        expenseRepository.deleteById(id);
        return "Deleted";
    }

    @PostMapping("/add-product")
    public Product addProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }
    
    @PostMapping("/checkout/{id}/{qty}")
    public String processSale(@PathVariable Long id, @PathVariable int qty) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (product.getQuantity() >= qty) {
            product.setQuantity(product.getQuantity() - qty);
            productRepository.save(product);
            return "SUCCESS: Sold " + qty + " units of " + product.getName();
        } else {
            return "ERROR: Not enough stock!";
        }
    }
    
    @PostMapping("/restock/{id}/{qty}")
    public String restockProduct(@PathVariable("id") Long id, @PathVariable("qty") int qty) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        product.setQuantity(product.getQuantity() + qty); // Adding instead of subtracting
        productRepository.save(product);
        return "Stock increased by " + qty;
    }
    @GetMapping("/customer-history/{id}")
    public Map<String, Object> getCustomerHistory(@PathVariable Long id) {
        System.out.println("--- Server hit for customer ID: " + id + " ---");
        Map<String, Object> response = new HashMap<>();
        
        Customer customer = customerRepository.findById(id).orElse(null);
        response.put("customer", customer);
        
        List<Sale> sales = saleRepository.findByCustomerId(id);
        response.put("sales", sales);
        
        return response;
    }
    
 // Inside StoreController.java
    @GetMapping("/view-customers-debt")
    public List<Map<String, Object>> getCustomersWithDebtStatus() {
        List<Customer> customers = customerRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Customer c : customers) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("name", c.getName());
            map.put("phone", c.getPhone());
            map.put("address", c.getAddress());
            map.put("debt", c.getDebt());
            
            // Fetch the last recorded sale or payment to find the "Debt Age"
            List<Sale> history = saleRepository.findByCustomerId(c.getId());
            if (!history.isEmpty()) {
                history.sort((s1, s2) -> s2.getDate().compareTo(s1.getDate()));
                map.put("lastActivity", history.get(0).getDate()); // 🟢 Key for Red Alert
            } else {
                map.put("lastActivity", null);
            }
            response.add(map);
        }
        return response;
    }

    @PostMapping("/add-customer")
    public ResponseEntity<?> addCustomer(@RequestBody Customer customer) {
        // 1. Look for the phone number
        Optional<Customer> existing = customerRepository.findAll().stream()
                .filter(c -> c.getPhone().equals(customer.getPhone()))
                .findFirst();

        // 2. If found, DON'T save. Send back the existing data with a 302 Found status
        if (existing.isPresent()) {
            return ResponseEntity.status(302).body(existing.get()); 
        }

        // 3. If totally new, save as usual
        if (customer.getAddress() == null) customer.setAddress("No Address Provided");
        customer.setDebt(0.0);
        customer.setTotalOrders(0);
        
        return ResponseEntity.ok(customerRepository.save(customer));
    }
    
    @PostMapping("/update-customer")
    public ResponseEntity<?> updateCustomerDetails(@RequestBody Map<String, Object> updates) {
        Long id = Long.parseLong(updates.get("id").toString());
        Customer existing = customerRepository.findById(id).orElse(null);
        
        if (existing != null) {
            existing.setName(updates.get("name").toString());
            existing.setPhone(updates.get("phone").toString());
            existing.setAddress(updates.get("address").toString());
            // We keep debt and total orders as they are
            return ResponseEntity.ok(customerRepository.save(existing));
        }
        return ResponseEntity.status(404).body("Customer not found");
    }
    
    @DeleteMapping("/delete-customer/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        customerRepository.deleteById(id);
        return "Customer Deleted";
    }
    
    @PostMapping("/pay-debt/{id}/{amount}")
    public String payDebt(@PathVariable Long id, @PathVariable Double amount) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer != null) {
            // 1. Update the balance
            customer.setDebt(customer.getDebt() - amount);
            customerRepository.save(customer);

            // 2. Add a record to Sale history so the list is accurate
            Sale paymentRecord = new Sale();
            paymentRecord.setCustomerId(id);
            paymentRecord.setTotalAmount(amount);
            paymentRecord.setItemsSummary("Balance Cleared / Payment Received");
            paymentRecord.setIsDebt(false); // This marks it as a PAYMENT
            paymentRecord.setDate(LocalDateTime.now());
            saleRepository.save(paymentRecord);
        }
        return "Paid";
    }
    
    @PostMapping("/update-product-full/{id}")
    public String updateProductFull(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Product product = productRepository.findById(id).orElseThrow();
        
        // 1. Update basic info
        product.setBrand(updates.get("brand").toString());
        product.setPrice(Double.parseDouble(updates.get("price").toString()));
        product.setBagPrice(Double.parseDouble(updates.get("bagPrice").toString()));
        product.setBagWeight(Integer.parseInt(updates.get("bagWeight").toString()));
        
        // 2. Logic for Stock & Supplier History
        double addedQty = Double.parseDouble(updates.get("addedQty").toString());
        if (addedQty > 0) {
            product.setQuantity(product.getQuantity() + addedQty);
            
            // 🟢 THE FIX: Create a history record for the Supplier Management page
            if (updates.get("supplierId") != null && !updates.get("supplierId").toString().isEmpty()) {
                Long sId = Long.parseLong(updates.get("supplierId").toString());
                Supplier supplier = supplierRepository.findById(sId).orElse(null);
                
                if (supplier != null) {
                    // Calculate bags and cost
                    int bags = (int)(addedQty / (double) product.getBagWeight());
                    double buyingPricePerBag = Double.parseDouble(updates.get("buyingPrice").toString());
                    double totalCost = bags * buyingPricePerBag;

                    // Create the Record for history
                    PurchaseRecord record = new PurchaseRecord();
                    record.setSupplierId(sId);
                    record.setItemName(product.getBrand() + " " + product.getName());
                    record.setBags(bags);
                    record.setCost(totalCost);
                    record.setDate(LocalDateTime.now()); // 🟢 CRITICAL: Set current time
                    record.setTransactionType("STOCK_ADD");
                    purchaseRecordRepository.save(record);

                    // Update Supplier Debt Balance
                    supplier.setTotalBillAmount(supplier.getTotalBillAmount() + totalCost);
                    supplier.setBalanceDebt(supplier.getTotalBillAmount() - supplier.getPaidAmount());
                    supplierRepository.save(supplier);
                }
            }
        }
        
        productRepository.save(product);
        return "Updated Successfully";
    }
    
 // 1. DELETE functionality
    @DeleteMapping("/delete-supplier/{id}")
    public String deleteSupplier(@PathVariable Long id) {
        supplierRepository.deleteById(id);
        return "Deleted";
    }

    // 2. EDIT functionality 
    @PostMapping("/edit-supplier/{id}")
    public Supplier editSupplier(@PathVariable Long id, @RequestBody Supplier updated) {
        Supplier existing = supplierRepository.findById(id).orElseThrow();
        existing.setName(updated.getName());
        existing.setPhone(updated.getPhone());
        existing.setAddress(updated.getAddress());
        return supplierRepository.save(existing);
    }

    // 3. PAYMENT functionality
    @PostMapping("/pay-supplier/{id}/{amount}")
    public String paySupplier(@PathVariable Long id, @PathVariable double amount) {
        Supplier s = supplierRepository.findById(id).orElseThrow();
        s.setPaidAmount(s.getPaidAmount() + amount);
        s.setBalanceDebt(s.getTotalBillAmount() - s.getPaidAmount());
        supplierRepository.save(s);

        // LOG THE PAYMENT IN HISTORY
        PurchaseRecord record = new PurchaseRecord();
        record.setSupplierId(id);
        record.setItemName("CASH PAYMENT");
        record.setBags(0); 
        record.setCost(amount);
        record.setTransactionType("PAYMENT");
        record.setDate(LocalDateTime.now()); // 🟢 ADD THIS LINE
        purchaseRecordRepository.save(record);

        return "Payment Recorded";
    }
    
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Replace this with a database lookup later
        if ("Admin".equals(username) && "2310".equals(password)) {
            return ResponseEntity.ok(Map.of("role", "ADMIN", "name", "Vadivel"));
        } else if ("staff".equals(username) && "5987".equals(password)) {
            return ResponseEntity.ok(Map.of("role", "STAFF", "name", "Billing Counter"));
        }

        return ResponseEntity.status(401).body("Invalid Credentials");
    }
    
    @GetMapping("/api/stats")
    public ResponseEntity<Map<String, Object>> getStats(@RequestParam String date, @RequestParam String range) {
        Map<String, Object> stats = new HashMap<>();
       

        // 1. UPDATED RANGE LOGIC (Daily vs Monthly only)
     // 🟢 IMPROVED DATE HANDLING
        LocalDate targetDate = LocalDate.parse(date);
        // Ensure we cover the full 24 hours of the selected date
        LocalDateTime start = targetDate.atStartOfDay(); 
        LocalDateTime end = targetDate.atTime(LocalTime.MAX); 

        List<Sale> filteredSales;
        if ("monthly".equals(range)) {
            // 30 days trailing from the selected date
            filteredSales = saleRepository.findByDateAfter(start.minusDays(30));
        } else {
            // Strict single day view
            filteredSales = saleRepository.findByDateBetween(start, end);
        }
        double cashInHand = 0;
        double totalSales = 0;
        double totalDebtGiven = 0;
        double totalNetProfit = 0;

        // 2. PRIMARY CALCULATION LOOP (Must happen first)
     // 2. PRIMARY CALCULATION LOOP
        if (filteredSales != null) {
            for (Sale s : filteredSales) {
                double revenue = (s.getTotalAmount() != null) ? s.getTotalAmount() : 0.0;
                double cashReceived = (s.getAmountPaid() != null) ? s.getAmountPaid() : 0.0;

                // Skip calculations for simple balance payments already handled in cashInHand
                if (s.getItemsSummary() != null && s.getItemsSummary().contains("Balance Cleared")) {
                    cashInHand += revenue;
                    continue; 
                }

                totalSales += revenue;

                if (s.getIsDebt()) {
                    // 🟢 ACTUAL DEBT = Full Bill - Cash Paid Today
                    totalDebtGiven += (revenue - cashReceived);
                    // 🟢 CASH IN HAND includes the partial payment from this debt sale
                    cashInHand += cashReceived;
                } else {
                    // Full cash sale
                    cashInHand += revenue;
                }

             // 🟢 Subtract discount from the Net Profit for accurate Business Intelligence
                double cost = (s.getBuyingPriceAtSale() != null) ? s.getBuyingPriceAtSale() : 0.0;
                double discount = (s.getDiscount() != null) ? s.getDiscount() : 0.0;
                totalNetProfit += (revenue - cost - discount);
            }
        }

        // 3. GROUPING LOGIC FOR BI CHARTS
     // 3. IMPROVED GROUPING LOGIC (Handles Bag & Retail Sales)
     // 3. SMART GROUPING LOGIC (Handles Bag & Retail Sales)
     // 3. SMART GROUPING LOGIC
     // 3. ULTIMATE GROUPING LOGIC (Handles "(Bag)" format from your DB)
     // 3. PRECISION GROUPING LOGIC (Calculates exact bag counts)
        Map<String, Double> profitByProduct = new HashMap<>();
        Map<String, Double> qtyByProduct = new HashMap<>(); 
        Map<String, Integer> bagsByProduct = new HashMap<>(); 

        // Pre-fetch products to get Bag Weights
        Map<String, Integer> weightMap = productRepository.findAll().stream()
            .collect(Collectors.toMap(p -> (p.getBrand() + " " + p.getName()).toLowerCase(), Product::getBagWeight, (v1, v2) -> v1));

        if (filteredSales != null) {
            for (Sale s : filteredSales) {
                String summary = s.getItemsSummary();
                if (summary == null || summary.contains("Balance Cleared")) continue;

                String[] parts = summary.split(", ");
                for (String part : parts) {
                    try {
                        String cleanPart = part.trim();
                        if (cleanPart.isEmpty()) continue;

                        if (cleanPart.contains("(Bag)")) {
                            // 1. Extract Name
                            String name = cleanPart.substring(0, cleanPart.indexOf(" (Bag)")).trim();
                            
                            // 2. Extract Total Weight from the last set of parentheses
                            String weightStr = cleanPart.substring(cleanPart.lastIndexOf("(") + 1, cleanPart.lastIndexOf(")")).trim();
                            double totalWeight = Double.parseDouble(weightStr);
                            
                            // 3. Get individual bag weight from our map (default to 25 if not found)
                            int individualBagWeight = weightMap.getOrDefault(name.toLowerCase(), 25);
                            
                            // 4. Calculate Actual Bag Count: 260.0 / 26 = 10
                            int actualBagCount = (int) Math.round(totalWeight / individualBagWeight);
                            
                            bagsByProduct.put(name, bagsByProduct.getOrDefault(name, 0) + actualBagCount);
                            
                            // Standard Profit Logic
                            double saleProfit = (s.getTotalAmount() != null ? s.getTotalAmount() : 0.0) - 
                                               (s.getBuyingPriceAtSale() != null ? s.getBuyingPriceAtSale() : 0.0);
                            profitByProduct.put(name, profitByProduct.getOrDefault(name, 0.0) + saleProfit);

                        } else if (cleanPart.contains(" (")) {
                            // RETAIL CASE
                            String name = cleanPart.substring(0, cleanPart.indexOf(" (")).trim();
                            double kg = Double.parseDouble(cleanPart.substring(cleanPart.indexOf("(") + 1, cleanPart.indexOf(")")).trim());
                            qtyByProduct.put(name, qtyByProduct.getOrDefault(name, 0.0) + kg);
                            
                            double saleProfit = (s.getTotalAmount() != null ? s.getTotalAmount() : 0.0) - 
                                               (s.getBuyingPriceAtSale() != null ? s.getBuyingPriceAtSale() : 0.0);
                            profitByProduct.put(name, profitByProduct.getOrDefault(name, 0.0) + saleProfit);
                        }
                    } catch (Exception e) {
                        System.out.println("Error parsing part: " + part);
                    }
                }
            }
        }
        // 4. SORT AND GET TOP 5
     // 4. SORT AND GET TOP 5 BY PROFIT
     // ... (existing grouping loops) ...

        // 🟢 BAG DATA
        List<String> topBagLabels = bagsByProduct.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(5).map(Map.Entry::getKey).collect(Collectors.toList());

        stats.put("topBagLabels", topBagLabels);
        stats.put("topBags", topBagLabels.stream().map(bagsByProduct::get).collect(Collectors.toList()));

        // 🔵 RETAIL QTY DATA
        List<String> topQtyLabels = qtyByProduct.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(5).map(Map.Entry::getKey).collect(Collectors.toList());

        stats.put("topQtyLabels", topQtyLabels);
        stats.put("topQtys", topQtyLabels.stream().map(qtyByProduct::get).collect(Collectors.toList()));

        // 🟢 PROFIT DATA
        List<String> topProfitLabels = profitByProduct.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(5).map(Map.Entry::getKey).collect(Collectors.toList());

        stats.put("topProfitLabels", topProfitLabels);
        stats.put("topProfits", topProfitLabels.stream().map(profitByProduct::get).collect(Collectors.toList()));

        // Logic for Today's Hero - Now using topQtyLabels
        

        // Logic for Idle Stock
     // Optimized Idle Stock Logic (Top 3 items not sold recently)
     // 🟢 THE FIX: You must fetch the products first!
     // 1. COLLECT ALL SOLD NAMES
        Set<String> allSoldNames = new HashSet<>();
        allSoldNames.addAll(qtyByProduct.keySet());
        allSoldNames.addAll(bagsByProduct.keySet());

        // 2. FETCH PRODUCTS
        List<Product> allProducts = productRepository.findAll();

        // 3. SLOW MOVING STOCK (Zero sales)
        List<String> slowMoving = allProducts.stream()
            .filter(p -> p.getQuantity() > 0)
            .filter(p -> {
                String fullName = (p.getBrand() + " " + p.getName()).toLowerCase();
                // This checks if the product name appears in any of the sold item strings
                return allSoldNames.stream().noneMatch(sold -> 
                    fullName.contains(sold.toLowerCase()) || sold.toLowerCase().contains(fullName)
                );
            })
            .map(p -> p.getBrand() + " " + p.getName() + " (" + (int)(p.getQuantity()/p.getBagWeight()) + " bags left)")
            .limit(4)
            .collect(Collectors.toList());

        stats.put("slowMovingList", slowMoving);

        // 4. REORDER ALERTS (Mapped to "oldStock" for your JS)
        List<Map<String, Object>> reorderAlerts = allProducts.stream()
            .filter(p -> p.getQuantity() < (p.getBagWeight() * 5)) 
            .limit(4)
            .map(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("brand", p.getBrand());
                map.put("name", p.getName());
                map.put("qty", (int)p.getQuantity());
                return map;
            })
            .collect(Collectors.toList());

        // 🟢 CRITICAL: Match this to your 'data.oldStock' in HTML
        stats.put("oldStock", reorderAlerts); 

        // 5. HERO & TOTALS
        String hero = (topQtyLabels.isEmpty()) ? "No Sales" : topQtyLabels.get(0);
        stats.put("heroVariety", hero);
        stats.put("totalCash", cashInHand);
        stats.put("totalSales", totalSales);
        stats.put("totalDebt", totalDebtGiven);
        stats.put("totalProfit", totalNetProfit);
        stats.put("totalItems", (filteredSales != null) ? filteredSales.size() : 0);

        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/view-suppliers")
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @PostMapping("/add-supplier")
    public Supplier addSupplier(@RequestBody Supplier supplier) {
        // Initialize balance just in case
        supplier.setBalanceDebt(supplier.getTotalBillAmount() - supplier.getPaidAmount());
        return supplierRepository.save(supplier);
    }
    
    @PostMapping("/log-supply")
    public String logSupply(@RequestBody Map<String, Object> data) {
        Long sId = Long.parseLong(data.get("supplierId").toString());
        double cost = Double.parseDouble(data.get("cost").toString());
        
        Supplier s = supplierRepository.findById(sId).orElseThrow();
        s.setTotalBillAmount(s.getTotalBillAmount() + cost);
        s.setBalanceDebt(s.getTotalBillAmount() - s.getPaidAmount());
        supplierRepository.save(s);
        
        // Save to history (Need a PurchaseRepository for this)
        return "Logged";
    }
   
    @PostMapping("/log-purchase-order")
    public String logPurchase(@RequestBody Map<String, Object> data) {
        Long sId = Long.parseLong(data.get("supplierId").toString());
        int bags = Integer.parseInt(data.get("bags").toString());
        double totalCost = Double.parseDouble(data.get("totalCost").toString());
        double buyingPrice = Double.parseDouble(data.get("buyingPrice").toString());
        boolean isNew = (boolean) data.get("isNewProduct");

        Product p;

        if (isNew) {
            // 1. Create a brand new Product entry
            p = new Product();
            p.setBrand(data.get("brand").toString());
            p.setName(data.get("variety").toString());
            p.setPrice(Double.parseDouble(data.get("retailPrice").toString()));
            p.setBagPrice(Double.parseDouble(data.get("wholesalePrice").toString()));
            p.setBagWeight(Integer.parseInt(data.get("weight").toString()));
            p.setMinStockBags(Integer.parseInt(data.get("minStock").toString())); // The alert limit
            p.setQuantity(0.0); // Start at 0, will add bags below
            p.setSupplierId(sId);
            p.setBuyingPrice(buyingPrice);
        } else {
            // 2. Use existing product
            Long pId = Long.parseLong(data.get("productId").toString());
            p = productRepository.findById(pId).orElseThrow();
        }

        // 3. Update the Stock (Common logic)
        double addedQty = bags * p.getBagWeight();
        p.setQuantity(p.getQuantity() + addedQty);
        p.setBuyingPrice(buyingPrice);
        productRepository.save(p);

        // 4. Update Supplier Debt
        Supplier s = supplierRepository.findById(sId).orElseThrow();
        s.setTotalBillAmount(s.getTotalBillAmount() + totalCost);
        s.setBalanceDebt(s.getTotalBillAmount() - s.getPaidAmount());
        supplierRepository.save(s);

        // 5. Save to Purchase History
        PurchaseRecord record = new PurchaseRecord();
        record.setSupplierId(sId);
        record.setItemName(p.getBrand() + " " + p.getName());
        record.setBags(bags);
        record.setCost(totalCost);
        record.setDate(LocalDateTime.now());
        purchaseRecordRepository.save(record);

        return "Purchase Logged Successfully";
    }

    // ADD THIS NEW ENDPOINT to send history to the frontend
    @GetMapping("/supplier-history/{id}")
    public List<PurchaseRecord> getHistory(@PathVariable Long id) {
        return purchaseRecordRepository.findAllBySupplierIdOrderByDateDesc(id);
    }
    
    @PostMapping("/process-sale/{customerId}/{isDebt}")
    public ResponseEntity<?> handleSale(@PathVariable Long customerId, 
                             @PathVariable boolean isDebt, 
                             @RequestBody List<Map<String, Object>> items, 
                             @RequestParam Double total,
                             @RequestParam(defaultValue = "0") Double amountPaid,
                             @RequestParam(defaultValue = "0") Double discount,
                             @RequestParam(defaultValue = "") String deliveryAddress,
                             @RequestParam(defaultValue = "false") boolean isHomeDelivery) {
        
        double totalBuyingCost = 0.0;
        StringBuilder summary = new StringBuilder();

        for (Map<String, Object> item : items) {
            Long pId = Long.parseLong(item.get("id").toString());
            double qtySold = Double.parseDouble(item.get("qty").toString());
            Product p = productRepository.findById(pId).orElse(null);
            if (p != null) {
                double costPerKg = p.getBuyingPrice() / (p.getBagWeight() <= 0 ? 25.0 : (double) p.getBagWeight());
                totalBuyingCost += (costPerKg * qtySold);
            }
            summary.append(item.get("name").toString()).append(" (").append(qtySold).append("), ");
        }

        Sale history = new Sale();
        history.setCustomerId(customerId > 0 ? customerId : null);
        history.setTotalAmount(total);
        history.setItemsSummary(summary.toString());
        history.setIsDebt(isDebt); 
        history.setDate(LocalDateTime.now());
        history.setBuyingPriceAtSale(totalBuyingCost); 
        history.setQuantity(1.0);
        history.setIsHomeDelivery(isHomeDelivery);
        history.setAmountPaid(amountPaid);
        history.setDiscount(discount);
        history.setDeliveryStatus(false);

        // 🟢 SYNC CUSTOMER IDENTITY
        if (customerId > 0) {
            Customer c = customerRepository.findById(customerId).orElse(null);
            if (c != null) {
                history.setCustomerName(c.getName()); 
                history.setCustomerPhone(c.getPhone());
                // Use provided delivery address if home delivery, otherwise use stored address
                history.setCustomerAddress(isHomeDelivery ? deliveryAddress : c.getAddress());
            }
        } else {
            history.setCustomerName("Guest");
            history.setCustomerAddress(deliveryAddress);
        }

        saleRepository.save(history);

        // 3. Update Customer Debt Ledger with Partial Payment Math[cite: 2]
        double oldDebt = 0.0;
        double unpaidFromThisSale = isDebt ? (total - amountPaid) : 0.0;

        if (customerId > 0) {
            Customer customer = customerRepository.findById(customerId).orElse(null);
            if (customer != null) {
                oldDebt = customer.getDebt(); 
                customer.setTotalOrders(customer.getTotalOrders() + 1);
                
                // New Total = Previous Balance + (Total Bill - Cash Paid Today)[cite: 2]
                customer.setDebt(oldDebt + unpaidFromThisSale);
                customerRepository.save(customer);
                
                return ResponseEntity.ok(Map.of(
                    "prevDebt", oldDebt,
                    "currentSaleDebt", unpaidFromThisSale,
                    "totalDebt", customer.getDebt()
                ));
            }
        }
        return ResponseEntity.ok(Map.of("totalDebt", 0.0, "status", "Sale Processed"));
    }
    
    @PostMapping("/update-delivery-status/{saleId}")
    public ResponseEntity<?> updateDeliveryStatus(@PathVariable Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Sale record not found"));
        
        sale.setDeliveryStatus(true); // 🟢 Mark as Done
        saleRepository.save(sale);
        
        return ResponseEntity.ok("Status Updated");
    }
    
    @GetMapping("/sales-by-date/{date}")
    public List<Sale> getSalesByDate(@PathVariable String date) {
        // This assumes your 'date' comes in as 'YYYY-MM-DD'
        // We fetch all sales and filter those that start with that date
        return saleRepository.findAll().stream()
            .filter(sale -> sale.getDate() != null && 
                    sale.getDate().toString().startsWith(date))
            .collect(Collectors.toList());
    }
    
    @GetMapping("/api/weekly-profit")
    public ResponseEntity<Map<String, Object>> getWeeklyProfit() {
        Map<String, Object> data = new LinkedHashMap<>(); // Keeps the days in order
        List<String> labels = new ArrayList<>();
        List<Double> profits = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);

            List<Sale> daySales = saleRepository.findByDateBetween(start, end);
            
            double dayProfit = 0;
            for (Sale s : daySales) {
                // Ignore balance cleared records for profit calculation
                if (s.getItemsSummary() != null && s.getItemsSummary().contains("Balance Cleared")) continue;
                
                double revenue = (s.getTotalAmount() != null) ? s.getTotalAmount() : 0.0;
                double cost = (s.getBuyingPriceAtSale() != null) ? s.getBuyingPriceAtSale() : 0.0;
                dayProfit += (revenue - cost);
            }

            labels.add(date.getDayOfWeek().toString().substring(0, 3)); // e.g., "MON"
            profits.add(dayProfit);
        }

        data.put("labels", labels);
        data.put("profits", profits);
        return ResponseEntity.ok(data);
    }
    
    @DeleteMapping("/delete-purchase-record/{id}")
    public String deletePurchaseRecord(@PathVariable Long id) {
        purchaseRecordRepository.deleteById(id);
        return "Deleted";
    }
    
    @DeleteMapping("/clear-history/{customerId}")
    @Transactional
    public String clearHistory(@PathVariable Long customerId) {
        saleRepository.deleteByCustomerId(customerId);
        return "History Cleared";
    }

    @GetMapping("/view-products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    @DeleteMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "Product with ID " + id + " has been deleted successfully!";
    }
} 