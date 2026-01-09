package com.luminar.service.queue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.luminar.dto.queue.PastTokenDTO;
import com.luminar.dto.queue.ServiceQueueStatusDTO;
import com.luminar.dto.queue.StaffAppointmentDTO;
import com.luminar.dto.queue.TokenStatusViewDTO;
import com.luminar.entity.BankServices;
import com.luminar.entity.Customer;
import com.luminar.entity.QueueToken;
import com.luminar.entity.TokenStatus;
import com.luminar.repository.BankServicesRepository;
import com.luminar.repository.CustomerRepository;
import com.luminar.repository.QueueTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class QueueTokenServiceImpl implements QueueTokenService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private BankServicesRepository bankServicesRepository;

	@Autowired
	private QueueTokenRepository queueTokenRepository;

	@Autowired
	private CustomerRepository customerRepository;

	// ========================= BOOK TOKEN =========================
	@Override
	@Transactional
	public void bookToken(String username, Long serviceId) {

	    Customer customer = customerRepository.findByUsername(username)
	            .orElseThrow(() -> new RuntimeException("Customer not found"));

	    BankServices service = bankServicesRepository.findById(serviceId)
	            .orElseThrow(() -> new RuntimeException("Service not found"));

	    // Prevent double booking (DB truth)
	    boolean alreadyBooked = queueTokenRepository.existsByCustomerAndServiceAndStatusIn(customer, service,
	            List.of(TokenStatus.WAITING, TokenStatus.SERVING));

	    if (alreadyBooked) {
	        throw new RuntimeException("You already have an active token");
	    }

	    // 🔒 Get last token for this service, regardless of status
	    QueueToken lastToken = queueTokenRepository.findTopByServiceOrderByCreatedAtDesc(service);

	    int nextSeq = 1;

	    if (lastToken != null) {
	        String[] parts = lastToken.getTokenNo().split("-");
	        nextSeq = Integer.parseInt(parts[1]) + 1;
	    }

	    String tokenNo = service.getCode() + "-" + String.format("%03d", nextSeq);

	    QueueToken token = new QueueToken(tokenNo, service, customer, TokenStatus.WAITING);

	    queueTokenRepository.save(token);

	    // 📊 Redis counter (cache only)
	    redisTemplate.opsForValue().increment("queue:" + service.getCode() + ":waiting");
	}

	// ========================= CUSTOMER TOKEN STATUS =========================
	@Override
	public TokenStatusViewDTO getTokenStatusForCustomer(String username) {

		Customer customer = customerRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		QueueToken latestToken = queueTokenRepository.findFirstByCustomerAndStatusInOrderByCreatedAtDesc(customer,
				List.of(TokenStatus.WAITING, TokenStatus.SERVING));

		if (latestToken == null) {
			return null;
		}

		String serviceCode = latestToken.getService().getCode();

		String currentlyServing = redisTemplate.opsForValue().get("queue:" + serviceCode + ":serving");

		String waitingStr = redisTemplate.opsForValue().get("queue:" + serviceCode + ":waiting");

		int waitingCount = waitingStr != null ? Integer.parseInt(waitingStr) : 0;

		return new TokenStatusViewDTO(latestToken.getService().getName(), serviceCode, latestToken.getTokenNo(),
				latestToken.getStatus().name(), currentlyServing, waitingCount);
	}

	// ========================= PAST TOKENS =========================
	@Override
	public List<PastTokenDTO> getRecentPastTokens(String username, int limit) {

		Customer customer = customerRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		List<QueueToken> pastTokens = queueTokenRepository.findByCustomerAndStatusInOrderByCreatedAtDesc(customer,
				List.of(TokenStatus.COMPLETED, TokenStatus.SKIPPED));

		List<PastTokenDTO> result = new ArrayList<>();

		for (int i = 0; i < Math.min(limit, pastTokens.size()); i++) {
			QueueToken t = pastTokens.get(i);
			result.add(
					new PastTokenDTO(t.getTokenNo(), t.getService().getName(), t.getStatus().name(), t.getCreatedAt()));
		}

		return result;
	}

	// ========================= SERVICE QUEUE STATUS =========================
	@Override
	public ServiceQueueStatusDTO getServiceQueueStatus(Long serviceId) {

		BankServices service = bankServicesRepository.findById(serviceId)
				.orElseThrow(() -> new RuntimeException("Service not found"));

		String code = service.getCode();

		String currentToken = redisTemplate.opsForValue().get("queue:" + code + ":serving");

		String waiting = redisTemplate.opsForValue().get("queue:" + code + ":waiting");

		int waitingCount = waiting != null ? Integer.parseInt(waiting) : 0;

		return new ServiceQueueStatusDTO(service.getName(), code, currentToken, waitingCount);
	}

	// ========================= CALL NEXT TOKEN =========================
	@Override
	@Transactional
	public void callNextToken(Long serviceId) {
	    BankServices service = bankServicesRepository.findById(serviceId)
	            .orElseThrow(() -> new RuntimeException("Service not found"));

	    String code = service.getCode();

	    // Check if a token is already being served
	    QueueToken servingToken = queueTokenRepository.findFirstByServiceAndStatusOrderByCreatedAtAsc(service, TokenStatus.SERVING);
	    if (servingToken != null) {
	        throw new RuntimeException("A token is already being served");
	    }

	    // Pick the next waiting token
	    QueueToken nextToken = queueTokenRepository.findFirstByServiceAndStatusOrderByCreatedAtAsc(service, TokenStatus.WAITING);
	    if (nextToken == null) {
	        throw new RuntimeException("No waiting tokens");
	    }

	    nextToken.setStatus(TokenStatus.SERVING);
	    queueTokenRepository.save(nextToken);

	    // Update Redis
	    redisTemplate.opsForValue().set("queue:" + code + ":serving", nextToken.getTokenNo());
	    redisTemplate.opsForValue().decrement("queue:" + code + ":waiting");
	}

	// ========================= COMPLETE TOKEN =========================
	@Override
	@Transactional
	public void completeCurrentToken(Long serviceId) {
	    BankServices service = bankServicesRepository.findById(serviceId)
	            .orElseThrow(() -> new RuntimeException("Service not found"));
	    String code = service.getCode();

	    // Fetch token currently being served
	    QueueToken token = queueTokenRepository.findFirstByServiceAndStatusOrderByCreatedAtAsc(service, TokenStatus.SERVING);
	    if (token == null) {
	        throw new RuntimeException("No token is currently being served");
	    }

	    token.setStatus(TokenStatus.COMPLETED);
	    queueTokenRepository.save(token);

	    redisTemplate.delete("queue:" + code + ":serving");
	}

	// ========================= SKIP TOKEN =========================
	@Override
	@Transactional
	public void skipCurrentToken(Long serviceId) {
	    BankServices service = bankServicesRepository.findById(serviceId)
	            .orElseThrow(() -> new RuntimeException("Service not found"));
	    String code = service.getCode();

	    // Fetch token currently being served
	    QueueToken token = queueTokenRepository.findFirstByServiceAndStatusOrderByCreatedAtAsc(service, TokenStatus.SERVING);
	    if (token == null) {
	        throw new RuntimeException("No token is currently being served");
	    }

	    token.setStatus(TokenStatus.SKIPPED);
	    queueTokenRepository.save(token);

	    redisTemplate.delete("queue:" + code + ":serving");
	}



	// ========================= STAFF APPOINTMENTS =========================
	@Override
	public List<StaffAppointmentDTO> getTodayAppointments(Long serviceId) {

		LocalDate today = LocalDate.now();

		return queueTokenRepository.findTodayAppointments(serviceId, today.atStartOfDay(),
				today.plusDays(1).atStartOfDay());
	}
}
