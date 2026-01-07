package com.luminar.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.luminar.entity.Customer;
import com.luminar.repository.CustomerRepository;

@Controller
public class CustomerAuthController {

	@Autowired
	private CustomerRepository customerRepo;

	@GetMapping("/customer/signup")
	public String customerSignupPage(Model model) {
		model.addAttribute("customer", new Customer());
		return "customer-signup";
	}

	@PostMapping("/customer/signup")
	public String registerCustomer(@ModelAttribute Customer customer, Model model) {

		if (customerRepo.findByUsername(customer.getUsername()).isPresent()) {
			model.addAttribute("error", "Username already exists");
			return "customer-signup";
		}

		customerRepo.save(customer);
		return "redirect:/login";
	}

}
