package com.orderfleet.webapp.service.async;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ExcelPOIHelper {

	public Map<Integer, List<String>> readExcel(File file,int sheetNumber) throws IOException {
		Map<Integer, List<String>> data = new HashMap<>();
		FileInputStream fis = new FileInputStream(file);
		if (file.getName().endsWith(".xls")) {
			data = readHSSFWorkbook(fis,sheetNumber);
		} else if (file.getName().endsWith(".xlsx")) {
			data = readXSSFWorkbook(fis,sheetNumber);
		}
		int maxNrCols = data.values().stream().mapToInt(List::size).max().orElse(0);
		data.values().stream().filter(ls -> ls.size() < maxNrCols).forEach(ls -> {
			IntStream.range(ls.size(), maxNrCols).forEach(i -> ls.add(" "));
		});
		return data;
	}

	private Map<Integer, List<String>> readHSSFWorkbook(FileInputStream fis,int sheetNumber) throws IOException {
		Map<Integer, List<String>> data = new HashMap<>();
		HSSFWorkbook workbook = null;
		try {
			workbook = new HSSFWorkbook(fis);
			HSSFSheet sheet = workbook.getSheetAt(sheetNumber);
			for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
				HSSFRow row = sheet.getRow(i);
				if (row.getCell(1) != null && row.getCell(1).getCellTypeEnum() != CellType.BLANK) {
					data.put(i, new ArrayList<>());
					for (int j = 0; j < row.getLastCellNum(); j++) {
						HSSFCell cell = row.getCell(j);
						if (cell != null ) {
							data.get(i).add(readCellContent(cell));
						} else {
							data.get(i).add(" ");
						}
					}
				}
			}
		} finally {
			if (workbook != null) {
				workbook.close();
			}
		}
		return data;
	}

	private Map<Integer, List<String>> readXSSFWorkbook(FileInputStream fis,int sheetNumber) throws IOException {
		XSSFWorkbook workbook = null;
		Map<Integer, List<String>> data = new HashMap<>();
		try {
			workbook = new XSSFWorkbook(fis);
			DataFormatter objDefaultFormat = new DataFormatter();
			FormulaEvaluator objFormulaEvaluator = new XSSFFormulaEvaluator(workbook);

			XSSFSheet sheet = workbook.getSheetAt(sheetNumber);
			for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
				XSSFRow row = sheet.getRow(i);
				data.put(i, new ArrayList<>());
				if (row != null) {
					for (int j = 0; j < row.getLastCellNum(); j++) {
						XSSFCell cell = row.getCell(j);
						objFormulaEvaluator.evaluate(cell);
						if (cell != null) {
							String cellValueStr = objDefaultFormat.formatCellValue(cell, objFormulaEvaluator);
							data.get(i).add(cellValueStr);
						} else {
							data.get(i).add(" ");
						}
					}
				}
			}
		} finally {
			if (workbook != null) {
				workbook.close();
			}
		}
		return data;
	}

	private String readCellContent(Cell cell) {
		String content;
		switch (cell.getCellTypeEnum()) {
		case STRING:
			content = cell.getStringCellValue();
			break;
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				content = cell.getDateCellValue() + "";
			} else {
				content = cell.getNumericCellValue() + "";
			}
			break;
		case BOOLEAN:
			content = cell.getBooleanCellValue() + "";
			break;
		case FORMULA:
			content = cell.getCellFormula() + "";
			break;
		default:
			content = "";
		}
		return content;
	}

}
