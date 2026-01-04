package com.luminar.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.luminar.dto.LoginDto;
import com.luminar.service.auth.AuthService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

	@Autowired
	private AuthService authService;
	
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
	@GetMapping("/login")
	public String loginPage(Model model) {
		model.addAttribute("loginDto", new LoginDto());
		return "login"; 
	}

	@PostMapping("/login")
	public String login(@ModelAttribute("loginDto") LoginDto loginDto, HttpSession session, Model model) {

		String redirectUrl = authService.authenticate(loginDto.getUsername(), loginDto.getPassword(), session);

		if (redirectUrl == null) {
			model.addAttribute("error", "Invalid username or password");
			return "login";
		}

		return "redirect:" + redirectUrl;
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}
}
