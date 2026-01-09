package com.luminar.dto.queue;

import java.time.LocalDateTime;

import com.luminar.entity.TokenStatus;

public class StaffAppointmentDTO {

	private Long tokenId;
	private String tokenNo;
	private String customerName;
	private String ifscCode;
	private TokenStatus status;
	private LocalDateTime createdAt;

	public StaffAppointmentDTO(Long tokenId, String tokenNo, String customerName, String ifscCode, TokenStatus status,
			LocalDateTime createdAt) {
		this.tokenId = tokenId;
		this.tokenNo = tokenNo;
		this.customerName = customerName;
		this.ifscCode = ifscCode;
		this.status = status;
		this.createdAt = createdAt;
	}

	public Long getTokenId() {
		return tokenId;
	}

	public String getTokenNo() {
		return tokenNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public TokenStatus getStatus() {
		return status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
