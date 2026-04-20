package com.prediction.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.prediction.service.MlService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/student")
public class StudentController {

	@Autowired
	private MlService mlService;

	@GetMapping("/dashboard")
	public String dashboard() {
		return "student-dashboard";
	}

	@PostMapping("/predict")
	public String predict(@RequestParam Map<String, Object> params, Model model, HttpSession session) {

		try {
			System.out.println("FORM PARAMS = " + params);

			// 🔥 FIX: Rename keys to match Python API
			Map<String, Object> input = Map.of("attendance_percent", params.get("attendance_percent"), "internal_marks",
					params.get("internal_marks"), "assignment_score", params.get("assignment_score"), "quiz_score",
					params.get("quiz_score"), "study_hours_per_day", params.get("study_hours_per_day"), "previous_cgpa",
					params.get("previous_cgpa"), "backlogs", params.get("backlogs"),

					// 🔥 IMPORTANT FIX
					"class_participation", params.get("classParticipation"), "submission_regular",
					params.get("submissionRegular"));

			Map<String, Object> resultMap = mlService.predict(input);

			double previousCgpa = Double.parseDouble(params.get("previous_cgpa").toString());
			double predictedCgpa = Double.parseDouble(resultMap.get("predicted_cgpa").toString());

			// Save for graph
			session.setAttribute("previousCgpa", previousCgpa);
			session.setAttribute("predictedCgpa", predictedCgpa);

			model.addAttribute("passFail", resultMap.get("pass_fail"));
			model.addAttribute("riskLevel", resultMap.get("risk_level"));
			model.addAttribute("predictedCgpa", predictedCgpa);

			// 🔥 ADD THESE (OPTIONAL BUT GOOD)
			model.addAttribute("participation", params.get("classParticipation"));
			model.addAttribute("submission", params.get("submissionRegular"));

			model.addAttribute("reason", resultMap.get("reason"));
			model.addAttribute("suggestion", resultMap.get("suggestion"));
			model.addAttribute("explanation", resultMap.get("explanation"));

			return "student-result";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Prediction failed");
			return "student-dashboard";
		}
	}
//	@PostMapping("/predict")
//	public String predict(@RequestParam Map<String, Object> params, Model model, HttpSession session) {
//
//		try {
//			System.out.println("FORM PARAMS = " + params);
//
//			Map<String, Object> resultMap = mlService.predict(params);
//
//			double previousCgpa = Double.parseDouble(params.get("previous_cgpa").toString());
//
//			double predictedCgpa = Double.parseDouble(resultMap.getOrDefault("predicted_cgpa", "0").toString());
//
//			// Save for graph
//			session.setAttribute("previousCgpa", previousCgpa);
//			session.setAttribute("predictedCgpa", predictedCgpa);
//
//			model.addAttribute("passFail", resultMap.get("pass_fail"));
//			model.addAttribute("riskLevel", resultMap.get("risk_level"));
//			model.addAttribute("predictedCgpa", predictedCgpa);
//
//			// 🔥 XAI DATA
//			model.addAttribute("reason", resultMap.get("reason"));
//			model.addAttribute("suggestion", resultMap.get("suggestion")); // ✅ NEW
//			model.addAttribute("explanation", resultMap.get("explanation"));
//
//			return "student-result";
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			model.addAttribute("error", "Prediction failed");
//			return "student-dashboard";
//		}
//	}
//	@PostMapping("/predict")
//	public String predict(@RequestParam Map<String, Object> params, Model model, HttpSession session) {
//
//		try {
//			System.out.println("FORM PARAMS = " + params);
//
//			// 🔹 DIRECT MAP FROM SERVICE
//			Map<String, Object> resultMap = mlService.predict(params);
//
//			double previousCgpa = Double.parseDouble(params.get("previous_cgpa").toString());
//
//			double predictedCgpa = Double.parseDouble(resultMap.get("predicted_cgpa").toString());
//
//			// Save for graph
//			session.setAttribute("previousCgpa", previousCgpa);
//			session.setAttribute("predictedCgpa", predictedCgpa);
//
//			model.addAttribute("passFail", resultMap.get("pass_fail"));
//			model.addAttribute("riskLevel", resultMap.get("risk_level"));
//			model.addAttribute("predictedCgpa", predictedCgpa);
//			model.addAttribute("explanation", resultMap.get("explanation"));
//			model.addAttribute("reason", resultMap.get("reason"));
//
//			return "student-result";
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			model.addAttribute("error", "Prediction failed");
//			return "student-dashboard";
//		}
//	}

//	@PostMapping("/predict")
//	public String predict(@RequestParam Map<String, Object> params, Model model, HttpSession session) {
//
//		try {
//			System.out.println("FORM PARAMS = " + params);
//
//			String jsonResult = mlService.predict(params);
//			ObjectMapper mapper = new ObjectMapper();
//			Map<String, Object> resultMap = mapper.readValue(jsonResult, Map.class);
//
//			/* ---------------- PREVIOUS CGPA ---------------- */
//			double previousCgpa = Double.parseDouble(params.getOrDefault("previous_cgpa", "0").toString());
//
//			/* ---------------- PREDICTED CGPA ---------------- */
//			double predictedCgpa;
//
//			if (resultMap.get("predicted_cgpa") != null) {
//				predictedCgpa = Double.parseDouble(resultMap.get("predicted_cgpa").toString());
//			} else {
//				predictedCgpa = previousCgpa; // fallback
//			}
//
//			// 🔒 Clamp CGPA between 0 and 10
//			predictedCgpa = Math.max(0, Math.min(10, predictedCgpa));
//
//			/* ---------------- PASS / FAIL ---------------- */
//			String passFail = String.valueOf(resultMap.get("pass_fail"));
//
//			/* ---------------- RISK LEVEL ---------------- */
//			String riskLevel = String.valueOf(resultMap.get("risk_level"));
//
//			/* ---------------- WRITTEN EXPLANATION ---------------- */
//			StringBuilder reason = new StringBuilder();
//
//			if ("Fail".equalsIgnoreCase(passFail)) {
//				reason.append("The student is predicted to fail due to ")
//						.append("low academic performance indicators such as ")
//						.append("attendance, internal marks, and backlogs. ");
//			} else {
//				reason.append("The student is predicted to pass due to ")
//						.append("consistent academic performance and engagement. ");
//			}
//
//			if ("High".equalsIgnoreCase(riskLevel)) {
//				reason.append("The risk level is high because the model ")
//						.append("detected weak patterns in study behavior ").append("and academic consistency.");
//			} else if ("Medium".equalsIgnoreCase(riskLevel)) {
//				reason.append("The risk level is medium, indicating ")
//						.append("moderate chances of performance fluctuation.");
//			} else {
//				reason.append("The risk level is low, showing stable ").append("academic performance.");
//			}
//
//			/* ---------------- SESSION (FOR GRAPH) ---------------- */
//			session.setAttribute("previousCgpa", previousCgpa);
//			session.setAttribute("predictedCgpa", predictedCgpa);
//
//			/* ---------------- MODEL ATTRIBUTES ---------------- */
//			model.addAttribute("passFail", passFail);
//			model.addAttribute("riskLevel", riskLevel);
//			model.addAttribute("predictedCgpa", predictedCgpa);
//			model.addAttribute("explanation", resultMap.get("explanation"));
//			model.addAttribute("reason", reason.toString());
//
//			return "student-result";
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			model.addAttribute("error", "Prediction failed. Please try again.");
//			return "student-dashboard";
//		}
//	}

	@GetMapping("/progress")
	public String progress(HttpSession session, Model model) {

		Object prev = session.getAttribute("previousCgpa");
		Object pred = session.getAttribute("predictedCgpa");

		if (prev == null || pred == null) {
			model.addAttribute("error", "Please predict performance first.");
			return "student-dashboard";
		}

		model.addAttribute("previousCgpa", prev);
		model.addAttribute("predictedCgpa", pred);

		return "student-progress";
	}
}

//package com.prediction.controller;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.prediction.service.MlService;
//
//import jakarta.servlet.http.HttpSession;
//
//@Controller
//@RequestMapping("/student")
//public class StudentController {
//
//	@Autowired
//	private MlService mlService;
//
//	// ==========================
//	// DASHBOARD
//	// ==========================
//	@GetMapping("/dashboard")
//	public String dashboard() {
//		return "student-dashboard";
//	}
//
//	// ==========================
//	// PREDICT
//	// ==========================
//	@PostMapping("/predict")
//	public String predict(@RequestParam Map<String, String> params, Model model, HttpSession session) {
//
//		try {
//			System.out.println("FORM PARAMS = " + params);
//
//			// ✅ Convert & map properly
//			Map<String, Object> input = new HashMap<>();
//
//			input.put("attendance_percent", Double.parseDouble(params.get("attendance_percent")));
//			input.put("internal_marks", Double.parseDouble(params.get("internal_marks")));
//			input.put("assignment_score", Double.parseDouble(params.get("assignment_score")));
//			input.put("quiz_score", Double.parseDouble(params.get("quiz_score")));
//			input.put("study_hours_per_day", Double.parseDouble(params.get("study_hours_per_day")));
//			input.put("previous_cgpa", Double.parseDouble(params.get("previous_cgpa")));
//			input.put("backlogs", Integer.parseInt(params.get("backlogs")));
//
//			// 🔥 FIXED KEY NAMES
//			input.put("class_participation", params.get("classParticipation"));
//			input.put("submission_regular", params.get("submissionRegular"));
//
//			// ✅ CALL ML API
//			Map<String, Object> resultMap = mlService.predict(input);
//
//			double previousCgpa = Double.parseDouble(params.get("previous_cgpa"));
//			double predictedCgpa = Double.parseDouble(resultMap.get("predicted_cgpa").toString());
//
//			// SESSION (for graph)
//			session.setAttribute("previousCgpa", previousCgpa);
//			session.setAttribute("predictedCgpa", predictedCgpa);
//
//			// MODEL ATTRIBUTES
//			model.addAttribute("passFail", resultMap.get("pass_fail"));
//			model.addAttribute("riskLevel", resultMap.get("risk_level"));
//			model.addAttribute("predictedCgpa", predictedCgpa);
//
//			model.addAttribute("reason", resultMap.get("reason"));
//			model.addAttribute("suggestion", resultMap.get("suggestion"));
//			model.addAttribute("explanation", resultMap.get("explanation"));
//
//			return "student-result";
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			model.addAttribute("error", "Prediction failed: " + e.getMessage());
//			return "student-dashboard";
//		}
//	}
//
//	// ==========================
//	// PROGRESS PAGE
//	// ==========================
//	@GetMapping("/progress")
//	public String progress(HttpSession session, Model model) {
//
//		Object prev = session.getAttribute("previousCgpa");
//		Object pred = session.getAttribute("predictedCgpa");
//
//		if (prev == null || pred == null) {
//			model.addAttribute("error", "Please predict performance first.");
//			return "student-dashboard";
//		}
//
//		model.addAttribute("previousCgpa", prev);
//		model.addAttribute("predictedCgpa", pred);
//
//		return "student-progress";
//	}
//}
