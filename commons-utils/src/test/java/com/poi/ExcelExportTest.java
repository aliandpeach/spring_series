package com.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;

public class ExcelExportTest
{
    public static void main(String[] args) {
        try (Workbook workbook = new XSSFWorkbook()) {

            // 创建Sheet
            Sheet sheet = workbook.createSheet("课程表");

            // 创建第一行并合并单元格（A到V）
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("学期课程表");

            // 合并 A1 到 V1 单元格
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 21));

            // 设置标题样式
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true); // 加粗
            titleFont.setFontHeightInPoints((short) 16); // 字体大小
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER); // 居中对齐
            titleCell.setCellStyle(titleStyle);

            // 创建第二行（原来的第一行），开始添加原来的标题内容
            Row headerRow1 = sheet.createRow(1);
            Row headerRow2 = sheet.createRow(2);

            // 创建合并单元格
            // A-F 合并两行
            String[] headers1 = {"序号", "班级名称", "学生人数", "班主任", "起始量", "节次"};
            for (int i = 0; i < headers1.length; i++) {
                Cell cell = headerRow1.createCell(i);
                cell.setCellValue(headers1[i]);
                // 合并 A2:F3 单元格
                sheet.addMergedRegion(new CellRangeAddress(1, 2, i, i));
            }

            // 星期一到星期五，G-U 每个星期3列
            String[] weekdays = {"星期一", "星期二", "星期三", "星期四", "星期五"};
            String[] subHeaders = {"课程名称", "任课教师", "教室"};

            for (int i = 0; i < weekdays.length; i++) {
                // 星期一到星期五的标题在第二行
                Cell cell = headerRow1.createCell(6 + i * 3); // G 列开始
                cell.setCellValue(weekdays[i]);
                // 合并列，G-I, J-L, M-O, P-R, S-U
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 6 + i * 3, 6 + i * 3 + 2));

                // 子标题在第三行
                for (int j = 0; j < subHeaders.length; j++) {
                    Cell subCell = headerRow2.createCell(6 + i * 3 + j);
                    subCell.setCellValue(subHeaders[j]);
                }
            }

            // V 列备注，合并两行
            Cell remarkCell = headerRow1.createCell(21); // V 列
            remarkCell.setCellValue("备注");
            sheet.addMergedRegion(new CellRangeAddress(1, 2, 21, 21));

            // 调整列宽（可以根据需要调整）
            for (int i = 0; i <= 21; i++) {
                sheet.autoSizeColumn(i);
            }

            // 导出文件
            try (FileOutputStream fileOut = new FileOutputStream("C:\\Users\\yangkai\\Desktop\\课程表.xlsx")) {
                workbook.write(fileOut);
            }

            System.out.println("Excel 生成成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
