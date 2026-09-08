
package com.example.Delivery_ERP;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.time.LocalDateTime;
public interface SaleRepository extends JpaRepository<Sale, Long> {
    
    // This magic line allows us to filter history by Customer ID!
    List<Sale> findByCustomerId(Long customerId);
    void deleteByCustomerId(Long id);
    List<Sale> findByDate(LocalDate date);
    List<Sale> findByDateAfter(LocalDate date);
   
        List<Sale> findByDateBetween(LocalDateTime start, LocalDateTime end);
        
        // Finds everything after a certain time
        List<Sale> findByDateAfter(LocalDateTime date);
    
}