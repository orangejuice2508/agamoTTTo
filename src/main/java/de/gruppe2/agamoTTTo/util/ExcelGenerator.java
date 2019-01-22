package de.gruppe2.agamoTTTo.util;

import de.gruppe2.agamoTTTo.domain.bo.filter.PoolDateFilter;
import de.gruppe2.agamoTTTo.domain.entity.Record;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.security.Permission;
import de.gruppe2.agamoTTTo.service.RecordService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
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

        // Create new Excel sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Arbeitszeiten");

        // Create cell style
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        // Set often used values
        String clock = "Uhr";
        String hour = "hh";
        String minute = "mm";
        String colon = ":";
        String hyphen = "-";
        String datePattern = "dd.MM.yyyy";

        // Start with first (-->0) row
        int rowIndex = 0;

        // Creating first row of the sheet
        sheet.createRow(rowIndex);
        Row firstHeaderRow = createAndMergeColumnsOfRow(sheet, rowIndex, 0, 6);
        firstHeaderRow.getCell(0).setCellValue("Name, Vorname der bzw. des Beschäftigten:");
        firstHeaderRow.createCell(7).setCellValue(user.getLastName() + ", " + user.getFirstName());
        rowIndex++;

        // Creating second row of the sheet
        sheet.createRow(rowIndex);
        Row secondHeaderRow = createAndMergeColumnsOfRow(sheet, rowIndex, 0, 6);
        secondHeaderRow.getCell(0).setCellValue("Aufzeichnung für den Zeitraum");
        secondHeaderRow.createCell(7).setCellValue(filter.getFrom().format(DateTimeFormatter.ofPattern(datePattern)) + hyphen + filter.getTo().format(DateTimeFormatter.ofPattern(datePattern)));
        rowIndex++;

        // Creating fourth row of the sheet
        Row thirdHeaderRow = sheet.createRow(++rowIndex);
        thirdHeaderRow.createCell(0).setCellValue("Datum");
        thirdHeaderRow = createAndMergeColumnsOfRow(sheet, rowIndex, 1, 5);
        thirdHeaderRow.getCell(1).setCellValue("Uhrzeiten");
        thirdHeaderRow.createCell(6).setCellValue("Stunden");
        thirdHeaderRow = createAndMergeColumnsOfRow(sheet, rowIndex, 7, 8);
        thirdHeaderRow.getCell(7).setCellValue("Tätigkeit");
        rowIndex++;

        // Creating fifth row of the sheet
        Row fourthHeaderRow = sheet.createRow(rowIndex);
        fourthHeaderRow.createCell(0).setCellValue("tt.mm.jjjj");
        fourthHeaderRow.createCell(1).setCellValue(hour + colon + minute);
        fourthHeaderRow.createCell(2).setCellValue(clock);
        fourthHeaderRow.createCell(3).setCellValue(hyphen);
        fourthHeaderRow.createCell(4).setCellValue(hour + colon + minute);
        fourthHeaderRow.createCell(5).setCellValue(clock);
        fourthHeaderRow.createCell(6).setCellValue("Dezimal");
        fourthHeaderRow = createAndMergeColumnsOfRow(sheet, rowIndex, 7, 8);
        fourthHeaderRow.getCell(7).setCellValue("Beschreibung");
        rowIndex++;

        // Get all records by filter and user
        List<Record> records = recordService.getAllRecordsByFilter(filter, user);

        // Initalize totalDuration with 0
        Long totalDuration = 0L;

        // Creating and filling a row for each record
        for (Record record : records) {
            Row row = sheet.createRow(rowIndex);

            row.createCell(0).setCellValue(record.getDate().format(DateTimeFormatter.ofPattern(datePattern)));
            row.createCell(1).setCellValue(record.getStartTime().toString());
            row.createCell(2).setCellValue(clock);
            row.createCell(3).setCellValue(hyphen);
            row.createCell(4).setCellValue(record.getEndTime().toString());
            row.createCell(5).setCellValue(clock);
            row.createCell(6).setCellValue(durationAsDecimal(record.getDuration()));
            row = createAndMergeColumnsOfRow(sheet, rowIndex, 7, 8);
            row.getCell(7).setCellValue(record.getDescription());
            totalDuration = totalDuration + record.getDuration();

            rowIndex++;
        }

        // Creating the first "duration row" of the sheet
        sheet.createRow(rowIndex);
        Row firstDurationRow = createAndMergeColumnsOfRow(sheet, rowIndex, 0, 5);
        firstDurationRow.getCell(0).setCellValue("Zwischensumme(Dezimal)");
        firstDurationRow.createCell(6).setCellValue(durationAsDecimal(totalDuration));
        firstDurationRow.createCell(7);
        firstDurationRow.createCell(8);
        rowIndex++;

        // Creating the second "duration row" of the sheet
        sheet.createRow(rowIndex);
        Row secondDurationRow = createAndMergeColumnsOfRow(sheet, rowIndex, 0, 7);
        secondDurationRow.getCell(0).setCellValue("Summe Arbeitszeiten aktueller Monat (Dezimal):");
        secondDurationRow.createCell(8);
        rowIndex++;

        // Creating "place and date row" of the sheet
        Row placeAndDateRow = sheet.createRow(++rowIndex);
        placeAndDateRow.createCell(0).setCellValue("Passau, den");
        createAndMergeColumnsOfRow(sheet, rowIndex, 2, 6);

        // Set cell style for all cells
        styleCells(style, sheet);

        // Try to write workbook to file and catch exception
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
     *
     * @param duration the duration that should be converted
     * @return the converted duration as String
     */
    private String durationAsDecimal(Long duration) {
        long hour = duration / 60;
        long minutes = duration % 60;
        String calculatedMinutes = ((Long) minutes).toString();
        if (minutes < 10) {
            calculatedMinutes = 0 + calculatedMinutes;
        }
        return hour + ":" + calculatedMinutes;
    }

    /**
     * Method that creates new columns in the specified row and merges them.
     *
     * @param sheet    the sheet which contains the specified row
     * @param row      the number of the row which should be modified
     * @param firstCol the number of the first column to be created
     * @param lastCol  the number of the last column to be created
     * @return the modified row with the created and merged columns
     */
    private Row createAndMergeColumnsOfRow(Sheet sheet, int row, int firstCol, int lastCol) {
        // Get the specified row from the sheet
        Row sheetRow = sheet.getRow(row);

        // Create a new column based on the specified col numbers
        for (int col = firstCol; col <= lastCol; col++) {
            sheetRow.createCell(col);
        }

        // Merge the cells
        sheet.addMergedRegion(new CellRangeAddress(row, row, firstCol, lastCol));

        return sheetRow;
    }

    /**
     * This method applies the specified style to each cell of the sheet.
     * Additionally it sets the size of every column of the sheet to auto.
     *
     * @param style the style to be applied to each cell
     * @param sheet the sheet of which every cell should be modified
     */
    private void styleCells(CellStyle style, Sheet sheet) {
        // The last column is 0 at start.
        int maxCell = 0;

        // Set cell style for each row in the sheet and determine maximum cell number
        for (Row row : sheet) {
            for (Cell cell : row) {
                cell.setCellStyle(style);
            }
            // If the number of cells of the current row is higher than maxCell, overwrite the current value of maxCell.
            if (row.getLastCellNum() > maxCell) {
                maxCell = row.getLastCellNum();
            }
        }

        // Autosize all columns
        for (int cell = 0; cell <= maxCell; cell++) {
            sheet.autoSizeColumn(cell);
        }
    }
}
