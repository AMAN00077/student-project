package com.prediction;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.prediction.controller.LoginController;
import com.prediction.entity.User;
import com.prediction.repository.UserRepository;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserRepository userRepository;

	// ===========================
	// ✅ 1. LOAD LOGIN PAGE
	// ===========================
	@Test
	public void testLoginPage() throws Exception {

		mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("login"));
	}

	// ===========================
	// ✅ 2. INVALID LOGIN
	// ===========================
	@Test
	public void testInvalidLogin() throws Exception {

		when(userRepository.findByEmail("test@gmail.com")).thenReturn(null);

		mockMvc.perform(post("/login").param("email", "test@gmail.com").param("password", "123"))
				.andExpect(status().isOk()).andExpect(view().name("login")).andExpect(model().attributeExists("error"));
	}

	// ===========================
	// ✅ 3. STUDENT LOGIN
	// ===========================
	@Test
	public void testStudentLogin() throws Exception {

		User user = new User();
		user.setEmail("student@gmail.com");
		user.setPassword("123");
		user.setRole("STUDENT");

		when(userRepository.findByEmail("student@gmail.com")).thenReturn(user);

		mockMvc.perform(post("/login").param("email", "student@gmail.com").param("password", "123"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/student/dashboard"));
	}

	// ===========================
	// ✅ 4. TEACHER LOGIN
	// ===========================
	@Test
	public void testTeacherLogin() throws Exception {

		User user = new User();
		user.setEmail("teacher@gmail.com");
		user.setPassword("123");
		user.setRole("TEACHER");

		when(userRepository.findByEmail("teacher@gmail.com")).thenReturn(user);

		mockMvc.perform(post("/login").param("email", "teacher@gmail.com").param("password", "123"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/teacher/dashboard"));
	}

	// ===========================
	// ✅ 5. ADMIN LOGIN
	// ===========================
	@Test
	public void testAdminLogin() throws Exception {

		User user = new User();
		user.setEmail("admin@gmail.com");
		user.setPassword("123");
		user.setRole("ADMIN");

		when(userRepository.findByEmail("admin@gmail.com")).thenReturn(user);

		mockMvc.perform(post("/login").param("email", "admin@gmail.com").param("password", "123"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/dashboard"));
	}
}