package com.luminar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luminar.entity.BankServices;
import com.luminar.entity.Customer;
import com.luminar.entity.QueueToken;
import com.luminar.entity.TokenStatus;

@Repository
public interface QueueTokenRepository extends JpaRepository<QueueToken, Long> {

	// Get the latest active token of a customer
	QueueToken findFirstByCustomerAndStatusInOrderByCreatedAtDesc(Customer customer, List<TokenStatus> statuses);

	// Get past tokens for a customer
	List<QueueToken> findByCustomerAndStatusInOrderByCreatedAtDesc(Customer customer, List<TokenStatus> statuses);

	// Get the currently serving token for a service
	QueueToken findFirstByServiceAndStatusOrderByCreatedAtAsc(BankServices service, TokenStatus status);

	// Optional: get waiting tokens for a service
	List<QueueToken> findByServiceAndStatusOrderByCreatedAtAsc(BankServices service, TokenStatus status);
	
	// Get the latest waiting token for a service
	QueueToken findFirstByServiceAndStatusOrderByCreatedAtDesc(BankServices service, TokenStatus status);

}