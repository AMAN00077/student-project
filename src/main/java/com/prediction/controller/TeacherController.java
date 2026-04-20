package com.prediction.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.prediction.entity.StudentRecord;
import com.prediction.repository.StudentRecordRepository;
import com.prediction.service.MlService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

	@Autowired
	private StudentRecordRepository recordRepository;

	@Autowired
	private MlService mlService;

	/* ---------------- DASHBOARD ---------------- */
	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		model.addAttribute("records", recordRepository.findAll());
		return "teacher-dashboard";
	}

	/* ---------------- ADD / UPDATE ---------------- */
	@PostMapping("/add")
	public String addOrUpdateRecord(StudentRecord record) {

		StudentRecord existing = recordRepository.findByStudentId(record.getStudentId());

		if (existing != null) {
			existing.setAttendance(record.getAttendance());
			existing.setInternalMarks(record.getInternalMarks());
			existing.setAssignmentScore(record.getAssignmentScore());
			existing.setQuizScore(record.getQuizScore());
			existing.setStudyHours(record.getStudyHours());
			existing.setPreviousCgpa(record.getPreviousCgpa());
			existing.setBacklogs(record.getBacklogs());

			// 🔥 NEW FIELDS
			existing.setClassParticipation(record.getClassParticipation());
			existing.setSubmissionRegular(record.getSubmissionRegular());

			recordRepository.save(existing);
		} else {
			recordRepository.save(record);
		}

		return "redirect:/teacher/dashboard";
	}

	/* ---------------- DELETE ---------------- */
	@PostMapping("/delete")
	public String deleteRecord(@RequestParam String studentId) {
		recordRepository.deleteByStudentId(studentId);
		return "redirect:/teacher/dashboard";
	}

	/* ---------------- PREDICT SINGLE ---------------- */
	@PostMapping("/predict")
	public String predictStudent(@RequestParam String studentId, Model model) {

		StudentRecord record = recordRepository.findByStudentId(studentId);

		if (record == null) {
			model.addAttribute("error", "Student not found");
			return "teacher-dashboard";
		}

		Map<String, Object> result = mlService.predict(Map.of("attendance_percent", record.getAttendance(),
				"internal_marks", record.getInternalMarks(), "assignment_score", record.getAssignmentScore(),
				"quiz_score", record.getQuizScore(), "study_hours_per_day", record.getStudyHours(), "previous_cgpa",
				record.getPreviousCgpa(), "backlogs", record.getBacklogs(),

				// 🔥 NEW FIELDS (STRING)
				"class_participation", record.getClassParticipation(), "submission_regular",
				record.getSubmissionRegular()));

		model.addAttribute("studentId", studentId);
		model.addAttribute("passFail", result.get("pass_fail"));
		model.addAttribute("riskLevel", result.get("risk_level"));
		model.addAttribute("predictedCgpa", result.get("predicted_cgpa"));
		model.addAttribute("reason", result.get("reason"));
		model.addAttribute("suggestion", result.get("suggestion"));

		return "teacher-prediction-result";
	}

	/* ---------------- DOWNLOAD ALL RECORDS ---------------- */
	@GetMapping("/download-records")
	public void downloadAllRecords(HttpServletResponse response) throws IOException {

		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=student_records.xlsx");

		List<StudentRecord> records = recordRepository.findAll();

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Student Records");

		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Student ID");
		header.createCell(1).setCellValue("Attendance");
		header.createCell(2).setCellValue("Internal Marks");
		header.createCell(3).setCellValue("Assignment");
		header.createCell(4).setCellValue("Quiz");
		header.createCell(5).setCellValue("Study Hours");
		header.createCell(6).setCellValue("Previous CGPA");
		header.createCell(7).setCellValue("Backlogs");
		header.createCell(8).setCellValue("Participation");
		header.createCell(9).setCellValue("Submission");

		int rowNum = 1;

		for (StudentRecord r : records) {
			Row row = sheet.createRow(rowNum++);

			row.createCell(0).setCellValue(r.getStudentId());
			row.createCell(1).setCellValue(r.getAttendance());
			row.createCell(2).setCellValue(r.getInternalMarks());
			row.createCell(3).setCellValue(r.getAssignmentScore());
			row.createCell(4).setCellValue(r.getQuizScore());
			row.createCell(5).setCellValue(r.getStudyHours());
			row.createCell(6).setCellValue(r.getPreviousCgpa());
			row.createCell(7).setCellValue(r.getBacklogs());

			// 🔥 NEW FIELDS
			row.createCell(8).setCellValue(r.getClassParticipation());
			row.createCell(9).setCellValue(r.getSubmissionRegular());
		}

		workbook.write(response.getOutputStream());
		workbook.close();
	}

	/* ---------------- SAFE CELL READ ---------------- */
	private double getNumeric(org.apache.poi.ss.usermodel.Cell cell) {
		if (cell == null) {
			return 0;
		}

		switch (cell.getCellType()) {
		case NUMERIC:
			return cell.getNumericCellValue();
		case STRING:
			return Double.parseDouble(cell.getStringCellValue());
		default:
			return 0;
		}
	}

	/* ---------------- UPLOAD + BULK PREDICT ---------------- */
	@PostMapping("/upload-excel")
	public String uploadExcel(@RequestParam("file") MultipartFile file, Model model, HttpSession session) {

		List<Map<String, Object>> predictions = new ArrayList<>();

		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

			Sheet sheet = workbook.getSheetAt(0);

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {

				Row row = sheet.getRow(i);
				if (row == null) {
					continue;
				}

				String studentId = row.getCell(0).toString().replace(".0", "");

				double attendance = getNumeric(row.getCell(1));
				double internal = getNumeric(row.getCell(2));
				double assignment = getNumeric(row.getCell(3));
				double quiz = getNumeric(row.getCell(4));
				double studyHours = getNumeric(row.getCell(5));
				double cgpa = getNumeric(row.getCell(6));
				int backlogs = (int) getNumeric(row.getCell(7));

				// 🔥 NEW INPUTS
				String participation = row.getCell(8).toString();
				String submission = row.getCell(9).toString();

				Map<String, Object> input = Map.of("attendance_percent", attendance, "internal_marks", internal,
						"assignment_score", assignment, "quiz_score", quiz, "study_hours_per_day", studyHours,
						"previous_cgpa", cgpa, "backlogs", backlogs, "class_participation", participation,
						"submission_regular", submission);

				Map<String, Object> result = mlService.predict(input);

				Map<String, Object> rowResult = new HashMap<>();
				rowResult.put("studentId", studentId);
				rowResult.put("attendance", attendance);
				rowResult.put("pass_fail", result.get("pass_fail"));
				rowResult.put("risk", result.get("risk_level"));
				rowResult.put("cgpa", result.get("predicted_cgpa"));
				rowResult.put("participation", participation);
				rowResult.put("submission", submission);

				predictions.add(rowResult);
			}

			model.addAttribute("predictions", predictions);
			session.setAttribute("predictions", predictions);

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Excel processing failed");
		}

		return "bulk-result";
	}

	/* ---------------- DOWNLOAD BULK PREDICTION ---------------- */
	@PostMapping("/download-predictions")
	public void downloadPredictions(HttpServletResponse response, HttpSession session) throws IOException {

		List<Map<String, Object>> predictions = (List<Map<String, Object>>) session.getAttribute("predictions");

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Predictions");

		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Student ID");
		header.createCell(1).setCellValue("Attendance");
		header.createCell(2).setCellValue("Pass/Fail");
		header.createCell(3).setCellValue("Risk Level");
		header.createCell(4).setCellValue("Predicted CGPA");
		header.createCell(5).setCellValue("Participation");
		header.createCell(6).setCellValue("Submission");

		int rowNum = 1;

		for (Map<String, Object> p : predictions) {
			Row row = sheet.createRow(rowNum++);

			row.createCell(0).setCellValue(p.get("studentId").toString());
			row.createCell(1).setCellValue(Double.parseDouble(p.get("attendance").toString()));
			row.createCell(2).setCellValue(p.get("pass_fail").toString());
			row.createCell(3).setCellValue(p.get("risk").toString());
			row.createCell(4).setCellValue(Double.parseDouble(p.get("cgpa").toString()));
			row.createCell(5).setCellValue(p.get("participation").toString());
			row.createCell(6).setCellValue(p.get("submission").toString());
		}

		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=prediction_results.xlsx");

		workbook.write(response.getOutputStream());
		workbook.close();
	}
}