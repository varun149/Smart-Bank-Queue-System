package com.luminar.controller.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.luminar.entity.Customer;
import com.luminar.repository.CustomerRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer")
public class CustomerQueueController {

	@Autowired
	private CustomerRepository customerRepository;

	// ================= PROFILE VIEW =================
	@GetMapping("/profile")
	public String viewProfile(HttpSession session, Model model) {

		Customer customer = (Customer) session.getAttribute("LOGGED_IN_CUSTOMER");
		if (customer == null) {
			return "redirect:/login";
		}

		model.addAttribute("customer", customer);
		return "customer/customer-profile"; // FULL PAGE
	}

	// ================= PROFILE UPDATE =================
	@PostMapping("/profile/update")
	public String updateProfile(HttpSession session, @RequestParam String fullName, @RequestParam String phoneNumber,
			@RequestParam String ifscCode) {

		Customer customer = (Customer) session.getAttribute("LOGGED_IN_CUSTOMER");
		if (customer == null) {
			return "redirect:/login";
		}

		customer.setFullName(fullName);
		customer.setPhoneNumber(phoneNumber);
		customer.setIfscCode(ifscCode);

		customerRepository.save(customer);
		session.setAttribute("LOGGED_IN_CUSTOMER", customer);

		return "redirect:/customer/profile";
	}

	// ================= CHANGE PASSWORD PAGE =================
	@GetMapping("/change-password")
	public String changePasswordPage(HttpSession session, Model model) {

		Customer customer = (Customer) session.getAttribute("LOGGED_IN_CUSTOMER");
		if (customer == null) {
			return "redirect:/login";
		}

		model.addAttribute("customer", customer);
		return "customer/change-password"; // FULL PAGE
	}

	// ================= CHANGE PASSWORD SUBMIT =================
	@PostMapping("/change-password")
	public String changePassword(HttpSession session, @RequestParam String oldPassword,
			@RequestParam String newPassword, @RequestParam String confirmPassword, Model model) {

		Customer customer = (Customer) session.getAttribute("LOGGED_IN_CUSTOMER");
		if (customer == null) {
			return "redirect:/login";
		}

		if (!customer.getPassword().equals(oldPassword)) {
			model.addAttribute("error", "Old password is incorrect");
			return "customer/change-password";
		}

		if (!newPassword.equals(confirmPassword)) {
			model.addAttribute("error", "Passwords do not match");
			return "customer/change-password";
		}

		customer.setPassword(newPassword);
		customerRepository.save(customer);

		return "redirect:/customer/profile";
	}
}
