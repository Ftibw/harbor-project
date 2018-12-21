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
import java.util.UUID;

public class ExcelUtils {

    public static boolean isExcel2003(String filePath) {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    public static boolean isExcel2007(String filePath) {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

    public static <T> List<T> importData(File file, Class<T> clazz) {

        Workbook wb = null;

        List<T> list = new ArrayList<>();
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

        Sheet sheet = wb.getSheetAt(0);//获取第一张表
        //第一列为标题项,第一行全部非空
        int start = 1;
        for (int i = start; i < sheet.getLastRowNum() + 1; i++) {
            Row row = sheet.getRow(i);//获取索引为i的行，以0开始
            Cell col0 = row.getCell(0);//获取第i行的索引为0的单元格数据
            Cell col1 = row.getCell(1);
            Cell col2 = row.getCell(2);
            Cell col3 = row.getCell(3);
            Cell col4 = row.getCell(4);
            Cell col5 = row.getCell(5);
            Cell col6 = row.getCell(6);

            String value0 = null != col0 ? col0.toString() : null;
            Double value1 = null != col1 ? col1.getNumericCellValue() : null;
            Double value2 = null != col2 ? col2.getNumericCellValue() : null;
            String value3 = null != col3 ? col3.toString() : null;
            String value4 = null != col4 ? col4.toString() : null;
            String value5 = null != col5 ? col5.toString() : null;
            String value6 = null != col6 ? col6.toString() : null;
            /*try {
                T t = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }*/
            BizShop shop = new BizShop();
            shop.setShopName(value0);
            shop.setBizFormatId(value1 == null ? null : value1.intValue());
            shop.setFloorId(value2 == null ? null : value2.intValue());
            shop.setShopNumber(value3);
            shop.setShopTel(value4);
            shop.setShopDescript(value5);
            shop.setShopLogoPath(value6);
            shop.setShopId(UUID.randomUUID().toString().replace("-", ""));

            try {
                shop.setShopEnglishName(PinyinUtils.toPinyin(value0));
            } catch (Exception e) {
                System.out.println(value0);//"π茶"中"π"转拼音报错
            }

            // String shopName = null;
            // int formatId = 0;
            // int floorId = 0;
            // String shopNumber = null;
            // String phoneNumber = null;
            // String describe = null;
            // String logoPath = null;
            list.add((T) shop);

            if (null == value0) break;

            //set field value
        }
        try {
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}



