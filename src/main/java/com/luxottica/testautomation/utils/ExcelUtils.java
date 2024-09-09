package com.luxottica.testautomation.utils;

import com.luxottica.testautomation.components.bucket.enums.BucketColumn;
import com.luxottica.testautomation.components.bucket.enums.StoresColumn;
import com.luxottica.testautomation.components.report.enums.ReportColumn;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Objects;

public class ExcelUtils {

    public static Cell getCell(Row row, ReportColumn column) {
        return row.getCell(column.offset);
    }

    public static Cell getCell(Row row, BucketColumn column) {
        return row.getCell(column.offset);
    }

    public static Cell getCell(Row row, StoresColumn column) {
        return row.getCell(column.offset);
    }

    public static boolean isCellNotNull(Row row, ReportColumn column) {
        Cell cell = row.getCell(column.offset);
        return Objects.nonNull(cell);
    }

    public static boolean isCellNull(Row row, ReportColumn column) {
        return !isCellNotNull(row, column);
    }

    public static boolean isCellNotNull(Row row, BucketColumn column) {
        Cell cell = row.getCell(column.offset);
        return Objects.nonNull(cell);
    }

    public static boolean isCellNull(Row row, StoresColumn column) {
        return !isCellNotNull(row, column);
    }

    public static boolean isCellNotNull(Row row, StoresColumn column) {
        Cell cell = row.getCell(column.offset);
        return Objects.nonNull(cell);
    }

    public static boolean isCellNull(Row row, BucketColumn column) {
        return !isCellNotNull(row, column);
    }

}
