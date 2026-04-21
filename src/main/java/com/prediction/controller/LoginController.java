package com.prediction.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.prediction.entity.User;
import com.prediction.repository.UserRepository;

@Controller
public class LoginController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/")
	public String homePage() {
		return "home"; // your new UI page
	}

	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}

	@PostMapping("/login")
	public String login(@RequestParam String email, @RequestParam String password, Model model) {

		User user = userRepository.findByEmail(email);

		if (user == null || !user.getPassword().equals(password)) {
			model.addAttribute("error", "Invalid credentials");
			return "login";
		}

		if (user.getRole().equals("STUDENT")) {
			return "redirect:/student/dashboard";
		} else if (user.getRole().equals("TEACHER")) {
			return "redirect:/teacher/dashboard";
		} else {
			return "redirect:/admin/dashboard";
		}
	}
}
