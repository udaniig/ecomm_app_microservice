package com.udanicode.inventoryservice.repository;

import com.udanicode.inventoryservice.dto.InventoryResponse;
import com.udanicode.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory,Long> {
     List<Inventory> findBySkuCodeIn(List<String> skuCodes);
}
