package com.luminar.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.luminar.entity.BankServices;
import com.luminar.repository.BankServicesRepository;

@Component
public class BankServiceSeeder implements CommandLineRunner {

    private final BankServicesRepository repo;

    public BankServiceSeeder(BankServicesRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {

        seed("Account Opening", "A");
        seed("Loan Enquiry", "L");
        seed("Cash Deposit", "D");
        seed("Cash Withdrawal", "W");
        seed("General Inquiry", "I");
    }

    private void seed(String name, String code) {
        if (!repo.existsByCode(code)) {
            repo.save(new BankServices(name, code));
        }
    }
}
