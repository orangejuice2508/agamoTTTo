package de.gruppe2.agamoTTTo.util;

import de.gruppe2.agamoTTTo.domain.base.filter.PoolDateFilter;
import de.gruppe2.agamoTTTo.domain.entity.Record;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.security.Permission;
import de.gruppe2.agamoTTTo.service.RecordService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * This class creates the excel sheet for exporting records.
 */
@Component
public class ExcelGenerator {

    private RecordService recordService;

    @Autowired
    public ExcelGenerator(RecordService recordService) {
        this.recordService = recordService;
    }

    @PreAuthorize(Permission.MITARBEITER)
    public ByteArrayInputStream createExcelSheet(PoolDateFilter filter, User user) {

        List<Record> records = recordService.getAllRecordsByFilter(filter, user);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("workhours");
        XSSFCellStyle style = ((XSSFWorkbook) workbook).createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        String clock = "Uhr";
        String hour = "hh";
        String minute = "mm";
        String colon = ":";
        String hyphen = "-";
        String datePattern = "dd.MM.yyyy";

        //Creating first row of the sheet
        Row firstHeaderRow = sheet.createRow(0);
        Cell firstRowFirstCell = firstHeaderRow.createCell(0);
        Cell firstRowSecondCell = firstHeaderRow.createCell(1);
        firstRowFirstCell.setCellValue("Name, Vorname der bzw. des Beschäftigten:");
        firstRowSecondCell.setCellValue(user.getLastName() + " " + user.getFirstName());

        //Creating second row of the sheet
        Row secondHeaderRow = sheet.createRow(1);
        Cell secondRowFirstCell = secondHeaderRow.createCell(0);
        Cell secondRowSecondCell = secondHeaderRow.createCell(1);
        secondRowFirstCell.setCellValue("Aufzeichnung für den Zeitraum");
        secondRowSecondCell.setCellValue(filter.getFrom().format(DateTimeFormatter.ofPattern(datePattern)) + hyphen + filter.getTo().format(DateTimeFormatter.ofPattern(datePattern)));

        //Creating third row of the sheet
        Row thirdHeaderRow = sheet.createRow(2);
        Cell thirdRowFirstCell = thirdHeaderRow.createCell(0);
        Cell thirdRowSecondCell = thirdHeaderRow.createCell(1);thirdHeaderRow.createCell(2);
        thirdHeaderRow.createCell(3);
        thirdHeaderRow.createCell(4);
        thirdHeaderRow.createCell(5);
        Cell thirdRowSixthCell = thirdHeaderRow.createCell(6);
        Cell thirdRowSeventhCell = thirdHeaderRow.createCell(7);
        thirdRowFirstCell.setCellValue("Datum");
        thirdRowSecondCell.setCellValue("Uhrzeiten");
        thirdRowSixthCell.setCellValue("Stunden");
        thirdRowSeventhCell.setCellValue("Tätigkeit");
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 5));

        //Creating fourth row of the sheet
        Row fourthHeaderRow = sheet.createRow(3);
        Cell fourthRowFirstCell = fourthHeaderRow.createCell(0);
        Cell fourthRowSecondCell = fourthHeaderRow.createCell(1);
        Cell fourthRowThirdCell = fourthHeaderRow.createCell(2);
        Cell fourhRowFourthCell = fourthHeaderRow.createCell(3);
        Cell fourthRowFifthCell = fourthHeaderRow.createCell(4);
        Cell fourthRowSixthCell = fourthHeaderRow.createCell(5);
        Cell fourthRowSeventhCell = fourthHeaderRow.createCell(6);
        fourthHeaderRow.createCell(7);
        fourthRowFirstCell.setCellValue("tt.mm.jjjj");
        fourthRowSecondCell.setCellValue(hour + colon + minute);
        fourthRowThirdCell.setCellValue(clock);
        fourhRowFourthCell.setCellValue(hyphen);
        fourthRowFifthCell.setCellValue(hour + colon + minute);
        fourthRowSixthCell.setCellValue(clock);
        fourthRowSeventhCell.setCellValue("Dezimal");


        int rowIndex = 4;
        Long totalDuration = 0L;

        // Creating and filling a row for each record
        for (Record record : records) {
            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(record.getDate().format(DateTimeFormatter.ofPattern(datePattern)));
            row.createCell(1).setCellValue(record.getStartTime().toString());
            row.createCell(2).setCellValue(clock);
            row.createCell(3).setCellValue(hyphen);
            row.createCell(4).setCellValue(record.getEndTime().toString());
            row.createCell(5).setCellValue(clock);
            row.createCell(6).setCellValue(durationAsDecimal(record.getDuration()));
            row.createCell(7).setCellValue(record.getDescription());
            totalDuration = totalDuration + record.getDuration();
        }

        // Creating the last row of the sheet
        Row duration = sheet.createRow(rowIndex);
        Cell firstDurationCell = duration.createCell(0);
        firstDurationCell.setCellValue("Zwischensumme(Dezimal)");
        duration.createCell(1);
        duration.createCell(2);
        duration.createCell(3);
        duration.createCell(4);
        duration.createCell(5);
        duration.createCell(6).setCellValue(durationAsDecimal(totalDuration));
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 5));

        // Set cell style fot each row in the sheet.
        for (Row row : sheet) {
            for(Cell cell : row) {
                cell.setCellStyle(style);
            }
        }

        try {
            workbook.write(output);
            output.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(output.toByteArray());
    }

    /**
     * Method to convert the duration from minutes to form hh:mm
     * @param duration the duration that should be converted
     * @return the converted duration as String
     */
    private String durationAsDecimal(Long duration) {
        Long hour = duration/60;
        Long minutes = duration%60;
        String calculatedMinutes = minutes.toString();
        if (minutes < 10) {
            calculatedMinutes = 0 + calculatedMinutes;
        }
        return hour + ":" + calculatedMinutes;
    }
}
