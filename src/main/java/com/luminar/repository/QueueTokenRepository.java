package com.luminar.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.luminar.dto.queue.StaffAppointmentDTO;
import com.luminar.entity.BankServices;
import com.luminar.entity.Customer;
import com.luminar.entity.QueueToken;
import com.luminar.entity.TokenStatus;

import jakarta.persistence.LockModeType;

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

	// REQUIRED for complete / skip token flow
	Optional<QueueToken> findByTokenNo(String tokenNo);

	// REQUIRED for fetching TODAY'S appointments for each staff service (portable
	// JPQL)
	@Query("""
			    SELECT new com.luminar.dto.queue.StaffAppointmentDTO(
			        t.id,
			        t.tokenNo,
			        c.fullName,
			        c.ifscCode,
			        t.status,
			        t.createdAt
			    )
			    FROM QueueToken t
			    JOIN t.customer c
			    WHERE t.service.id = :serviceId
			      AND t.createdAt >= :start
			      AND t.createdAt < :end
			    ORDER BY t.createdAt ASC
			""")
	List<StaffAppointmentDTO> findTodayAppointments(@Param("serviceId") Long serviceId,
			@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	// Count how many tokens are created TODAY for a service
	@Query("""
			    SELECT COUNT(t)
			    FROM QueueToken t
			    WHERE t.service.id = :serviceId
			      AND t.createdAt >= :start
			      AND t.createdAt < :end
			""")
	long countTodayTokens(@Param("serviceId") Long serviceId, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

	boolean existsByCustomerAndServiceAndStatusIn(Customer customer, BankServices service, List<TokenStatus> statuses);

	QueueToken findFirstByServiceAndCreatedAtBetweenOrderByCreatedAtDesc(BankServices service, LocalDateTime start,
			LocalDateTime end);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	QueueToken findFirstByServiceAndStatusInOrderByCreatedAtDesc(BankServices service, List<TokenStatus> statuses);
	
	// Get the last token of any status for a service (for sequence increment)
	QueueToken findTopByServiceOrderByCreatedAtDesc(BankServices service);


}
