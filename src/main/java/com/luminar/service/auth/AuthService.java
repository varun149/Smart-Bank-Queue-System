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

//	public String authenticate(String username, String password, HttpSession session) {
//
//		Customer customer = customerRepository.findByUsername(username).orElse(null);
//
//		if (customer != null && customer.getPassword().equals(password)) {
//
//			session.setAttribute("USER_ID", customer.getId());
//			session.setAttribute("ROLE", "CUSTOMER");
//
//			return "/customer/dashboard";
//		}
//
//		Staff staff = staffRepository.findByUsername(username).orElse(null);
//
//		if (staff != null && staff.getPassword().equals(password)) {
//
//			session.setAttribute("USER_ID", staff.getId());
//			session.setAttribute("ROLE", "STAFF");
//			session.setAttribute("SERVICE_ID", staff.getService().getId());
//
//			return "/staff/dashboard";
//		}
//
//		return null;
//	}
	public String authenticate(String username, String password, HttpSession session) {

	    // --- CUSTOMER LOGIN ---
	    Customer customer = customerRepository.findByUsername(username).orElse(null);

	    if (customer != null && customer.getPassword().equals(password)) {
	        // store the full customer object in session
	        session.setAttribute("LOGGED_IN_CUSTOMER", customer);
	        session.setAttribute("ROLE", "CUSTOMER");

	        // redirect to profile page which exists
	        return "/customer/profile";
	    }

	    // --- STAFF LOGIN ---
	    Staff staff = staffRepository.findByUsername(username).orElse(null);

	    if (staff != null && staff.getPassword().equals(password)) {
	        // store full staff object in session
	        session.setAttribute("LOGGED_IN_STAFF", staff);
	        session.setAttribute("ROLE", "STAFF");
	        session.setAttribute("SERVICE_ID", staff.getService().getId());

	        // redirect to staff dashboard page (make sure this mapping exists)
	        return "/staff/dashboard";
	    }

	    // --- INVALID LOGIN ---
	    return null;
	}

}
