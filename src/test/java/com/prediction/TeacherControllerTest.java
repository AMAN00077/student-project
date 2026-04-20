package com.prediction;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.prediction.controller.TeacherController;
import com.prediction.entity.StudentRecord;
import com.prediction.repository.StudentRecordRepository;
import com.prediction.service.MlService;

@WebMvcTest(TeacherController.class)
public class TeacherControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private StudentRecordRepository recordRepository;

	@MockitoBean
	private MlService mlService;

	// ===========================
	// ✅ 1. DASHBOARD
	// ===========================
	@Test
	public void testDashboard() throws Exception {

		when(recordRepository.findAll()).thenReturn(new ArrayList<>());

		mockMvc.perform(get("/teacher/dashboard")).andExpect(status().isOk())
				.andExpect(view().name("teacher-dashboard")).andExpect(model().attributeExists("records"));
	}

	// ===========================
	// ✅ 2. ADD NEW RECORD
	// ===========================
	@Test
	public void testAddRecord() throws Exception {

		when(recordRepository.findByStudentId("1")).thenReturn(null);

		mockMvc.perform(post("/teacher/add").param("studentId", "1") // ✅ FIXED
				.param("attendance", "80").param("internalMarks", "70").param("assignmentScore", "75")
				.param("quizScore", "60").param("studyHours", "3").param("previousCgpa", "7.5").param("backlogs", "0")
				.param("classParticipation", "Good").param("submissionRegular", "Yes"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/teacher/dashboard"));
	}

	// ===========================
	// ✅ 3. DELETE RECORD
	// ===========================
	@Test
	public void testDeleteRecord() throws Exception {

		doNothing().when(recordRepository).deleteByStudentId("1"); // ✅ FIXED

		mockMvc.perform(post("/teacher/delete").param("studentId", "1")) // ✅ FIXED
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/teacher/dashboard"));
	}

	// ===========================
	// ✅ 4. PREDICT STUDENT SUCCESS
	// ===========================
	@Test
	public void testPredictStudentSuccess() throws Exception {

		StudentRecord record = new StudentRecord();
		record.setStudentId(1L); // ✅ FIXED TYPE
		record.setAttendance(80);
		record.setInternalMarks(70);
		record.setAssignmentScore(75);
		record.setQuizScore(60);
		record.setStudyHours(3);
		record.setPreviousCgpa(7.5);
		record.setBacklogs(0);
		record.setClassParticipation("Good");
		record.setSubmissionRegular("Yes");

		when(recordRepository.findByStudentId("1")).thenReturn(record);

		Map<String, Object> result = new HashMap<>();
		result.put("pass_fail", "Pass");
		result.put("risk_level", "Low");
		result.put("predicted_cgpa", "8.0");
		result.put("reason", "Good");
		result.put("suggestion", "Keep it up");

		when(mlService.predict(any())).thenReturn(result);

		mockMvc.perform(post("/teacher/predict").param("studentId", "1")).andExpect(status().isOk())
				.andExpect(view().name("teacher-prediction-result"))
				.andExpect(model().attributeExists("predictedCgpa"));
	}

	// ===========================
	// ✅ 5. PREDICT STUDENT NOT FOUND
	// ===========================
	@Test
	public void testPredictStudentNotFound() throws Exception {

		when(recordRepository.findByStudentId("1")).thenReturn(null);

		mockMvc.perform(post("/teacher/predict").param("studentId", "1")).andExpect(status().isOk())
				.andExpect(view().name("teacher-dashboard")).andExpect(model().attributeExists("error"));
	}

	// ===========================
	// ✅ 6. DOWNLOAD RECORDS
	// ===========================
	@Test
	public void testDownloadRecords() throws Exception {

		when(recordRepository.findAll()).thenReturn(new ArrayList<>());

		mockMvc.perform(get("/teacher/download-records")).andExpect(status().isOk());
	}

	// ===========================
	// ✅ 7. DOWNLOAD PREDICTIONS
	// ===========================
	@Test
	public void testDownloadPredictions() throws Exception {

		List<Map<String, Object>> predictions = new ArrayList<>();

		Map<String, Object> row = new HashMap<>();
		row.put("studentId", "1");
		row.put("attendance", 80);
		row.put("pass_fail", "Pass");
		row.put("risk", "Low");
		row.put("cgpa", "8.0");
		row.put("participation", "Good");
		row.put("submission", "Yes");

		predictions.add(row);

		mockMvc.perform(post("/teacher/download-predictions").sessionAttr("predictions", predictions))
				.andExpect(status().isOk());
	}
}