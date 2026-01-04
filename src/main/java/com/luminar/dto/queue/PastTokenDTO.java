package com.luminar.dto.queue;

import java.time.LocalDateTime;

public class PastTokenDTO {
	private String tokenNo;
    private String serviceName;
    private String status;
    private LocalDateTime createdAt;

    public PastTokenDTO() {}

	public PastTokenDTO(String tokenNo, String serviceName, String status, LocalDateTime createdAt) {
		this.tokenNo = tokenNo;
		this.serviceName = serviceName;
		this.status = status;
		this.createdAt = createdAt;
	}

	public String getTokenNo() {
		return tokenNo;
	}

	public void setTokenNo(String tokenNo) {
		this.tokenNo = tokenNo;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

    
}
