package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.conf.FileDir;
import com.whxm.harbor.conf.UrlConfig;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.service.ShopService;
import com.whxm.harbor.utils.FileUtils;
import com.whxm.harbor.vo.BizShopVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "商铺服务")
@RestController
@RequestMapping("/shop")
@MyApiResponses
public class ShopController {

    private static final Logger logger = LoggerFactory.getLogger(ShopController.class);

    @Autowired
    private ShopService shopService;

    @Autowired
    private UrlConfig urlConfig;

    @Autowired
    private FileDir fileDir;

    @ApiOperation(value = "根据店铺名称首字母获取商铺列表")
    @PostMapping(value = "/shopsByName", consumes = "application/x-www-form-urlencoded")
    public Map<String, Object> getBizShopsByName(
            @ApiParam(name = "initial", value = "商铺名称首字母", required = true)
                    String initial) {
//        shopService
        return null;
    }


    @ApiOperation(value = "根据业态和楼层获取店铺列表")
    @PostMapping(value = "/shops", consumes = "application/x-www-form-urlencoded")
    public Map<String, Object> getBizShops(
            @ApiParam(name = "floor", value = "楼层ID")
                    Integer floor,
            @ApiParam(name = "type", value = "业态ID")
                    Integer type,
            @ApiParam(name = "initial", value = "商铺名称首字母")
                    String initial
    ) {

        ResultMap<String, Object> ret = new ResultMap<>(2);

        try {
            List<BizShopVo> list = shopService.getBizShopListOptional(
                    new ResultMap<String, Object>(3)
                            .build("floorId", floor)
                            .build("bizFormatId", type)
                            .build("initial", initial)
            );

            ret.build("data", list);

            ret = list.isEmpty() ? ret.build("success", false) : ret.build("success", true);

        } catch (Exception e) {

            logger.error("楼层列表 获取报错", e);

            ret.build("data", new Object[]{}).build("success", false);
        }

        return ret;
    }

    @ApiOperation(value = "根据商铺ID获取商铺图片")
    @GetMapping("/shopPictures/{ID}")
    public Result getPicturesById(@PathVariable("ID") String bizShopId) {

        Result ret;

        try {
            List<ShopPicture> shopPictures = shopService.getShopPicturesById(bizShopId);

            ret = new Result(shopPictures);

        } catch (Exception e) {
            logger.error("ID为{}的商铺图片 获取报错", bizShopId, e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "ID为" + bizShopId + "的商铺图片 获取报错", Constant.NO_DATA);
        }

        return ret;
    }

    //=========================以上为对外提供的API=================================

    @ApiOperation("上传商铺logo")
    @PostMapping("/logo")
    public Result uploadLogo(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

        return FileUtils.upload(file, request, fileDir.getShopLogoDir());
    }

    @ApiOperation(value = "上传商铺图片", notes = "表单控件中name属性的值必须为file")
    @PostMapping("/pictures")
    public Result uploadPicture(HttpServletRequest request) {

        Result ret = null;

        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");

        ArrayList<Object> retList = new ArrayList<>();

        files.forEach(file -> {
            try {
                Map<String, Object> map = new HashMap<String, Object>(4);

                FileUtils.upload(file, request, fileDir.getShopPictureDir(), map);

                //InputStream is = file.getInputStream();
                //判断图片横屏还是竖屏
                /*URL url = new URL(urlConfig.getUrlPrefix() + map.get("filePath"));

                URLConnection con = url.openConnection();
                //不超时
                con.setConnectTimeout(0);
                //不允许缓存
                con.setUseCaches(false);

                con.setDefaultUseCaches(false);

                InputStream pis = con.getInputStream();

                BufferedImage bufferedImg = ImageIO.read(pis);

                int imgWidth = bufferedImg.getWidth();

                int imgHeight = bufferedImg.getHeight();

                String type = imgWidth > imgHeight ? "横屏" : "竖屏";

                System.out.println(type);*/

                retList.add(map);

            } catch (Exception e) {

                logger.error("文件" + file.getOriginalFilename() + "上传 发生错误", e);
            }
        });

        ret = new Result(retList);

        return ret;
    }

    //==========================以下均被拦截============================

    @ApiOperation("获取商铺列表(需授权)")
    @GetMapping("/bizShops")
    public Result getBizShops(PageQO<BizShop> pageQO, BizShop condition) {
        Result ret = null;

        try {
            pageQO.setCondition(condition);

            PageVO<BizShop> pageVO = shopService.getBizShopList(pageQO);

            ret = new Result(pageVO);

        } catch (Exception e) {

            logger.error("商铺列表 获取错误", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "商铺列表 获取错误", pageQO);
        }

        return ret;
    }

    @ApiOperation("获取商铺(需授权)")
    @GetMapping("/bizShop/{ID}")
    public Result getBizShop(
            @ApiParam(name = "ID", value = "商铺的ID", required = true)
            @PathVariable("ID") String shopId
    ) {
        Result ret = null;

        BizShop shop = null;
        try {
            shop = shopService.getBizShop(shopId);

            ret = new Result(shop);

        } catch (Exception e) {

            logger.error("ID为{}的商铺数据 获取报错", shopId, e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "ID为" + shopId + "的商铺数据 获取报错", Constant.NO_DATA);
        }

        return ret;
    }

    @ApiOperation("修改商铺(需授权)")
    @PutMapping("/bizShop")
    public Result updateBizShop(@RequestBody BizShop bizShop) {

        Result ret = null;
        try {
            ret = shopService.updateBizShop(bizShop);

        } catch (Exception e) {

            logger.error("商铺数据 修改报错", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "商铺数据 修改报错", bizShop);
        }
        return ret;
    }

    @ApiOperation("启用/停用商铺(需授权)")
    @DeleteMapping(value = "/bizShop")
    public Result triggerBizShop(
            @ApiParam(name = "ID", value = "商铺的ID", required = true)
                    String id
    ) {
        Result ret = null;
        try {
            ret = shopService.triggerBizShop(id);

        } catch (Exception e) {

            logger.error("ID为{}的商铺 状态(启用/停用)变更报错", id);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "ID为" + id + "的商铺 状态切换报错", Constant.NO_DATA);
        }

        return ret;
    }

    @ApiOperation(value = "添加商铺(需授权)",
            notes = "pictureList中元素为map,map有3个key," +
                    "shopPictureName(商铺图片名称),shopPicturePath(商铺图片路径),shopPictureSize(商铺图片大小)")
    @PostMapping("/bizShop")
    public Result addBizShop(@RequestBody ShopParam param) {

        Result ret = null;

        try {
            ret = shopService.addBizShop(param.bizShop, param.pictureList);

        } catch (Exception e) {

            logger.error("商铺数据 添加报错", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "商铺数据 添加报错", param);
        }

        return ret;
    }
}

//商铺+商铺图片数据封装
class ShopParam {

    public BizShop bizShop;

    public List<Map<String, Object>> pictureList;
}