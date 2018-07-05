package com.whxm.harbor;

import com.whxm.harbor.service.ShopService;
import com.whxm.harbor.utils.BizShop;
import com.whxm.harbor.utils.ExcelUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
@MapperScan("com.whxm.harbor.mapper")
@RestController
public class HarborWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(HarborWebApplication.class, args);
    }

    @Autowired
    private ShopService shopService;

    @PostMapping("/excel")
    public String uploadExcel(MultipartFile file) {

        try {

            File uploadedFile = new File("C:\\Users\\Public\\Documents", file.getOriginalFilename());

            file.transferTo(uploadedFile);

            List<BizShop> shops = ExcelUtils.importData(uploadedFile, BizShop.class);

            System.out.println(shops.get(shops.size() - 1).getShopName());

            int affectRow = shopService.batchInsert(shops);

            System.out.println(affectRow);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "OK";
    }
}
