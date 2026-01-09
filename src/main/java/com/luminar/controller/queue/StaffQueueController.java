package com.luminar.controller.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.luminar.entity.Staff;
import com.luminar.repository.StaffRepository;
import com.luminar.service.queue.QueueTokenService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/staff")
public class StaffQueueController {

	@Autowired
	private StaffRepository staffRepository;

	@Autowired
	private QueueTokenService queueTokenService;

	/* ================= PROFILE ================= */

	@GetMapping("/profile")
	public String viewProfile(HttpSession session, Model model) {

		Staff staff = (Staff) session.getAttribute("LOGGED_IN_STAFF");
		if (staff == null)
			return "redirect:/login";

		model.addAttribute("staff", staff);
		return "staff/staff-profile";
	}

	@PostMapping("/profile/update")
	public String updateProfile(HttpSession session, String fullName, String phoneNumber) {

		Staff staff = (Staff) session.getAttribute("LOGGED_IN_STAFF");
		if (staff == null)
			return "redirect:/login";

		staff.setFullName(fullName);
		staff.setPhoneNumber(phoneNumber);

		staffRepository.save(staff);
		session.setAttribute("LOGGED_IN_STAFF", staff);

		return "redirect:/staff/profile";
	}

	/* ================= CHANGE PASSWORD ================= */

	@GetMapping("/change-password")
	public String changePasswordPage(HttpSession session, Model model) {

		Staff staff = (Staff) session.getAttribute("LOGGED_IN_STAFF");
		if (staff == null)
			return "redirect:/login";

		model.addAttribute("staff", staff);
		return "staff/change-password";
	}

	@PostMapping("/change-password")
	public String changePassword(HttpSession session, String oldPassword, String newPassword, String confirmPassword,
			Model model) {

		Staff staff = (Staff) session.getAttribute("LOGGED_IN_STAFF");
		if (staff == null)
			return "redirect:/login";

		if (!staff.getPassword().equals(oldPassword)) {
			model.addAttribute("error", "Old password is incorrect");
			return "staff/change-password";
		}

		if (!newPassword.equals(confirmPassword)) {
			model.addAttribute("error", "Passwords do not match");
			return "staff/change-password";
		}

		staff.setPassword(newPassword);
		staffRepository.save(staff);

		return "redirect:/staff/profile";
	}

	/* ================= APPOINTMENTS ================= */

	@GetMapping("/appointments")
	public String viewTodayAppointments(HttpSession session, Model model) {

		Staff staff = (Staff) session.getAttribute("LOGGED_IN_STAFF");
		if (staff == null)
			return "redirect:/login";

		Long serviceId = (Long) session.getAttribute("SERVICE_ID");

		model.addAttribute("appointments", queueTokenService.getTodayAppointments(serviceId));

		model.addAttribute("queueStatus", queueTokenService.getServiceQueueStatus(serviceId));

		return "staff/appointments";
	}

	/* ================= QUEUE ACTIONS ================= */

	@PostMapping("/appointments/call-next")
	public String callNextToken(HttpSession session) {

		Staff staff = (Staff) session.getAttribute("LOGGED_IN_STAFF");
		if (staff == null)
			return "redirect:/login";

		Long serviceId = (Long) session.getAttribute("SERVICE_ID");
		queueTokenService.callNextToken(serviceId);

		return "redirect:/staff/appointments";
	}

	@PostMapping("/appointments/complete")
	public String completeToken(HttpSession session) {

		Staff staff = (Staff) session.getAttribute("LOGGED_IN_STAFF");
		if (staff == null)
			return "redirect:/login";

		Long serviceId = (Long) session.getAttribute("SERVICE_ID");
		queueTokenService.completeCurrentToken(serviceId);

		return "redirect:/staff/appointments";
	}

	@PostMapping("/appointments/skip")
	public String skipToken(HttpSession session) {

		Staff staff = (Staff) session.getAttribute("LOGGED_IN_STAFF");
		if (staff == null)
			return "redirect:/login";

		Long serviceId = (Long) session.getAttribute("SERVICE_ID");
		queueTokenService.skipCurrentToken(serviceId);

		return "redirect:/staff/appointments";
	}
}
