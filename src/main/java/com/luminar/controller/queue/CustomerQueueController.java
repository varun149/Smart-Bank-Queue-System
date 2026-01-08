package com.luminar.controller.queue;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.luminar.dto.queue.PastTokenDTO;
import com.luminar.dto.queue.TokenStatusViewDTO;
import com.luminar.entity.Customer;
import com.luminar.repository.BankServicesRepository;
import com.luminar.repository.CustomerRepository;
import com.luminar.service.queue.QueueTokenService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer")
public class CustomerQueueController {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private QueueTokenService queueTokenService;

	@Autowired
	private BankServicesRepository bankServicesRepository;

	// ================= PROFILE VIEW =================
	@GetMapping("/profile")
	public String viewProfile(HttpSession session, Model model) {

		Customer customer = (Customer) session.getAttribute("LOGGED_IN_CUSTOMER");
		if (customer == null) {
			return "redirect:/login";
		}

		model.addAttribute("customer", customer);
		return "customer/customer-profile";
	}

	// ================= PROFILE UPDATE =================
	@PostMapping("/profile/update")
	public String updateProfile(HttpSession session,
	                            @RequestParam String fullName,
	                            @RequestParam String phoneNumber,
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
		return "customer/change-password";
	}

	// ================= CHANGE PASSWORD SUBMIT =================
	@PostMapping("/change-password")
	public String changePassword(HttpSession session,
	                             @RequestParam String oldPassword,
	                             @RequestParam String newPassword,
	                             @RequestParam String confirmPassword,
	                             Model model) {

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

	// ================= BOOK TOKEN PAGE =================
	@GetMapping("/book-token")
	public String bookTokenPage(HttpSession session, Model model) {

	    Customer customer = (Customer) session.getAttribute("LOGGED_IN_CUSTOMER");
	    if (customer == null) {
	        return "redirect:/login";
	    }

	    // Data required to render the form
	    model.addAttribute("services", bankServicesRepository.findAll());

	    return "customer/book-token";   // GET renders page
	}


	// ================= BOOK TOKEN SUBMIT =================
	@PostMapping("/book-token")
	public String bookToken(HttpSession session,
	                        @RequestParam("serviceId") Long serviceId) {

	    Customer customer = (Customer) session.getAttribute("LOGGED_IN_CUSTOMER");
	    if (customer == null) {
	        return "redirect:/login";
	    }

	    // Business logic only — NO rendering
	    queueTokenService.bookToken(customer.getUsername(), serviceId);

	    // PRG pattern (Post → Redirect → Get)
	    return "redirect:/customer/token-status";
	}

	// ================= TOKEN STATUS =================
	@GetMapping("/token-status")
	public String viewTokenStatus(HttpSession session, Model model) {

		Customer customer = (Customer) session.getAttribute("LOGGED_IN_CUSTOMER");
		if (customer == null) {
			return "redirect:/login";
		}

		TokenStatusViewDTO status =
				queueTokenService.getTokenStatusForCustomer(customer.getUsername());

		model.addAttribute("tokenStatus", status);
		return "customer/token-status";
	}

	// ================= PAST APPOINTMENTS =================
	@GetMapping("/past-appointments")
	public String pastAppointments(HttpSession session, Model model) {

		Customer customer = (Customer) session.getAttribute("LOGGED_IN_CUSTOMER");
		if (customer == null) {
			return "redirect:/login";
		}

		List<PastTokenDTO> pastTokens =
				queueTokenService.getRecentPastTokens(customer.getUsername(), 20);

		model.addAttribute("pastTokens", pastTokens);
		return "customer/past-appointments";
	}
}
