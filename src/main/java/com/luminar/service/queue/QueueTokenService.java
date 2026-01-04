package com.luminar.service.queue;

import java.util.List;

import com.luminar.dto.queue.PastTokenDTO;
import com.luminar.dto.queue.ServiceQueueStatusDTO;
import com.luminar.dto.queue.TokenStatusViewDTO;

public interface QueueTokenService {
	/* =========================
    CUSTOMER OPERATIONS
    ========================= */

 /**
  * Book a new token for a customer for a given service.
  * One active token (WAITING / SERVING) per customer per service.
  */
 void bookToken(String username, Long serviceId);

 /**
  * Get live token status for the logged-in customer.
  * Used in customer dashboard "Token Status" page.
  */
 TokenStatusViewDTO getTokenStatusForCustomer(String username);

 /**
  * Get recent past tokens (COMPLETED / SKIPPED) for customer.
  * Limit controls how many records to return.
  */
 List<PastTokenDTO> getRecentPastTokens(String username, int limit);



 /* =========================
    STAFF OPERATIONS
    ========================= */

 /**
  * Get current queue status for a service
  * (current serving + waiting count).
  */
 ServiceQueueStatusDTO getServiceQueueStatus(Long serviceId);

 /**
  * Call next token for the service assigned to staff.
  * Moves token WAITING → SERVING.
  */
 void callNextToken(Long serviceId);

 /**
  * Mark currently serving token as COMPLETED.
  */
 void completeCurrentToken(Long serviceId);

 /**
  * Mark currently serving token as SKIPPED.
  */
 void skipCurrentToken(Long serviceId);
}
