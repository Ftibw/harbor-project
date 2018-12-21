package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.annotation.VisitLogger;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.conf.FileDir;
import com.whxm.harbor.conf.FtpConfig;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.service.ShopService;
import com.whxm.harbor.service.ShopVisitService;
import com.whxm.harbor.utils.BizShop;
import com.whxm.harbor.utils.FileUtils;
import com.whxm.harbor.ftp.FtpSession;
import com.whxm.harbor.vo.BizShopVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Api(description = "商铺服务")
@RestController
@RequestMapping("/shop")
@MyApiResponses
public class ShopController {

    private final Logger logger = LoggerFactory.getLogger(ShopController.class);

    @Autowired
    private ShopVisitService shopVisitService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private FileDir fileDir;

    @ApiOperation(value = "根据业态/楼层/商铺名称信息获取店铺列表")
    @PostMapping(value = "/shops")
    public Map<String, Object> getBizShops(
            @ApiParam(name = "floor", value = "楼层ID")
                    Integer floor,
            @ApiParam(name = "type", value = "业态ID")
                    Integer type,
            @ApiParam(name = "initial", value = "商铺名称大写首字母")
                    String initial
    ) {
        ResultMap<String, Object> ret = new ResultMap<>(2);

        try {
            List<BizShopVo> list = shopService.getBizShopListOptional(
                    new ResultMap<String, Object>(3)
                            .build("floorId", floor)
                            .build("bizFormatId", type)
                            .build("initial", Objects.nonNull(initial) ? initial.toLowerCase() : initial)
            );

            ret.build("data", list);

            ret = list.isEmpty() ? ret.build("success", false) : ret.build("success", true);

        } catch (Exception e) {

            logger.error("floorId:{},bizFormatId:{},initial:{}---店铺列表 获取报错", floor, type, initial, e);

            ret.build("data", new byte[]{}).build("success", false);
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

    @VisitLogger
    @ApiOperation(value = "访问商铺")
    @PostMapping("/visit")
    public Map<String, Object> updateShopVisit(
            @ApiParam(name = "no", value = "商铺编号")
            @RequestParam("no") String shopNumber) {

        Assert.notNull(shopNumber, "商铺编号为空");

        return shopVisitService.updateShopVisit(shopNumber);
    }

    @ApiOperation(value = "获取商铺访问数据列表")
    @GetMapping("/visits")
    public Result getShopVisitList(PageQO<BizShop> pageQO, BizShop condition) {

        Result ret = null;

        try {
            pageQO.setCondition(condition);

            PageVO<ShopVisit> pageVO = shopVisitService.getShopVisitList(pageQO);

            ret = new Result(pageVO);

        } catch (Exception e) {

            logger.error("商铺访问数据列表 获取错误", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "商铺访问数据列表 获取错误", pageQO);
        }

        return ret;
    }
    //=========================以上为对外提供的API=================================

    @ApiOperation("上传商铺logo")
    @PostMapping("/logo")
    public Result uploadLogo(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

        return FileUtils.upload(file, request);
    }

    @ApiOperation(value = "上传商铺图片", notes = "表单控件中name属性的值必须为file")
    @PostMapping("/pictures")
    public Result uploadPicture(HttpServletRequest request) {

        Result ret = null;

        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");

        ArrayList<Object> retList = new ArrayList<>();

        files.forEach(file -> {
            try {
                Map<String, Object> map = new HashMap<String, Object>(6);

                FileUtils.upload(file, request, map);

                retList.add(map);

            } catch (Exception e) {

                logger.error("文件" + file.getOriginalFilename() + "上传 发生错误", e);
            }
        });

        ret = new Result(retList);

        return ret;
    }

    @ApiOperation("根据商铺ID获取商铺信息")
    @GetMapping("/shop")
    public Result getBizShop(
            @ApiParam(name = "id", value = "商铺的ID", required = true)
            @RequestParam("id") String shopId
    ) {
        Result ret = null;

        BizShopVo shop = null;
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

    @Autowired
    private FtpConfig ftpConfig;

    @ApiOperation(value = "添加商铺(需授权)",
            notes = "pictureList中元素为map,map有3个key," +
                    "shopPictureName(商铺图片名称),shopPicturePath(商铺图片路径),shopPictureSize(商铺图片大小)")
    @PostMapping("/bizShop")
    public Result addBizShop(@RequestBody ShopParam param, HttpServletRequest request) {

        Assert.notNull(param, "参数不能为空");

        BizShop bizShop = param.bizShop;

        Assert.notNull(bizShop, "商铺数据不能为空");

        List<Map<String, Object>> pictureList = param.pictureList;

        //---------------------------------------------------------------------------
        FtpSession ftpSession = ftpConfig.openSession(true);

        bizShop.setShopLogoPath(ftpSession.clearLocalFileAfterUpload(bizShop.getShopLogoPath(), fileDir.getShopLogoDir(), request));

        if (null != pictureList
                && !pictureList.isEmpty()
                && !pictureList.get(0).isEmpty()) {

            pictureList.forEach(item ->
                    item.put("shopPicturePath", ftpSession.clearLocalFileAfterUpload(String.valueOf(item.get("shopPicturePath")), fileDir.getShopPictureDir(), request)));
        }
        ftpConfig.closeSession(ftpSession);
        //---------------------------------------------------------------------------

        Result ret = null;

        try {
            ret = shopService.addBizShop(bizShop, pictureList);

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