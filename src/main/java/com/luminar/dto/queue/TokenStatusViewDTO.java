package com.luminar.dto.queue;

public class TokenStatusViewDTO {
	 private String serviceName;
	    private String serviceCode;

	    private String yourToken;
	    private String yourStatus;

	    private String currentlyServingToken;
	    private int waitingCount;

	    public TokenStatusViewDTO() {}

		public TokenStatusViewDTO(String serviceName, String serviceCode, String yourToken, String yourStatus,
				String currentlyServingToken, int waitingCount) {
			super();
			this.serviceName = serviceName;
			this.serviceCode = serviceCode;
			this.yourToken = yourToken;
			this.yourStatus = yourStatus;
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

		public String getYourToken() {
			return yourToken;
		}

		public void setYourToken(String yourToken) {
			this.yourToken = yourToken;
		}

		public String getYourStatus() {
			return yourStatus;
		}

		public void setYourStatus(String yourStatus) {
			this.yourStatus = yourStatus;
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
