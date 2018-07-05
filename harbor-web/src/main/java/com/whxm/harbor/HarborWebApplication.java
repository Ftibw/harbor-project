package com.whxm.harbor;

import com.whxm.harbor.utils.ExcelUtils;
import com.whxm.harbor.utils.FileUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
@MapperScan("com.whxm.harbor.mapper")
@RestController
public class HarborWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(HarborWebApplication.class, args);
    }

    @PostMapping("/excel")
    public String uploadExcel(MultipartFile file) {

        try {

            File uploadedFile = new File("C:\\Users\\Public\\Documents", file.getOriginalFilename());

            file.transferTo(uploadedFile);

            ExcelUtils.importData(uploadedFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "OK";
    }
}
