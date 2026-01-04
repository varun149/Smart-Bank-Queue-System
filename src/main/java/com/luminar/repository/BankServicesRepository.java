package com.luminar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luminar.entity.BankServices;

@Repository
public interface BankServicesRepository extends JpaRepository<BankServices, Long> {

	boolean existsByCode(String code);
}
