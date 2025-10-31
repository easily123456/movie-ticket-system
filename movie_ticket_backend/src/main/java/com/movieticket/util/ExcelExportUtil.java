package com.movieticket.util;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class ExcelExportUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 导出数据到Excel
    public <T> void exportToExcel(HttpServletResponse response, List<T> data, Class<T> clazz, String fileName) {
        try {
            Workbook workbook = createWorkbook(data, clazz);
            setResponseHeaders(response, fileName);
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            log.error("导出Excel失败", e);
            throw new RuntimeException("导出失败");
        }
    }

    // 创建工作簿
    private <T> Workbook createWorkbook(List<T> data, Class<T> clazz) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("数据导出");

        // 创建表头
        Row headerRow = sheet.createRow(0);
        Field[] fields = clazz.getDeclaredFields();
        createHeaderRow(headerRow, fields, workbook);

        // 创建数据行
        createDataRows(sheet, data, fields);

        // 自动调整列宽
        autoSizeColumns(sheet, fields.length);

        return workbook;
    }

    // 创建表头行
    private void createHeaderRow(Row headerRow, Field[] fields, Workbook workbook) {
        CellStyle headerStyle = createHeaderStyle(workbook);

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(getFieldDisplayName(field));
            cell.setCellStyle(headerStyle);
        }
    }

    // 创建数据行
    private <T> void createDataRows(Sheet sheet, List<T> data, Field[] fields) {
        CellStyle dataStyle = createDataStyle(sheet.getWorkbook());

        for (int i = 0; i < data.size(); i++) {
            T item = data.get(i);
            Row dataRow = sheet.createRow(i + 1);

            for (int j = 0; j < fields.length; j++) {
                Field field = fields[j];
                field.setAccessible(true);

                try {
                    Object value = field.get(item);
                    Cell cell = dataRow.createCell(j);
                    setCellValue(cell, value, dataStyle);
                } catch (IllegalAccessException e) {
                    log.error("获取字段值失败: {}", field.getName(), e);
                    dataRow.createCell(j).setCellValue("");
                }
            }
        }
    }

    // 设置单元格值
    private void setCellValue(Cell cell, Object value, CellStyle style) {
        cell.setCellStyle(style);

        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue(((LocalDateTime) value).format(DATE_FORMATTER));
        } else {
            cell.setCellValue(value.toString());
        }
    }

    // 创建表头样式
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();

        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());

        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    // 创建数据样式
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    // 获取字段显示名称
    private String getFieldDisplayName(Field field) {
        // 这里可以根据注解获取显示名称
        // 暂时返回字段名
        return field.getName();
    }

    // 自动调整列宽
    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // 设置响应头
    private void setResponseHeaders(HttpServletResponse response, String fileName) throws IOException {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                .replaceAll("\\+", "%20");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename*=UTF-8''" + encodedFileName + ".xlsx");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
    }
}