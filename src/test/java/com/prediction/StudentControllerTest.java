package com.prediction;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.prediction.controller.StudentController;
import com.prediction.service.MlService;

@WebMvcTest
@Import({ StudentController.class, StudentControllerTest.TestConfig.class })
public class StudentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MlService mlService;

	// ✅ MANUAL MOCK CONFIG
	@Configuration
	static class TestConfig {
		@Bean
		public MlService mlService() {
			return Mockito.mock(MlService.class);
		}
	}

	@Test
	public void testDashboard() throws Exception {
		mockMvc.perform(get("/student/dashboard")).andExpect(status().isOk());
	}

	@Test
	public void testPredict() throws Exception {

		Map<String, Object> result = new HashMap<>();
		result.put("pass_fail", "Pass");
		result.put("risk_level", "Low");
		result.put("predicted_cgpa", "8.5");
		result.put("reason", "Good");
		result.put("suggestion", "Keep it up");

		Map<String, Double> explanation = new HashMap<>();
		explanation.put("attendance", 0.8);
		result.put("explanation", explanation);

		when(mlService.predict(anyMap())).thenReturn(result);

		mockMvc.perform(post("/student/predict").param("attendance_percent", "85").param("internal_marks", "75")
				.param("assignment_score", "80").param("quiz_score", "70").param("study_hours_per_day", "3")
				.param("previous_cgpa", "7.5").param("backlogs", "0").param("classParticipation", "Good")
				.param("submissionRegular", "Yes")).andExpect(status().isOk());
	}
}