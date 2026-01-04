package com.luminar.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luminar.entity.Customer;
import com.luminar.entity.Staff;
import com.luminar.repository.CustomerRepository;
import com.luminar.repository.StaffRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class AuthService {
	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private StaffRepository staffRepository;

	public String authenticate(String username, String password, HttpSession session) {

		Customer customer = customerRepository.findByUsername(username).orElse(null);

		if (customer != null && customer.getPassword().equals(password)) {

			session.setAttribute("USER_ID", customer.getId());
			session.setAttribute("ROLE", "CUSTOMER");

			return "/customer/dashboard";
		}

		Staff staff = staffRepository.findByUsername(username).orElse(null);

		if (staff != null && staff.getPassword().equals(password)) {

			session.setAttribute("USER_ID", staff.getId());
			session.setAttribute("ROLE", "STAFF");
			session.setAttribute("SERVICE_ID", staff.getService().getId());

			return "/staff/dashboard";
		}

		return null;
	}
}
