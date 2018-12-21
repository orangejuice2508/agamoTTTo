package de.gruppe2.agamoTTTo.domain.base;

import de.gruppe2.agamoTTTo.domain.base.filter.PoolDateFilter;
import de.gruppe2.agamoTTTo.domain.entity.Record;
import de.gruppe2.agamoTTTo.domain.entity.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;


@Service
public class ExcelGenerator {

    public ByteArrayInputStream createExcelSheet(List<Record> records, PoolDateFilter filter, User user) {

        Workbook workbook = new XSSFWorkbook();
        CreationHelper createHelper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet("workhours");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Row firstHeaderRow = sheet.createRow(0);
        Cell firstRowFirstCell = firstHeaderRow.createCell(0);
        Cell firstRowSecondCell = firstHeaderRow.createCell(1);
        firstRowFirstCell.setCellValue("Name, Vorname der bzw. des Beschäftigten:");
        firstRowSecondCell.setCellValue(user.getLastName() + " " + user.getFirstName());
        Row secondHeaderRow = sheet.createRow(1);
        Cell secondRowFirstCell = secondHeaderRow.createCell(0);
        Cell secondRowSecondCell = secondHeaderRow.createCell(1);
        secondRowFirstCell.setCellValue("Aufzeichnung für den Zeitraum");
        secondRowSecondCell.setCellValue(filter.getFrom().toString() + "-" + filter.getTo().toString());

        try {
            workbook.write(output);
            output.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(output.toByteArray());
    }

    @Autowired
    public ExcelGenerator() {
    }
}
