//package com.prediction.service;
//
//import java.util.Map;
//
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import tools.jackson.databind.ObjectMapper;
//
//@Service
//public class MlService {
//
//	private final RestTemplate restTemplate = new RestTemplate();
//	private final ObjectMapper mapper = new ObjectMapper();
//
//	public Map<String, Object> predict(Map<String, Object> data) {
//
//		String flaskUrl = "http://127.0.0.1:5000/predict";
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//
//		HttpEntity<Map<String, Object>> request = new HttpEntity<>(data, headers);
//
//		ResponseEntity<String> response = restTemplate.postForEntity(flaskUrl, request, String.class);
//
//		try {
//			Map<String, Object> resultMap = mapper.readValue(response.getBody(), Map.class);
//
//			// 🔒 Clamp CGPA between 0–10
//			if (resultMap.containsKey("predicted_cgpa")) {
//				double cgpa = Double.parseDouble(resultMap.get("predicted_cgpa").toString());
//
//				cgpa = Math.max(0, Math.min(10, cgpa));
//				resultMap.put("predicted_cgpa", cgpa);
//			}
//
//			return resultMap;
//
//		} catch (Exception e) {
//			throw new RuntimeException("ML Prediction failed", e);
//		}
//	}
//}
package com.prediction.service;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import tools.jackson.databind.ObjectMapper;

@Service
public class MlService {

	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper mapper = new ObjectMapper();

	// ✅ USE YOUR DEPLOYED API
	private final String flaskUrl = "https://ml-api-o2cv.onrender.com/predict";

	public Map<String, Object> predict(Map<String, Object> data) {

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Map<String, Object>> request = new HttpEntity<>(data, headers);

			// ✅ Directly get JSON as String
			String response = restTemplate.postForObject(flaskUrl, request, String.class);

			// ✅ Convert JSON → Map
			Map<String, Object> resultMap = mapper.readValue(response, Map.class);

			// 🔒 Clamp CGPA between 0–10
			if (resultMap.containsKey("predicted_cgpa")) {
				double cgpa = Double.parseDouble(resultMap.get("predicted_cgpa").toString());
				cgpa = Math.max(0, Math.min(10, cgpa));
				resultMap.put("predicted_cgpa", cgpa);
			}

			return resultMap;

		} catch (Exception e) {
			throw new RuntimeException("ML Prediction failed: " + e.getMessage());
		}
	}
}