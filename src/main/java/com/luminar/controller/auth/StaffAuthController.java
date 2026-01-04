package com.luminar.controller.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.luminar.entity.BankServices;
import com.luminar.entity.Staff;
import com.luminar.repository.BankServicesRepository;
import com.luminar.repository.StaffRepository;

@Controller
public class StaffAuthController {

	@Autowired
	private StaffRepository staffRepo;

	@Autowired
	private BankServicesRepository serviceRepo;

	@GetMapping("/staff/signup")
	public String staffSignupPage(Model model) {
		model.addAttribute("staff", new Staff());
		List<BankServices> services = serviceRepo.findAll();
		model.addAttribute("services", services);
		return "staff-signup";
	}

	@PostMapping("/staff/signup")
	public String registerStaff(@ModelAttribute Staff staff) {
		staffRepo.save(staff);
		return "redirect:/login";
	}
}
