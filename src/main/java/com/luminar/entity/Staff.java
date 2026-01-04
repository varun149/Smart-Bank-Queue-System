package com.luminar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "staff")
public class Staff {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "full_name", nullable = false)
	private String fullName;

	@Column(name = "bank_id", nullable = false, unique = true)
	private String bankIdNo;

	@Column(name = "phone_number", nullable = false, unique = true)
	private String phoneNumber;

	@Column(name = "aadhar_number", nullable = false, unique = true, length = 12)
	private String aadharNumber;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@ManyToOne
	@JoinColumn(name = "service_id", nullable = false)
	private BankServices service;

	public Staff() {
	}

	public Staff(Long id, String fullName, String bankIdNo, String phoneNumber, String aadharNumber, String username,
			String password, BankServices service) {
		super();
		this.id = id;
		this.fullName = fullName;
		this.bankIdNo = bankIdNo;
		this.phoneNumber = phoneNumber;
		this.aadharNumber = aadharNumber;
		this.username = username;
		this.password = password;
		this.service = service;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getBankIdNo() {
		return bankIdNo;
	}

	public void setBankIdNo(String bankIdNo) {
		this.bankIdNo = bankIdNo;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public BankServices getService() {
		return service;
	}

	public void setService(BankServices service) {
		this.service = service;
	}

}
