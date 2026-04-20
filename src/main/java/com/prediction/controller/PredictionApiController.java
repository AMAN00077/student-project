package com.prediction.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prediction.service.MlService;

@RestController
@RequestMapping("/api")
public class PredictionApiController {

	@Autowired
	private MlService mlService;

	@PostMapping("/predict")
	public Map<String, Object> predict(@RequestBody Map<String, Object> input) {

		System.out.println("API INPUT = " + input);

		Map<String, Object> result = mlService.predict(input);

		return result; // ✅ JSON response
	}
}