package com.luminar.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "queue_tokens")
public class QueueToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // eg : L-001
    @Column(name = "token_no", nullable = false, unique = true)
    private String tokenNo;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private BankServices service;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public QueueToken() {
    }

    public QueueToken(String tokenNo, BankServices service, Customer customer, TokenStatus status) {
        this.tokenNo = tokenNo;
        this.service = service;
        this.customer = customer;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // getters & setters
    public Long getId() {
        return id;
    }

    public String getTokenNo() {
        return tokenNo;
    }

    public BankServices getService() {
        return service;
    }

    public Customer getCustomer() {
        return customer;
    }

    public TokenStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setStatus(TokenStatus status) {
        this.status = status;
    }
}
