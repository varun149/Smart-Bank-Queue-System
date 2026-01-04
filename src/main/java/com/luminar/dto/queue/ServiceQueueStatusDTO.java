package com.luminar.dto.queue;

public class ServiceQueueStatusDTO {
	private String serviceName;
	private String serviceCode;
	private String currentlyServingToken;
	private int waitingCount;

	public ServiceQueueStatusDTO() {
	}

	public ServiceQueueStatusDTO(String serviceName, String serviceCode, String currentlyServingToken,
			int waitingCount) {
		super();
		this.serviceName = serviceName;
		this.serviceCode = serviceCode;
		this.currentlyServingToken = currentlyServingToken;
		this.waitingCount = waitingCount;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getCurrentlyServingToken() {
		return currentlyServingToken;
	}

	public void setCurrentlyServingToken(String currentlyServingToken) {
		this.currentlyServingToken = currentlyServingToken;
	}

	public int getWaitingCount() {
		return waitingCount;
	}

	public void setWaitingCount(int waitingCount) {
		this.waitingCount = waitingCount;
	}

}
