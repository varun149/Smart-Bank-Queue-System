package com.luminar.controller.queue;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.luminar.dto.queue.PastTokenDTO;
import com.luminar.dto.queue.TokenStatusViewDTO;
import com.luminar.service.queue.QueueTokenService;

@Controller
public class CustomerQueueController {
	 private final QueueTokenService queueTokenService;

	    public CustomerQueueController(QueueTokenService queueTokenService) {
	        this.queueTokenService = queueTokenService;
	    }

	    // Book Token
	    @PostMapping("/customer/book-token")
	    public String bookToken(@RequestParam Long serviceId, Principal principal) {

	        String username = principal.getName();
	        queueTokenService.bookToken(username, serviceId);

	        return "redirect:/customer/token-status";
	    }

	    //  Token Status Page
	    @GetMapping("/customer/token-status")
	    public String tokenStatus(Model model, Principal principal) {

	        String username = principal.getName();
	        TokenStatusViewDTO dto = queueTokenService.getTokenStatusForCustomer(username);

	        model.addAttribute("tokenStatus", dto);
	        return "customer/token-status";
	    }

	    // Past Appointments
	    @GetMapping("/customer/past-appointments")
	    public String pastAppointments(Model model, Principal principal) {

	        String username = principal.getName();
	        List<PastTokenDTO> pastTokens =
	                queueTokenService.getRecentPastTokens(username, 10);

	        model.addAttribute("pastTokens", pastTokens);
	        return "customer/past-appointments";
	    }

	
}
