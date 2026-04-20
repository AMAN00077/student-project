package com.prediction.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class PredictionExportService {

	public ByteArrayInputStream exportExcel(List<Map<String, Object>> data) throws Exception {

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Predictions");

		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Student ID");
		header.createCell(1).setCellValue("Pass / Fail");
		header.createCell(2).setCellValue("Risk Level");
		header.createCell(3).setCellValue("Predicted CGPA");

		int rowNum = 1;
		for (Map<String, Object> r : data) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(r.get("studentId").toString());
			row.createCell(1).setCellValue(r.get("pass_fail").toString());
			row.createCell(2).setCellValue(r.get("risk_level").toString());
			row.createCell(3).setCellValue(r.get("predicted_cgpa").toString());
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		workbook.write(out);
		workbook.close();

		return new ByteArrayInputStream(out.toByteArray());
	}
}
