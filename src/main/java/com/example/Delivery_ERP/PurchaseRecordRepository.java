package com.example.Delivery_ERP;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PurchaseRecordRepository extends JpaRepository<PurchaseRecord, Long> {
    // This custom query finds all history for a specific supplier
    List<PurchaseRecord> findAllBySupplierIdOrderByDateDesc(Long supplierId);
}
