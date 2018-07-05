package com.whxm.harbor.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {

    public static boolean isExcel2003(String filePath) {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    public static boolean isExcel2007(String filePath) {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

    public static List importData(File file) {

        Workbook wb = null;

        List HeroList = new ArrayList();
        try {
            if (isExcel2007(file.getPath())) {
                wb = new XSSFWorkbook(new FileInputStream(file));
            } else {
                wb = new HSSFWorkbook(new FileInputStream(file));
            }
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }

        String shopName = null;
        int formatId = 0;

        Sheet sheet = wb.getSheetAt(0);//获取第一张表
        int start = 1;
        for (int i = start; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);//获取索引为i的行，以0开始
            String shopName_ = row.getCell(0).getStringCellValue();//获取第i行的索引为0的单元格数据

            Cell column1 = row.getCell(1);
            double formatId_ = column1.getNumericCellValue();
            System.out.println(shopName_ + "---" + formatId_);
            if (null == shopName_
                    && 0 == formatId_) {
                break;
            }
            //set field value
        }
        try {
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return HeroList;
    }
}
