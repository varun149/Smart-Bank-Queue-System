package com.luminar.service.queue;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.luminar.dto.queue.PastTokenDTO;
import com.luminar.dto.queue.ServiceQueueStatusDTO;
import com.luminar.dto.queue.TokenStatusViewDTO;
import com.luminar.entity.BankServices;
import com.luminar.entity.Customer;
import com.luminar.entity.QueueToken;
import com.luminar.entity.TokenStatus;
import com.luminar.repository.BankServicesRepository;
import com.luminar.repository.CustomerRepository;
import com.luminar.repository.QueueTokenRepository;

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

	@Override
	public void bookToken(String username, Long serviceId) {

		Customer customer = customerRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Customer not found"));
		BankServices service = bankServicesRepository.findById(serviceId)
				.orElseThrow(() -> new RuntimeException("Service not found"));

		String code = service.getCode();

		// Get last token for this service to increment sequence
		QueueToken lastToken = queueTokenRepository.findFirstByServiceAndStatusOrderByCreatedAtDesc(service,
				TokenStatus.WAITING);

		int nextSeq = (lastToken != null) ? Integer.parseInt(lastToken.getTokenNo().split("-")[1]) + 1 : 1;
		String tokenNo = code + "-" + String.format("%03d", nextSeq);

		// Save token
		queueTokenRepository.save(new QueueToken(tokenNo, service, customer, TokenStatus.WAITING));

		// Update Redis waiting count
		redisTemplate.opsForValue().increment("queue:" + code + ":waiting");
	}

	@Override
	public TokenStatusViewDTO getTokenStatusForCustomer(String username) {
		Customer customer = customerRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		// Only consider active(WAITING) tokens
		List<TokenStatus> activeStatuses = List.of(TokenStatus.WAITING, TokenStatus.SERVING);

		QueueToken latestToken = queueTokenRepository.findFirstByCustomerAndStatusInOrderByCreatedAtDesc(customer,
				activeStatuses);

		if (latestToken == null) {
			return null; // no active token
		}

		String serviceCode = latestToken.getService().getCode();

		// Currently serving token from Redis
		String currentlyServing = redisTemplate.opsForValue().get("queue:" + serviceCode + ":serving");

		// Waiting count from Redis
		String waitingStr = redisTemplate.opsForValue().get("queue:" + serviceCode + ":waiting");

		int waitingCount = (waitingStr != null) ? Integer.parseInt(waitingStr) : 0;

		return new TokenStatusViewDTO(latestToken.getService().getName(), // serviceName
				serviceCode, // serviceCode
				latestToken.getTokenNo(), // Token
				latestToken.getStatus().name(), // Status as String
				currentlyServing, // currentlyServingToken
				waitingCount // waitingCount
		);
	}

	@Override
	public List<PastTokenDTO> getRecentPastTokens(String username, int limit) {
		Customer customer = customerRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		// Only consider past tokens
		List<TokenStatus> pastStatuses = List.of(TokenStatus.COMPLETED, TokenStatus.SKIPPED);

		List<QueueToken> pastTokens = queueTokenRepository.findByCustomerAndStatusInOrderByCreatedAtDesc(customer,
				pastStatuses);

		List<PastTokenDTO> dtoList = new ArrayList<>();

		for (int i = 0; i < Math.min(limit, pastTokens.size()); i++) {
			QueueToken token = pastTokens.get(i);
			dtoList.add(new PastTokenDTO(token.getTokenNo(), token.getService().getName(), token.getStatus().name(),
					token.getCreatedAt()));
		}

		return dtoList;
	}

	@Override
	public ServiceQueueStatusDTO getServiceQueueStatus(Long serviceId) {
		BankServices service = bankServicesRepository.findById(serviceId)
				.orElseThrow(() -> new RuntimeException("Service not found"));

		String code = service.getCode();

		String currentToken = redisTemplate.opsForValue().get("queue:" + code + ":serving");

		String waiting = redisTemplate.opsForValue().get("queue:" + code + ":waiting");

		int waitingCount = (waiting == null) ? 0 : Integer.parseInt(waiting);

		return new ServiceQueueStatusDTO(service.getName(), code, currentToken, waitingCount);
	}

	@Override
	public void callNextToken(Long serviceId) {
		BankServices service = bankServicesRepository.findById(serviceId)
				.orElseThrow(() -> new RuntimeException("Service not found"));

		String code = service.getCode();

		String currentServing = redisTemplate.opsForValue().get("queue:" + code + ":serving");

		if (currentServing != null) {
			throw new RuntimeException("A token is already being served");
		}

		QueueToken nextToken = queueTokenRepository.findFirstByServiceAndStatusOrderByCreatedAtAsc(service,
				TokenStatus.WAITING);

		if (nextToken == null) {
			throw new RuntimeException("No waiting tokens");
		}

		// Update DB
		nextToken.setStatus(TokenStatus.SERVING);
		queueTokenRepository.save(nextToken);

		// Update Redis
		redisTemplate.opsForValue().set("queue:" + code + ":serving", nextToken.getTokenNo());

		redisTemplate.opsForValue().decrement("queue:" + code + ":waiting");

	}

	@Override
	public void completeCurrentToken(Long serviceId) {

		BankServices service = bankServicesRepository.findById(serviceId)
				.orElseThrow(() -> new RuntimeException("Service not found"));

		String code = service.getCode();

		String currentTokenNo = redisTemplate.opsForValue().get("queue:" + code + ":serving");

		if (currentTokenNo == null) {
			throw new RuntimeException("No token is currently being served");
		}

		QueueToken token = queueTokenRepository.findByTokenNo(currentTokenNo)
				.orElseThrow(() -> new RuntimeException("Token not found"));

		// Update DB
		token.setStatus(TokenStatus.COMPLETED);
		queueTokenRepository.save(token);

		// Clear Redis serving
		redisTemplate.delete("queue:" + code + ":serving");
	}

	@Override
	public void skipCurrentToken(Long serviceId) {

		BankServices service = bankServicesRepository.findById(serviceId)
				.orElseThrow(() -> new RuntimeException("Service not found"));

		String code = service.getCode();

		String currentTokenNo = redisTemplate.opsForValue().get("queue:" + code + ":serving");

		if (currentTokenNo == null) {
			throw new RuntimeException("No token is currently being served");
		}

		QueueToken token = queueTokenRepository.findByTokenNo(currentTokenNo)
				.orElseThrow(() -> new RuntimeException("Token not found"));

		// Update DB
		token.setStatus(TokenStatus.SKIPPED);
		queueTokenRepository.save(token);

		// Clear Redis serving
		redisTemplate.delete("queue:" + code + ":serving");
	}

}
